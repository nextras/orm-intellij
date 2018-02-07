package org.nextras.orm.intellij.actions.generation

import com.intellij.codeInsight.CodeInsightActionHandler
import com.intellij.codeInsight.actions.CodeInsightAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty
import com.jetbrains.php.lang.psi.PhpPsiElementFactory
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpClass


class GenerateGettersSettersAction : CodeInsightAction() {
	private val actionHandler = object : GenerateActionHandler() {
		override fun canShow(property: PhpDocProperty, phpClass: PhpClass): Boolean {
			val name = property.name
			val getter_methodName = "getter" + name.substring(0, 1).toUpperCase() + name.substring(1)
			val setter_methodName = "setter" + name.substring(0, 1).toUpperCase() + name.substring(1)
			val getter_method = phpClass.findMethodByName(getter_methodName)
			val setter_method = phpClass.findMethodByName(setter_methodName)
			return getter_method == null && setter_method == null
		}

		override fun createAccessors(field: Field, project: Project): String {
			val name = field.name

			val getter_methodName = "getter" + name.substring(0, 1).toUpperCase() + name.substring(1)
			val getter_method = PhpPsiElementFactory.createMethod(project, "protected function $getter_methodName(\$value) { return \$value; }")

			val setter_methodName = "setter" + name.substring(0, 1).toUpperCase() + name.substring(1)
			val setter_method = PhpPsiElementFactory.createMethod(project, "protected function $setter_methodName(\$value) { return \$value; }")

			return setter_method.text + '\n' + getter_method.text
		}
	}


	override fun isValidForFile(project: Project, editor: Editor, file: PsiFile): Boolean {
		return this.actionHandler.isValidForFile(project, editor, file)
	}


	override fun getHandler(): CodeInsightActionHandler {
		return this.actionHandler
	}
}
