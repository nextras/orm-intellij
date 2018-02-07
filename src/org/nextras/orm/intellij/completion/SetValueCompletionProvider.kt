package org.nextras.orm.intellij.completion


import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.PhpClass
import org.nextras.orm.intellij.utils.OrmUtils
import org.nextras.orm.intellij.utils.PhpIndexUtils

class SetValueCompletionProvider : CompletionProvider<CompletionParameters>() {

	override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
		parameters.position
		val el = parameters.position
		if (el.parent == null
			|| el.parent.parent == null
			|| el.parent.parent !is ParameterList
			|| (el.parent.parent as ParameterList).parameters[0] !== el.parent
			|| el.parent.parent.parent == null
			|| el.parent.parent.parent !is MethodReference) {
			return
		}
		val methodReference = el.parent.parent.parent as MethodReference
		if (methodReference.name != "setValue" && methodReference.name != "setReadOnlyValue") {
			return
		}
		val phpIndex = PhpIndex.getInstance(el.project)
		for (cls in PhpIndexUtils.getByType(methodReference.classReference!!.type, phpIndex)) {
			if (!OrmUtils.OrmClass.ENTITY.`is`(cls, phpIndex)) {
				continue
			}
			for (field in cls.fields) {
				if (field !is PhpDocProperty) {
					continue
				}
				result.addElement(LookupElementBuilder.create(field.getName()).withTypeText(field.getType().toString()))
			}
		}
	}
}
