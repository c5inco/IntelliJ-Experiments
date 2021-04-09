package com.c5inco.idea.plugin

import androidx.compose.desktop.ComposePanel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.c5inco.idea.RotatingGlobe
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