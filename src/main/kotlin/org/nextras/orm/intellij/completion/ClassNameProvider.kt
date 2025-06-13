package org.nextras.orm.intellij.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionUtil
import com.intellij.codeInsight.completion.PlainPrefixMatcher
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.ProcessingContext
import com.jetbrains.php.PhpIcons
import com.jetbrains.php.PhpIndex

class ClassNameProvider(
	private val parentInterface: String,
	private val allowAbstract: Boolean,
) : CompletionProvider<CompletionParameters>() {
	override fun addCompletions(
		parameters: CompletionParameters,
		context: ProcessingContext,
		result: CompletionResultSet
	) {
		val el = parameters.position

		val phpIndex = PhpIndex.getInstance(el.project)
		val prefix = CompletionUtil.findReferenceOrAlphanumericPrefix(parameters)
		val matcher = PlainPrefixMatcher(prefix)

		phpIndex.processAllSubclasses(parentInterface) { phpClass ->
			if ((phpClass.isAbstract && !allowAbstract) || phpClass.isInterface || phpClass.name.isBlank()) return@processAllSubclasses true
			if (!matcher.prefixMatches(phpClass.name)) return@processAllSubclasses true

			val className = phpClass.name
			val fqName = phpClass.fqn

			val element = LookupElementBuilder.create(className)
				.withTypeText(fqName, true)
				.withIcon(PhpIcons.CLASS)

			result.addElement(element)
			true
		}
	}
}
