package com.c5inco.idea.plugin

import androidx.compose.desktop.ComposePanel
import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.c5inco.idea.apps.colorpicker.ColorPicker
import com.c5inco.idea.apps.lafdefaults.LafDefaults
import com.c5inco.idea.apps.rotatingglobe.RotatingGlobe
import com.c5inco.idea.plugin.intellij.SwingColors
import com.c5inco.idea.utils.WithoutTouchSlop
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.ComponentPopupBuilder
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.util.ui.JBUI
import javax.swing.JComponent


@ExperimentalFoundationApi
class MyToolWindowFactory : ToolWindowFactory, DumbAware {
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
        //val laf = contentFactory.createContent(createLafPanel(), "Laf Defaults", false)
        toolWindow.contentManager.addContent(
            contentFactory.createContent(createComposeComponent { LafDefaults(it) }, "Laf Defaults", false))
        toolWindow.contentManager.addContent(
            contentFactory.createContent(createComposeComponent { RotatingGlobe() }, "Rotating Globe", false))
        toolWindow.contentManager.addContent(
            contentFactory.createContent(
                createComposeComponent {
                    Column(
                        Modifier.size(width = 240.dp, height = 450.dp)
                    ) {
                        ColorPicker()
                    }
                }, "Color Picker", false))
    }

    fun createLafPanel(): JComponent {
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
                            LafDefaults(swingColors.isDarcula)
                        }
                    }
                }
            }
        }
    }

    fun createGlobalPanel(): JComponent {
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
                            RotatingGlobe()
                        }
                    }
                }
            }
        }
    }

    fun showPopup() {
        var popupBuilder: ComponentPopupBuilder? =
            JBPopupFactory
                .getInstance()
                .createComponentPopupBuilder(
                    createComposeComponent {
                        WithoutTouchSlop {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                ColorPicker()
                            }
                        }
                    },
                    null
                )
        var popup: JBPopup = popupBuilder!!.createPopup()
        popup.showCenteredInCurrentWindow(_project)
    }

    fun createComposeComponent(content: @Composable (isDarkTheme: Boolean) -> Unit): JComponent {
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
                        content(swingColors.isDarcula)
                    }
                }
            }
        }
    }
}