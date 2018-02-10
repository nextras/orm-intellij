package org.nextras.orm.intellij.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.ProcessingContext
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.ParameterList
import org.nextras.orm.intellij.utils.OrmUtils
import org.nextras.orm.intellij.utils.PhpIndexUtils

class SetValueCompletionProvider : CompletionProvider<CompletionParameters>() {
	override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
		val el = parameters.position

		if (el.parent?.parent !is ParameterList || (el.parent.parent as ParameterList).parameters[0] !== el.parent) {
			return
		}

		val methodReference = el.parent?.parent?.parent as? MethodReference ?: return
		if (methodReference.name != "setValue" && methodReference.name != "setReadOnlyValue") {
			return
		}

		val phpIndex = PhpIndex.getInstance(el.project)
		val classes = PhpIndexUtils.getByType(methodReference.classReference!!.type, phpIndex)

		classes
			.filter { OrmUtils.OrmClass.ENTITY.`is`(it, phpIndex) }
			.flatMap { it.fields }
			.filterIsInstance<PhpDocProperty>()
			.forEach {
				result.addElement(
					LookupElementBuilder.create(it.name).withTypeText(it.type.toString())
				)
			}
	}
}
