package com.example.data.model

data class AudioTrack(
    val id: String,
    val uri: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val sizeBytes: Long = 0L,
    val mimeType: String = "audio/flac",
    val sampleRate: Int = 96000,
    val bitDepth: Int = 24,
    val bitrateKbps: Int = 4608,
    val formatBadge: String = "Hi-Res LOSSLESS 24-Bit / 96kHz",
    val isHiRes: Boolean = true,
    val genre: String = "Hi-Res Master",
    val year: Int = 2026,
    val trackNumber: Int = 1,
    val discNumber: Int = 1,
    val lyrics: String = "",
    val isFavorite: Boolean = false,
    val rating: Int = 5,
    val coverPreset: Int = 0,
    val dateAdded: Long = System.currentTimeMillis()
)
