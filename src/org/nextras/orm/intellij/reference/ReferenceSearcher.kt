package org.nextras.orm.intellij.reference

import com.intellij.openapi.application.QueryExecutorBase
import com.intellij.psi.PsiReference
import com.intellij.psi.search.PsiSearchHelper
import com.intellij.psi.search.UsageSearchContext
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.util.ProcessingContext
import com.intellij.util.Processor
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

class ReferenceSearcher : QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters>() {
	override fun processQuery(searchParameters: ReferencesSearch.SearchParameters, processor: Processor<in PsiReference>) {
		val property = searchParameters.elementToSearch as? PhpDocProperty ?: return
		val providers = arrayOf(CollectionPropertyReferenceProvider(), SetValueReferenceProvider())

		PsiSearchHelper.getInstance(property.project)
			.processElementsWithWord(
				{ psiElement, _ ->
					if (psiElement !is StringLiteralExpression) {
						return@processElementsWithWord true
					}

					val processingContext = ProcessingContext()
					processingContext.put("field", property.name)
					for (provider in providers) {
						provider.getReferencesByElement(psiElement, processingContext)
							.filter { it.isReferenceTo(searchParameters.elementToSearch) }
							.forEach { processor.process(it) }
					}

					true
				},
				searchParameters.scopeDeterminedByUser,
				property.name,
				UsageSearchContext.IN_STRINGS,
				true
			)
	}
}
