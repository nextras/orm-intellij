package org.nextras.orm.intellij.actions.generation

import com.intellij.codeInsight.CodeInsightActionHandler
import com.intellij.codeInsight.actions.CodeInsightAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty
import com.jetbrains.php.lang.psi.PhpPsiElementFactory
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.PhpClass

class GenerateGettersAction : CodeInsightAction() {
	private val actionHandler = object : GenerateActionHandler() {
		override fun canShow(property: PhpDocProperty, phpClass: PhpClass): Boolean {
			val name = property.name
			val methodName = "getter" + name.substring(0, 1).uppercase() + name.substring(1)
			val method = phpClass.findMethodByName(methodName)
			return method == null
		}

		override fun createAccessors(field: Field, project: Project): String {
			val name = field.name
			val methodName = "getter" + name.substring(0, 1).uppercase() + name.substring(1)
			val method = PhpPsiElementFactory.createMethod(project, "protected function $methodName(\$value) { return \$value; }")
			return method.text
		}
	}

	override fun isValidForFile(project: Project, editor: Editor, file: PsiFile): Boolean {
		return this.actionHandler.isValidForFile(project, editor, file)
	}

	override fun getHandler(): CodeInsightActionHandler {
		return this.actionHandler
	}
}
