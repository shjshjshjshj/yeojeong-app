package com.tts.yeojeong.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tts.yeojeong.R
import com.tts.yeojeong.ui.theme.Disabled
import com.tts.yeojeong.ui.theme.Ink
import com.tts.yeojeong.ui.theme.Primary
import com.tts.yeojeong.ui.theme.Soft
import com.tts.yeojeong.ui.theme.SurfaceWhite
import com.tts.yeojeong.ui.theme.TextSecondary
import com.tts.yeojeong.ui.theme.TextTertiary
import com.tts.yeojeong.ui.theme.lineColor

@Composable
fun YScreenHeader(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {},
    contentColor: Color = TextSecondary
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            YIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "뒤로가기",
                tint = contentColor,
                onClick = onBack
            )
        }
        Text(
            text = title,
            modifier = Modifier
                .padding(start = if (onBack == null) 0.dp else 8.dp)
                .weight(1f),
            style = MaterialTheme.typography.titleMedium,
            color = contentColor
        )
        actions()
    }
}

@Composable
fun YIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color = TextSecondary,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable(
                role = Role.Button,
                onClickLabel = contentDescription,
                onClick = onClick
            )
            .semantics { this.contentDescription = contentDescription; role = Role.Button },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun YGlassIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color(0x66000000))
            .clickable(onClick = onClick)
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
fun YPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .background(if (enabled) Primary else Disabled)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (enabled) Color.White else TextSecondary
        )
    }
}

@Composable
fun YSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    Row(
        modifier = modifier
            .height(48.dp)
            .shadow(2.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceWhite)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp),
            color = Primary
        )
    }
}

@Composable
fun YOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceWhite)
            .border(1.dp, if (enabled) Primary else Disabled, RoundedCornerShape(8.dp))
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (enabled) Primary else TextSecondary
        )
    }
}

@Composable
fun YTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Ink),
        cursorBrush = SolidColor(Primary),
        modifier = modifier.fillMaxWidth(),
        decorationBox = { inner ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceWhite)
                    .border(1.dp, TextSecondary, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                inner()
            }
        }
    )
}

@Composable
fun YSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = Ink),
        cursorBrush = SolidColor(Primary),
        modifier = modifier.fillMaxWidth(),
        decorationBox = { inner ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 40.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Disabled)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary
                        )
                    }
                    inner()
                }
            }
        }
    )
}

@Composable
fun YSectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    action: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge,
            color = Ink
        )
        if (action != null) {
            Text(
                text = action,
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(enabled = onAction != null) { onAction?.invoke() }
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun YLineBadge(line: String, modifier: Modifier = Modifier, large: Boolean = false) {
    val color = lineColor(line)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(if (large) 6.dp else 4.dp))
            .background(color)
            .padding(horizontal = if (large) 8.dp else 5.dp, vertical = if (large) 3.dp else 1.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = line,
            color = Color.White,
            fontSize = if (large) 11.sp else 8.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = if (large) 14.sp else 10.sp
        )
    }
}

@Composable
fun YTag(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Soft)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
    }
}

@Composable
fun YCardPanel(
    modifier: Modifier = Modifier,
    padding: Dp = 20.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(10.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceWhite)
            .padding(padding)
    ) {
        content()
    }
}

@Composable
fun YEmptyState(
    title: String,
    body: String,
    action: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(Soft),
            contentAlignment = Alignment.Center
        ) {
            RouteGlyph(color = Primary, modifier = Modifier.size(38.dp))
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Ink,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        if (action != null && onAction != null) {
            Spacer(Modifier.height(24.dp))
            YSecondaryButton(text = action, onClick = onAction)
        }
    }
}

@Composable
fun RouteGlyph(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.08f
        drawCircle(color = color, radius = w * 0.13f, center = androidx.compose.ui.geometry.Offset(w * 0.2f, h * 0.72f))
        drawCircle(color = color, radius = w * 0.13f, center = androidx.compose.ui.geometry.Offset(w * 0.8f, h * 0.28f))
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(w * 0.3f, h * 0.65f),
            end = androidx.compose.ui.geometry.Offset(w * 0.68f, h * 0.35f),
            strokeWidth = stroke
        )
        drawCircle(color = color, radius = w * 0.07f, center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.5f))
    }
}

@Composable
fun ReportGlyph(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val barWidth = w * 0.18f
        val radius = androidx.compose.ui.geometry.CornerRadius(barWidth / 2f)
        drawRoundRect(
            color = color,
            topLeft = androidx.compose.ui.geometry.Offset(w * 0.16f, h * 0.58f),
            size = androidx.compose.ui.geometry.Size(barWidth, h * 0.28f),
            cornerRadius = radius
        )
        drawRoundRect(
            color = color,
            topLeft = androidx.compose.ui.geometry.Offset(w * 0.41f, h * 0.34f),
            size = androidx.compose.ui.geometry.Size(barWidth, h * 0.52f),
            cornerRadius = radius
        )
        drawRoundRect(
            color = color,
            topLeft = androidx.compose.ui.geometry.Offset(w * 0.66f, h * 0.46f),
            size = androidx.compose.ui.geometry.Size(barWidth, h * 0.40f),
            cornerRadius = radius
        )
    }
}

@Composable
fun YBottomBar(
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceWhite)
            .navigationBarsPadding()
            .height(56.dp)
    ) {
        val items = listOf("home", "route", "report", "my")
        items.forEach { key ->
            val active = selected == key
            val label = when (key) {
                "home" -> "홈"
                "route" -> "루트"
                "report" -> "리포트"
                else -> "마이"
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(role = Role.Tab, onClick = { onSelect(key) })
                    .semantics { contentDescription = label; role = Role.Tab },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (active) 3.dp else 1.dp)
                        .background(if (active) Primary else Disabled)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    val icon = when (key) {
                        "home" -> if (active) R.drawable.ic_home_sel else R.drawable.ic_home_non
                        "route" -> if (active) R.drawable.ic_route_sel else R.drawable.ic_route_non
                        "report" -> if (active) R.drawable.ic_report_sel else R.drawable.ic_report_non
                        else -> if (active) R.drawable.ic_my_sel else R.drawable.ic_my_non
                    }
                    Image(
                        painter = painterResource(icon),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun YWeatherChip(
    location: String,
    temperature: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .heightIn(min = 40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceWhite)
            .border(1.dp, Disabled, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "⌖", style = MaterialTheme.typography.bodyMedium, color = Ink)
        Spacer(Modifier.width(6.dp))
        Text(text = location, style = MaterialTheme.typography.bodyMedium, color = Ink)
        Spacer(Modifier.width(8.dp))
        Text(text = "☀", style = MaterialTheme.typography.titleSmall, color = com.tts.yeojeong.ui.theme.Highlight)
        Spacer(Modifier.width(4.dp))
        Text(
            text = temperature,
            style = MaterialTheme.typography.titleSmall.copy(fontSize = 16.sp),
            color = com.tts.yeojeong.ui.theme.Accent
        )
    }
}

@Composable
fun YLogo(modifier: Modifier = Modifier) {
    androidx.compose.foundation.Image(
        painter = androidx.compose.ui.res.painterResource(id = R.drawable.splash_logo),
        contentDescription = "여정",
        modifier = modifier.clip(CircleShape)
    )
}
