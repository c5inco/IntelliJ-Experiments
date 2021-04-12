package com.c5inco.idea.plugin

import androidx.compose.desktop.ComposePanel
import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.c5inco.idea.RotatingGlobe
import com.c5inco.idea.plugin.intellij.SwingColors
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.ComponentPopupBuilder
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.util.ui.JBUI
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.UIManager


@ExperimentalFoundationApi
class MyToolWindowFactory : ToolWindowFactory {
    /**
     * Create the tool window content.
     *
     * @param project    current project
     * @param toolWindow current tool window
     */
    private lateinit var _project: Project

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        _project = project
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory.createContent(createCenterPanel(), "", false)
        toolWindow.contentManager.addContent(content)
    }

    fun createCenterPanel(): JComponent {
        return ComposePanel().apply {
            setContent {
                Thread.currentThread().contextClassLoader = PluginAction::class.java.classLoader
                val bgColor = JBUI.CurrentTheme.NewClassDialog.panelBackground()
                val swingColors = SwingColors()
                val appColors = if (swingColors.isDarcula) darkColors() else lightColors()

                DesktopMaterialTheme(colors =
                    appColors.copy(
                        background = swingColors.background,
                        onBackground = swingColors.onBackground,
                        surface = swingColors.surface,
                        onSurface = swingColors.onSurface
                    )
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color(bgColor.red, bgColor.green, bgColor.blue)
                    ) {
                        Column {
                            Button(onClick = { showPopup() }) {
                                Text("popup")
                            }
                            Spacer(Modifier.height(32.dp))
                            Button(onClick = { showPopup() }) {
                                Text("color picker")
                            }
                        }
                        LafDefaults(swingColors.isDarcula)
                    }
                }
            }
        }
    }

    @ExperimentalFoundationApi
    @Composable
    private fun LafDefaults(isDarkTheme: Boolean) {
        var defaults by remember(isDarkTheme) { mutableStateOf(getLafDefaultsColors()) }

        fun colorToColor(color: java.awt.Color): Color {
            return Color(color.red, color.green, color.blue)
        }

        defaults?.let {
            Box {
                val state = rememberLazyListState()

                LazyColumn(
                    state = state
                ) {
                    defaults.forEach { (key, color) ->
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
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(
                        scrollState = state,
                        itemCount = defaults.size,
                        averageItemSize = 32.dp
                    )
                )
            }
        }
    }

    private fun getLafDefaultsColors(): Map<String, java.awt.Color> {
        var defaults = UIManager.getDefaults().toMap()

        defaults = defaults.filter { (_, v) ->
            v is java.awt.Color
        }
        val sortedDefaults = mutableMapOf<String, java.awt.Color>()
        defaults.map {
            sortedDefaults[it.key!! as String] = it.value as java.awt.Color
        }

        return sortedDefaults.toSortedMap()
    }

    fun showPopup() {
        var popupBuilder: ComponentPopupBuilder? =
            JBPopupFactory
                .getInstance()
                .createComponentPopupBuilder(
                    createComposeComponent {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            RotatingGlobe(Modifier.fillMaxSize(0.7f))
                        }
                    },
                    null
                )
        var popup: JBPopup = popupBuilder!!.createPopup()
        popup.showCenteredInCurrentWindow(_project)
    }

    fun createComposeComponent(content: @Composable () -> Unit): JComponent {
        return ComposePanel().apply {
            preferredSize = Dimension(150, 300)
            setContent {
                Thread.currentThread().contextClassLoader = PluginAction::class.java.classLoader

                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    content()
                }
            }
        }
    }
}