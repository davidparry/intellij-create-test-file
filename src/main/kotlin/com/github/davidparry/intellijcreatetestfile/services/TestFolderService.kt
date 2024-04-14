package com.github.davidparry.intellijcreatetestfile.services

import com.intellij.lang.Language
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager


@Service(Service.Level.PROJECT)
class TestFolderService(private val project: Project) {

    private val logger = Logger.getInstance(TestFolderService::class.java)

    fun findTestSourceRoots(): String {
        var path = ""
        val modules = ModuleManager.getInstance(project).modules
        for (module in modules) {
            val contentEntries = ModuleRootManager.getInstance(module).contentEntries
            for (entry in contentEntries) {
                val sourceFolders = entry.sourceFolders

                sourceFolders.filter { it.isTestSource }.forEach { folder ->
                    val testRoot = folder.file
                    if (testRoot != null && testRoot.isDirectory) {
                        path = testRoot.path
                        logger.info("Test source root found: ${testRoot.path}")
                        createSpockTestFile(testRoot, project)
                    } else {
                        logger.warn("Test source root not found or unable to access the path")
                    }
                }
            }
        }
        return path
    }

    /**
     * This function just to show that can write a file to the Test folder
     */
    private fun createSpockTestFile(testRoot: VirtualFile, project: Project) {
        val psiManager = PsiManager.getInstance(project)
        val directory = psiManager.findDirectory(testRoot)
        directory?.let {
            val groovyLanguage = Language.findLanguageByID("Groovy") // Look up Groovy language by ID
            if (groovyLanguage != null) {
                val groovyTestContent = """
                import spock.lang.Specification
                
                class AppSpec extends Specification {
                    def "sample test"() {
                        expect:
                        1 + 1 == 2
                    }
                }
            """.trimIndent()
                val groovyFile = PsiFileFactory.getInstance(project).createFileFromText("AppSpec.groovy", groovyLanguage, groovyTestContent)
                it.add(groovyFile)
                logger.info("Groovy Spock test file created: ${groovyFile.virtualFile.path}")
            } else {
                logger.error("Groovy language not found")
            }
        }
    }

}
