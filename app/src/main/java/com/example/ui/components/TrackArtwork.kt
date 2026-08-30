package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.AudioTrack
import com.example.ui.theme.LocalAuraColors

@Composable
fun TrackArtwork(
    track: AudioTrack?,
    modifier: Modifier = Modifier,
    size: Dp = 50.dp,
    shapeRadius: Dp = 14.dp,
    isRotatingVinyl: Boolean = false,
    isPlaying: Boolean = false,
    showPlayIconWhenActive: Boolean = true
) {
    val auraColors = LocalAuraColors.current
    val infiniteTransition = rememberInfiniteTransition(label = "VinylRotation")

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "DiscSpin"
    )

    val appliedRotation = if (isRotatingVinyl && isPlaying) rotationAngle else 0f
    val shape = if (isRotatingVinyl) CircleShape else RoundedCornerShape(shapeRadius)

    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = if (isRotatingVinyl) 12.dp else 4.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.5f),
                spotColor = auraColors.primaryAccent.copy(alpha = 0.4f)
            )
            .clip(shape)
            .background(Color(0xFF0C0C0E))
            .border(
                width = 1.dp,
                color = if (isRotatingVinyl) auraColors.primaryAccent.copy(alpha = 0.6f) else Color(0x24FFFFFF),
                shape = shape
            )
            .rotate(appliedRotation),
        contentAlignment = Alignment.Center
    ) {
        if (showPlayIconWhenActive && isRotatingVinyl && isPlaying) {
            // Render active play icon inside artwork box (matching Screenshot 3)
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Playing",
                tint = Color.White,
                modifier = Modifier.size(size * 0.52f)
            )
        } else {
            // Stylized CONVX Spiral Monogram
            ConvxMonogramCanvas(
                modifier = Modifier.fillMaxSize(),
                tint = Color(0xFFE8E8EC)
            )
        }

        // Vinyl Center Spindle hole if rotating vinyl mode
        if (isRotatingVinyl && !showPlayIconWhenActive) {
            Box(
                modifier = Modifier
                    .size(size * 0.22f)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.85f))
                    .border(1.5.dp, auraColors.primaryAccent, CircleShape)
            )
        }
    }
}

@Composable
fun ConvxMonogramCanvas(
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .padding(9.dp)
    ) {
        val minDim = size.minDimension
        val strokeWidth = (minDim * 0.082f).coerceAtLeast(2f)
        val outerRadius = (minDim - strokeWidth) / 2f
        val center = this.center

        // Outer smooth circular ring
        drawCircle(
            color = tint,
            radius = outerRadius,
            center = center,
            style = Stroke(width = strokeWidth)
        )

        // Inner swooping spiral loop
        val innerR = outerRadius * 0.60f
        val spiralPath = Path().apply {
            arcTo(
                rect = Rect(
                    center.x - innerR,
                    center.y - innerR,
                    center.x + innerR,
                    center.y + innerR
                ),
                startAngleDegrees = 45f,
                sweepAngleDegrees = 260f,
                forceMoveTo = true
            )
            lineTo(center.x + innerR * 0.25f, center.y + innerR * 0.05f)
        }

        drawPath(
            path = spiralPath,
            color = tint,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Small inner dot / focal point
        drawCircle(
            color = tint,
            radius = strokeWidth * 0.85f,
            center = Offset(center.x + innerR * 0.18f, center.y - innerR * 0.18f)
        )
    }
}
