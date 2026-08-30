package com.example.player

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.Virtualizer
import android.net.Uri
import android.os.Build
import android.util.Log
import com.example.data.model.AudioTrack as TrackModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

enum class RepeatMode {
    OFF, ALL, ONE
}

data class EqBand(
    val index: Int,
    val frequencyLabel: String,
    val centerFreqHz: Int,
    val levelMb: Int // -1500 to +1500 millibels
)

class AudioEngine(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var mediaPlayer: MediaPlayer? = null
    private var syntheticAudioTrack: AudioTrack? = null
    private var syntheticThread: Thread? = null
    @Volatile private var isSyntheticPlaying = false
    @Volatile private var syntheticPositionMs = 0L

    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentTrack = MutableStateFlow<TrackModel?>(null)
    val currentTrack: StateFlow<TrackModel?> = _currentTrack.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(1L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _repeatMode = MutableStateFlow(RepeatMode.ALL)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()

    private val _isShuffle = MutableStateFlow(false)
    val isShuffle: StateFlow<Boolean> = _isShuffle.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _volume = MutableStateFlow(0.85f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    // 32-band visualizer amplitude stream (0.0 to 1.0)
    private val _visualizerAmplitudes = MutableStateFlow(List(32) { 0.1f })
    val visualizerAmplitudes: StateFlow<List<Float>> = _visualizerAmplitudes.asStateFlow()

    // Equalizer state
    private val _eqBands = MutableStateFlow(
        listOf(
            EqBand(0, "60Hz", 60, 0),
            EqBand(1, "230Hz", 230, 0),
            EqBand(2, "910Hz", 910, 0),
            EqBand(3, "3.6kHz", 3600, 0),
            EqBand(4, "14kHz", 14000, 0)
        )
    )
    val eqBands: StateFlow<List<EqBand>> = _eqBands.asStateFlow()

    private val _bassBoostStrength = MutableStateFlow(0) // 0 - 1000
    val bassBoostStrength: StateFlow<Int> = _bassBoostStrength.asStateFlow()

    private val _isVirtualizerEnabled = MutableStateFlow(true)
    val isVirtualizerEnabled: StateFlow<Boolean> = _isVirtualizerEnabled.asStateFlow()

    private var onTrackCompletedCallback: (() -> Unit)? = null
    private var progressTickerJob: Job? = null

    init {
        startProgressTicker()
    }

    fun setOnTrackCompletedListener(callback: () -> Unit) {
        this.onTrackCompletedCallback = callback
    }

    fun playTrack(track: TrackModel) {
        stopCurrentPlayback()
        _currentTrack.value = track
        _durationMs.value = if (track.durationMs > 0) track.durationMs else 180000L
        _currentPositionMs.value = 0L

        if (track.uri.startsWith("aura://")) {
            playSyntheticHiRes(track)
        } else {
            playLocalUri(track)
        }
    }

    private fun playLocalUri(track: TrackModel) {
        try {
            val mp = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(context, Uri.parse(track.uri))
                prepare()
                setVolume(_volume.value, _volume.value)
                applySpeed(_playbackSpeed.value, this)
                setOnCompletionListener {
                    handleTrackCompletion()
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("AudioEngine", "MediaPlayer error $what, $extra")
                    false
                }
                start()
            }
            mediaPlayer = mp
            _durationMs.value = mp.duration.toLong().coerceAtLeast(1000L)
            _isPlaying.value = true
            setupAudioEffects(mp.audioSessionId)
        } catch (e: Exception) {
            Log.e("AudioEngine", "Error playing local URI: ${track.uri}", e)
            // Fallback to rich synthetic stream so user can still listen
            playSyntheticHiRes(track)
        }
    }

    private fun playSyntheticHiRes(track: TrackModel) {
        val sampleRate = 48000
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT
        ) * 2

        try {
            val trackAudio = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            syntheticAudioTrack = trackAudio
            trackAudio.setVolume(_volume.value)
            trackAudio.play()
            isSyntheticPlaying = true
            _isPlaying.value = true
            setupAudioEffects(trackAudio.audioSessionId)

            // Select chord frequencies based on track id or title hash
            val seed = (track.title.hashCode() and 0xFFFF)
            val baseFreq = when (track.coverPreset % 4) {
                0 -> 220.0 // A3 (Lush Ambient)
                1 -> 261.63 // C4 (Electronic Pulse)
                2 -> 196.0 // G3 (Classical Horizon)
                else -> 174.61 // F3 (Cyberpunk)
            }
            val chordOffsets = listOf(1.0, 1.25, 1.5, 1.875, 2.0)

            syntheticThread = Thread {
                val numSamples = 2048
                val buffer = ShortArray(numSamples * 2) // Stereo
                var sampleIndex = (syntheticPositionMs * sampleRate / 1000L).toDouble()

                while (isSyntheticPlaying) {
                    val speed = _playbackSpeed.value.toDouble()
                    for (i in 0 until numSamples) {
                        val t = sampleIndex / sampleRate
                        // Rich generative synth with multiple harmonic layers, chorus, and subtle shimmer
                        var left = 0.0
                        var right = 0.0

                        // Fundamental and harmonics
                        for ((idx, mult) in chordOffsets.withIndex()) {
                            val f = baseFreq * mult
                            val lfo = 1.0 + 0.15 * sin(2 * PI * 0.2 * t + idx)
                            val wave1 = sin(2 * PI * f * t * lfo)
                            val wave2 = sin(2 * PI * (f * 1.003) * t) // Stereo detune
                            val shimmer = 0.25 * sin(2 * PI * (f * 2.0) * t)

                            // Bass band influence
                            val bassBoostFactor = 1.0 + (_bassBoostStrength.value / 1000.0) * (if (idx == 0) 1.5 else 0.0)
                            val amp = (0.28 / chordOffsets.size) * bassBoostFactor

                            left += (wave1 + shimmer * 0.5) * amp
                            right += (wave2 + shimmer * 0.7) * amp
                        }

                        // Soft clipping / saturation
                        left = left.coerceIn(-0.95, 0.95)
                        right = right.coerceIn(-0.95, 0.95)

                        val sLeft = (left * Short.MAX_VALUE * _volume.value).toInt().toShort()
                        val sRight = (right * Short.MAX_VALUE * _volume.value).toInt().toShort()

                        buffer[i * 2] = sLeft
                        buffer[i * 2 + 1] = sRight

                        sampleIndex += speed
                    }

                    syntheticAudioTrack?.write(buffer, 0, buffer.size)
                    syntheticPositionMs = (sampleIndex * 1000L / sampleRate).toLong()

                    if (syntheticPositionMs >= _durationMs.value) {
                        scope.launch { handleTrackCompletion() }
                        break
                    }
                }
            }.apply {
                priority = Thread.MAX_PRIORITY
                start()
            }
        } catch (e: Exception) {
            Log.e("AudioEngine", "Synthetic AudioTrack initialization failed", e)
        }
    }

    private fun setupAudioEffects(sessionId: Int) {
        try {
            if (sessionId != AudioManager.ERROR) {
                equalizer?.release()
                bassBoost?.release()
                virtualizer?.release()

                equalizer = Equalizer(0, sessionId).apply {
                    enabled = true
                }
                bassBoost = BassBoost(0, sessionId).apply {
                    enabled = true
                    setStrength(_bassBoostStrength.value.toShort())
                }
                virtualizer = Virtualizer(0, sessionId).apply {
                    enabled = _isVirtualizerEnabled.value
                    setStrength(800.toShort())
                }
            }
        } catch (e: Exception) {
            Log.w("AudioEngine", "Audio effects not supported on this session", e)
        }
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            resume()
        }
    }

    fun pause() {
        _isPlaying.value = false
        mediaPlayer?.let {
            if (it.isPlaying) it.pause()
        }
        if (isSyntheticPlaying) {
            isSyntheticPlaying = false
            syntheticAudioTrack?.pause()
        }
    }

    fun resume() {
        val track = _currentTrack.value ?: return
        _isPlaying.value = true
        mediaPlayer?.let {
            it.start()
            return
        }
        if (syntheticAudioTrack != null && !isSyntheticPlaying) {
            isSyntheticPlaying = true
            syntheticAudioTrack?.play()
            // Restart synthetic generation loop
            playSyntheticHiRes(track)
        } else {
            playTrack(track)
        }
    }

    fun seekTo(positionMs: Long) {
        val targetMs = positionMs.coerceIn(0L, _durationMs.value)
        _currentPositionMs.value = targetMs
        mediaPlayer?.seekTo(targetMs.toInt())
        syntheticPositionMs = targetMs
    }

    fun setVolume(vol: Float) {
        val clamped = vol.coerceIn(0f, 1f)
        _volume.value = clamped
        mediaPlayer?.setVolume(clamped, clamped)
        syntheticAudioTrack?.setVolume(clamped)
    }

    fun setSpeed(speed: Float) {
        _playbackSpeed.value = speed
        mediaPlayer?.let { applySpeed(speed, it) }
    }

    private fun applySpeed(speed: Float, mp: MediaPlayer) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val params = mp.playbackParams
                params.speed = speed
                mp.playbackParams = params
            } catch (e: Exception) {
                Log.w("AudioEngine", "Failed to set playback speed", e)
            }
        }
    }

    fun cycleRepeatMode() {
        _repeatMode.value = when (_repeatMode.value) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
    }

    fun toggleShuffle() {
        _isShuffle.value = !_isShuffle.value
    }

    fun setEqBandLevel(bandIndex: Int, levelMb: Int) {
        val bands = _eqBands.value.toMutableList()
        if (bandIndex in bands.indices) {
            bands[bandIndex] = bands[bandIndex].copy(levelMb = levelMb)
            _eqBands.value = bands
            try {
                equalizer?.setBandLevel(bandIndex.toShort(), levelMb.toShort())
            } catch (e: Exception) {
                // Ignore if not supported
            }
        }
    }

    fun setBassBoost(strength: Int) {
        _bassBoostStrength.value = strength
        try {
            bassBoost?.setStrength(strength.toShort())
        } catch (e: Exception) {
            // Ignore
        }
    }

    fun toggleVirtualizer() {
        _isVirtualizerEnabled.value = !_isVirtualizerEnabled.value
        try {
            virtualizer?.enabled = _isVirtualizerEnabled.value
        } catch (e: Exception) {
            // Ignore
        }
    }

    fun applyEqPreset(presetName: String) {
        when (presetName) {
            "Acoustic Hi-Res" -> {
                setEqBandsDirect(listOf(300, 150, 0, 350, 600))
                setBassBoost(200)
            }
            "Electronic Pulse" -> {
                setEqBandsDirect(listOf(800, 400, -100, 500, 700))
                setBassBoost(750)
            }
            "Bass Heavy" -> {
                setEqBandsDirect(listOf(1000, 600, 200, -200, 0))
                setBassBoost(950)
            }
            "Vocal Clarity" -> {
                setEqBandsDirect(listOf(-300, 100, 700, 600, 200))
                setBassBoost(100)
            }
            "Studio Flat" -> {
                setEqBandsDirect(listOf(0, 0, 0, 0, 0))
                setBassBoost(0)
            }
            "Concert Hall" -> {
                setEqBandsDirect(listOf(500, 200, 300, 400, 800))
                setBassBoost(400)
            }
        }
    }

    private fun setEqBandsDirect(levelsMb: List<Int>) {
        val bands = _eqBands.value.toMutableList()
        for (i in bands.indices) {
            if (i < levelsMb.size) {
                bands[i] = bands[i].copy(levelMb = levelsMb[i])
                try {
                    equalizer?.setBandLevel(i.toShort(), levelsMb[i].toShort())
                } catch (e: Exception) {}
            }
        }
        _eqBands.value = bands
    }

    private fun handleTrackCompletion() {
        if (_repeatMode.value == RepeatMode.ONE) {
            seekTo(0L)
            resume()
        } else {
            onTrackCompletedCallback?.invoke()
        }
    }

    private fun stopCurrentPlayback() {
        isSyntheticPlaying = false
        syntheticAudioTrack?.apply {
            try {
                stop()
                release()
            } catch (e: Exception) {}
        }
        syntheticAudioTrack = null
        syntheticThread = null

        mediaPlayer?.apply {
            try {
                if (isPlaying) stop()
                release()
            } catch (e: Exception) {}
        }
        mediaPlayer = null
    }

    private fun startProgressTicker() {
        progressTickerJob?.cancel()
        progressTickerJob = scope.launch {
            var phase = 0.0
            while (isActive) {
                if (_isPlaying.value) {
                    if (mediaPlayer != null) {
                        try {
                            mediaPlayer?.let {
                                if (it.isPlaying) {
                                    _currentPositionMs.value = it.currentPosition.toLong()
                                }
                            }
                        } catch (e: Exception) {}
                    } else if (isSyntheticPlaying) {
                        _currentPositionMs.value = syntheticPositionMs
                    }

                    // Generate dynamic fluid visualizer bars
                    phase += 0.18
                    val amps = MutableList(32) { i ->
                        val base = sin(phase * 1.5 + i * 0.35).toFloat() * 0.4f + 0.55f
                        val harmonic = sin(phase * 3.2 - i * 0.2).toFloat() * 0.25f
                        val bassExtra = if (i < 8) (_bassBoostStrength.value / 2500f) else 0f
                        (base + harmonic + bassExtra).coerceIn(0.08f, 0.98f)
                    }
                    _visualizerAmplitudes.value = amps
                } else {
                    // Decay visualizer bars gently when paused
                    val current = _visualizerAmplitudes.value
                    _visualizerAmplitudes.value = current.map { (it * 0.92f).coerceAtLeast(0.05f) }
                }
                delay(33) // ~30 fps updates for fluid animations
            }
        }
    }

    fun release() {
        progressTickerJob?.cancel()
        stopCurrentPlayback()
        equalizer?.release()
        bassBoost?.release()
        virtualizer?.release()
    }
}
