package org.nextras.orm.intellij.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.ProcessingContext

class ModifiersProvider : CompletionProvider<CompletionParameters>() {
	override fun addCompletions(
		params: CompletionParameters,
		context: ProcessingContext,
		result: CompletionResultSet
	) {
		result.addElement(LookupElementBuilder.create("enum").withInsertHandler(withParams))
		result.addElement(LookupElementBuilder.create("default").withInsertHandler(withParams))
		result.addElement(LookupElementBuilder.create("virtual").withInsertHandler(withoutParams))
		result.addElement(LookupElementBuilder.create("1:m").withInsertHandler(withParams))
		result.addElement(LookupElementBuilder.create("m:1").withInsertHandler(withParams))
		result.addElement(LookupElementBuilder.create("1:1").withInsertHandler(withParams))
		result.addElement(LookupElementBuilder.create("m:m").withInsertHandler(withParams))
		result.addElement(LookupElementBuilder.create("primary").withInsertHandler(withoutParams))
		result.addElement(LookupElementBuilder.create("primary-proxy").withInsertHandler(withoutParams))
		result.addElement(LookupElementBuilder.create("wrapper").withInsertHandler(withParams))
		result.addElement(LookupElementBuilder.create("embeddable").withInsertHandler(withoutParams))
	}

	companion object {
		private val withParams = ModifierInsertHandler(true)
		private val withoutParams = ModifierInsertHandler(false)
	}
}
