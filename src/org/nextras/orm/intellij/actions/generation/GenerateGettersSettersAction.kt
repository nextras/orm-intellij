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

class GenerateGettersSettersAction : CodeInsightAction() {
	private val actionHandler = object : GenerateActionHandler() {
		override fun canShow(property: PhpDocProperty, phpClass: PhpClass): Boolean {
			val name = property.name
			val getterMethodName = "getter" + name.substring(0, 1).uppercase() + name.substring(1)
			val setterMethodName = "setter" + name.substring(0, 1).uppercase() + name.substring(1)
			val getterMethod = phpClass.findMethodByName(getterMethodName)
			val setterMethod = phpClass.findMethodByName(setterMethodName)
			return getterMethod == null && setterMethod == null
		}

		override fun createAccessors(field: Field, project: Project): String {
			val name = field.name
			val getterMethodName = "getter" + name.substring(0, 1).uppercase() + name.substring(1)
			val getterMethod = PhpPsiElementFactory.createMethod(project, "protected function $getterMethodName(\$value) { return \$value; }")
			val setterMethodName = "setter" + name.substring(0, 1).uppercase() + name.substring(1)
			val setterMethod = PhpPsiElementFactory.createMethod(project, "protected function $setterMethodName(\$value) { return \$value; }")
			return setterMethod.text + '\n' + getterMethod.text
		}
	}

	override fun isValidForFile(project: Project, editor: Editor, file: PsiFile): Boolean {
		return this.actionHandler.isValidForFile(project, editor, file)
	}

	override fun getHandler(): CodeInsightActionHandler {
		return this.actionHandler
	}
}
