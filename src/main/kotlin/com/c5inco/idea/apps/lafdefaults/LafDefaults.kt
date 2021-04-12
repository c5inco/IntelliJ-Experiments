package com.c5inco.idea.apps.lafdefaults

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import javax.swing.UIManager
import java.awt.Color as AWTColor

@ExperimentalFoundationApi
@Composable
fun LafDefaults(isDarkTheme: Boolean) {
    var defaults by remember(isDarkTheme) { mutableStateOf(getLafDefaultsColors()) }

    fun colorToColor(color: java.awt.Color): Color {
        return Color(color.red, color.green, color.blue)
    }

    defaults?.let {
        var filter by remember { mutableStateOf("") }
        val filteredResults = defaults.filter { (k, _) ->
            k.toLowerCase().contains(filter.toLowerCase())
        }

        Column {
            OutlinedTextField(
                value = filter,
                onValueChange = {
                    filter = it
                },
                modifier = Modifier.fillMaxWidth()
            )
            Box {
                val state = rememberLazyListState()

                LazyColumn(
                    state = state,
                    contentPadding = PaddingValues(horizontal = 0.dp, vertical = 4.dp)
                ) {
                    filteredResults.forEach { (key, color) ->
                        item {
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
                                Column(
                                    Modifier
                                        .size(24.dp)
                                        .background(colorToColor(color))
                                        .border(1.dp, Color.Black.copy(alpha = 0.1f))
                                ) { }
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
                        itemCount = defaults.size,
                        averageItemSize = 32.dp
                    )
                )
            }
        }
    }
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