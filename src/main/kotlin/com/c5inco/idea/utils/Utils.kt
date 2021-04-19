package com.c5inco.idea.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import com.github.ajalt.colormath.RGB
import kotlin.math.*
import java.awt.Color as AWTColor

fun clampFloat(value: Float, min: Float = 0f, max: Float = 1f): Float {
    if (min > max) {
        throw IllegalArgumentException("$min>$max")
    }
    return min(max, max(value, min))
}

fun clampInt(value: Int, min: Int = 0, max: Int = 100): Int {
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

val Color.asHex: String get() {
    val hex = RGB(red, green, blue, alpha).toHex(withNumberSign = false)
    if (hex.length == 8) {
        return hex.substring(6 until hex.length) + hex.substring(0 until 6)
    }
    return hex
}

@Composable
fun WithoutTouchSlop(content: @Composable () -> Unit) {
    fun ViewConfiguration.withoutTouchSlop() = object : ViewConfiguration {
        override val longPressTimeoutMillis get() =
            this@withoutTouchSlop.longPressTimeoutMillis

        override val doubleTapTimeoutMillis get() =
            this@withoutTouchSlop.doubleTapTimeoutMillis

        override val doubleTapMinTimeMillis get() =
            this@withoutTouchSlop.doubleTapMinTimeMillis

        override val touchSlop: Float get() = 0.01f
    }

    CompositionLocalProvider(
        LocalViewConfiguration provides LocalViewConfiguration.current.withoutTouchSlop()
    ) {
        content()
    }
}