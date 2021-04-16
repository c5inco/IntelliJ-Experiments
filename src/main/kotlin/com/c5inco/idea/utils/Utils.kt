package com.c5inco.idea.apps.colorpicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import kotlin.math.*
import java.awt.Color as AWTColor

fun clamp(value: Float, min: Float, max: Float): Float {
    if (min > max) {
        throw IllegalArgumentException("$min>$max")
    }
    return min(max, max(value, min))
}

fun toHex(c: AWTColor, withAlpha: Boolean): String? {
    val R = Integer.toHexString(c.red)
    val G = Integer.toHexString(c.green)
    val B = Integer.toHexString(c.blue)
    val rgbHex = (if (R.length < 2) "0" else "") + R + (if (G.length < 2) "0" else "") + G + (if (B.length < 2) "0" else "") + B
    if (!withAlpha) {
        return rgbHex
    }
    val A = Integer.toHexString(c.alpha)
    return rgbHex + (if (A.length < 2) "0" else "") + A
}

val AWTColor.asComposeColor: Color get() = Color(red, green, blue, alpha)

@Composable
fun WithoutTouchSlop(content: @Composable () -> Unit) {
    fun ViewConfiguration.withoutTouchSlop() = object : ViewConfiguration {
        override val longPressTimeoutMillis get() =
            this@withoutTouchSlop.longPressTimeoutMillis

        override val doubleTapTimeoutMillis get() =
            this@withoutTouchSlop.doubleTapTimeoutMillis

        override val doubleTapMinTimeMillis get() =
            this@withoutTouchSlop.doubleTapMinTimeMillis

        override val touchSlop: Float get() = 0f
    }

    CompositionLocalProvider(
        LocalViewConfiguration provides LocalViewConfiguration.current.withoutTouchSlop()
    ) {
        content()
    }
}