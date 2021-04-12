package com.c5inco.idea.apps.colorpicker

import androidx.compose.ui.graphics.Color
import java.awt.Color as AWTColor

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