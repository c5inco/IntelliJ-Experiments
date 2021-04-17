package com.c5inco.idea.apps.lafdefaults

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.c5inco.idea.apps.colorpicker.ColorPicker
import com.c5inco.idea.utils.asComposeColor
import com.c5inco.idea.utils.toHex
import com.intellij.ide.ui.LafManager
import com.intellij.openapi.application.ApplicationManager
import javax.swing.UIManager
import javax.swing.plaf.ColorUIResource
import kotlin.math.floor
import java.awt.Color as AWTColor

@ExperimentalFoundationApi
@Composable
fun LafDefaults(
    isDarkTheme: Boolean
) {
    var defaults by remember(isDarkTheme) { mutableStateOf(getLafDefaultsColors()) }

    defaults?.let {
        var filter by remember { mutableStateOf("") }
        var groupByColor by remember { mutableStateOf(false )}
        var onlyAlpha by remember { mutableStateOf(false )}

        val filteredResults = defaults.filter { (k, v) ->
            var include = k.toLowerCase().contains(filter.toLowerCase())
            if (onlyAlpha) {
                include && v.alpha < 255
            } else {
                include
            }
        }

        Column(
            Modifier.padding(8.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = groupByColor,
                        onCheckedChange = {
                            groupByColor = it
                        }
                    )
                    Text(
                        "Group by color"
                    )
                }
                Spacer(Modifier.width(20.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = onlyAlpha,
                        onCheckedChange = {
                            onlyAlpha = it
                        }
                    )
                    Text(
                        "Only alpha"
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = filter,
                onValueChange = {
                    filter = it
                },
                modifier = Modifier.fillMaxWidth()
            )

            if(filteredResults.isNotEmpty()) {
                Box {
                    val state = rememberLazyListState()

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = state,
                        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 4.dp)
                    ) {
                        if (groupByColor) {
                            val groupedColors = transformToGroupedColorMap(filteredResults)

                            groupedColors.forEach { (key, values) ->
                                stickyHeader(key) {
                                    Row(
                                        modifier = Modifier
                                            .padding(horizontal = 8.dp)
                                            .fillMaxWidth()
                                            .height(32.dp)
                                            .background(Color.Cyan),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(generateRgbaString(key))
                                        Text("[${values.size}]")
                                    }
                                }

                                items(values) { colorResource ->
                                    ColorResourceRow(colorResource.first, colorResource.second)
                                }
                            }
                        } else {
                            filteredResults.forEach { (key, color) ->
                                item {
                                    ColorResourceRow(key, color)
                                }
                            }
                        }
                    }

                    VerticalScrollbar(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight()
                            .padding(horizontal = 0.dp, vertical = 4.dp),
                        adapter = rememberScrollbarAdapter(
                            scrollState = state,
                            itemCount = if (groupByColor) calculateAllItems(filteredResults) else filteredResults.size,
                            averageItemSize = 32.dp
                        )
                    )
                }
            } else {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column {
                        Text("No colors found.", color = LocalContentColor.current.copy(alpha = ContentAlpha.disabled))
                        Spacer(Modifier.fillMaxHeight(0.4f))
                    }
                }
            }
        }
    }
}

private fun calculateAllItems(filteredResults: Map<String, AWTColor>): Int {
    val groupedColors = transformToGroupedColorMap(filteredResults)
    var count = groupedColors.size

    groupedColors.forEach { (key, values) ->
        count += values.size
    }

    return count
}

private fun transformToGroupedColorMap(
    filteredResults: Map<String, AWTColor>
): MutableMap<AWTColor, MutableList<Pair<String, AWTColor>>> {
    val flatMap = filteredResults.flatMap { (k, v) ->
        listOf(Pair(k, v))
    }

    val groupedColors = mutableMapOf<AWTColor, MutableList<Pair<String, AWTColor>>>()

    flatMap.forEach {
        val c = it.second
        if (groupedColors.containsKey(c)) {
            val current = groupedColors.getOrDefault(c, mutableListOf())
            current.add(it)
        } else {
            groupedColors.set(c, mutableListOf(it))
        }
    }
    return groupedColors
}

@Composable
private fun ColorResourceRow(key: String, color: AWTColor) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(32.dp)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "$key",
            color = MaterialTheme.colors.onSurface
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                generateRgbaString(color)
            )

            var showColorPicker by remember { mutableStateOf(false) }
            Box {
                Column(
                    Modifier
                        .clickable { showColorPicker = true }
                        .size(24.dp)
                        .background(color.asComposeColor)
                        .border(1.dp, Color.Black.copy(alpha = 0.1f))
                ) { }
                DropdownMenu(
                    expanded = showColorPicker,
                    offset = DpOffset(x = 32.dp, y = (-24).dp),
                    onDismissRequest = {
                        showColorPicker = false
                    }
                ) {
                    ColorPicker(
                        initialColor = color,
                        onClose = { showColorPicker = false },
                        onColorChange = {
                            updateColor(key, ColorUIResource(AWTColor(it.red, it.green, it.blue, it.alpha)))
                        }
                    )
                }
            }
        }
    }
}

fun generateRgbaString(color: AWTColor): String {
    if (color.alpha < 255) {
        val calcAlpha = color.alpha.toDouble() / 255.0
        return "rgba(${color.red},${color.green},${color.blue},${floor(calcAlpha * 100) / 100}), #${toHex(color, true)}"
    }
    return "rgb(${color.red},${color.green},${color.blue}), #${toHex(color, false)}"
}

private fun getLafDefaultsColors(): Map<String, AWTColor> {
    var defaults = UIManager.getDefaults().toMap()

    defaults = defaults.filter { (_, v) ->
        v is AWTColor
    }
    val sortedDefaults = mutableMapOf<String, AWTColor>()
    defaults.map {
        sortedDefaults[it.key!! as String] = it.value as AWTColor
    }

    return sortedDefaults.toSortedMap()
}

private fun updateColor(key: String, next: ColorUIResource) {
    UIManager.getDefaults().remove(key)
    UIManager.getDefaults()[key] = next

    ApplicationManager.getApplication().invokeLater {
        LafManager.getInstance().updateUI()
        LafManager.getInstance().repaintUI()
    }
}