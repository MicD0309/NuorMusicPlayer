package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalAuraColors

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    backgroundColor: Color = Color(0x1AFFFFFF),
    borderColor: Color = Color(0x38FFFFFF),
    borderWidth: Dp = 1.dp,
    elevation: Dp = 8.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val auraColors = LocalAuraColors.current

    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(color = Color.White.copy(alpha = 0.2f)),
            onClick = onClick
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = Color(0x33000000),
                spotColor = Color(0x22FFFFFF)
            )
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0x24FFFFFF),
                        Color(0x0CFFFFFF),
                        Color(0x14FFFFFF)
                    )
                )
            )
            .border(
                BorderStroke(
                    borderWidth,
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0x66FFFFFF),
                            Color(0x18FFFFFF),
                            Color(0x40FFFFFF)
                        )
                    )
                ),
                shape = shape
            )
            .then(clickModifier)
    ) {
        // Specular Top Gloss Highlight Line
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to Color(0x22FFFFFF),
                        0.35f to Color(0x05FFFFFF),
                        1.0f to Color.Transparent
                    )
                )
        )
        content()
    }
}

@Composable
fun LiquidGlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    shape: Shape = RoundedCornerShape(20.dp),
    isProminent: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(),
        label = "ButtonScale"
    )

    val bgGradient = if (isProminent) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFFFFF),
                Color(0xFFE2E2E8)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0x38FFFFFF),
                Color(0x12FFFFFF)
            )
        )
    }

    val contentColor = if (isProminent) Color(0xFF101012) else Color.White
    val borderBrush = if (isProminent) {
        Brush.linearGradient(listOf(Color(0xFFFFFFFF), Color(0x99FFFFFF)))
    } else {
        Brush.linearGradient(listOf(Color(0x80FFFFFF), Color(0x20FFFFFF), Color(0x55FFFFFF)))
    }

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = if (isProminent) 12.dp else 6.dp,
                shape = shape,
                ambientColor = Color(0x33000000),
                spotColor = if (isProminent) Color(0x40FFFFFF) else Color(0x20FFFFFF)
            )
            .clip(shape)
            .background(bgGradient)
            .border(BorderStroke(1.dp, borderBrush), shape = shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = contentColor.copy(alpha = 0.2f)),
                onClick = onClick
            )
            .padding(horizontal = 18.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        // Specular inner sheen for non-prominent glass
        if (!isProminent) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            0f to Color(0x25FFFFFF),
                            0.5f to Color.Transparent
                        )
                    )
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = contentColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp
            )
        }
    }
}

@Composable
fun LiquidGlassIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    iconSize: Dp = 20.dp,
    shape: Shape = CircleShape,
    isProminent: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(),
        label = "IconBtnScale"
    )

    val bgGradient = if (isProminent) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFFFFF),
                Color(0xFFD8D8E0)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0x35FFFFFF),
                Color(0x10FFFFFF)
            )
        )
    }

    val iconColor = if (isProminent) Color(0xFF101012) else Color.White
    val borderBrush = if (isProminent) {
        Brush.linearGradient(listOf(Color(0xFFFFFFFF), Color(0x88FFFFFF)))
    } else {
        Brush.linearGradient(listOf(Color(0x88FFFFFF), Color(0x20FFFFFF), Color(0x50FFFFFF)))
    }

    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .shadow(
                elevation = if (isProminent) 10.dp else 6.dp,
                shape = shape,
                ambientColor = Color(0x33000000),
                spotColor = Color(0x26FFFFFF)
            )
            .clip(shape)
            .background(bgGradient)
            .border(BorderStroke(1.dp, borderBrush), shape = shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = iconColor.copy(alpha = 0.25f)),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (!isProminent) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            0f to Color(0x28FFFFFF),
                            0.5f to Color.Transparent
                        )
                    )
            )
        }

        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconColor,
            modifier = Modifier.size(iconSize)
        )
    }
}
