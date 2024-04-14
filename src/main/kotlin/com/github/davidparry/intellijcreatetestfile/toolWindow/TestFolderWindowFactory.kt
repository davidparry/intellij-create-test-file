package com.github.davidparry.intellijcreatetestfile.toolWindow

import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import javax.swing.JButton
import com.github.davidparry.intellijcreatetestfile.TestFileBundle
import com.github.davidparry.intellijcreatetestfile.services.TestFolderService

class TestFolderWindowFactory : ToolWindowFactory {

    private val logger = Logger.getInstance(TestFolderWindowFactory::class.java)

    init {
        logger.warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindow = MyToolWindow(toolWindow)
        val content = ContentFactory.SERVICE.getInstance().createContent(myToolWindow.getContent(), "", false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project): Boolean = true

    class MyToolWindow(private val toolWindow: ToolWindow) {

        private val service: TestFolderService = toolWindow.project.service()

        fun getContent() = JBPanel<JBPanel<*>>().apply {
            val label = JBLabel(TestFileBundle.message("pathLabel", "?"))

            add(label)
            add(JButton(TestFileBundle.message("pathBtnLabel")).apply {
                addActionListener {
                    label.text = TestFileBundle.message("pathLabel", service.findTestSourceRoots())

                }
            })
        }
    }
}
