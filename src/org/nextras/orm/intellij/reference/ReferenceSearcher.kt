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
	override fun processQuery(searchParameters: ReferencesSearch.SearchParameters, processor: Processor<PsiReference>) {
		if (searchParameters.elementToSearch !is PhpDocProperty) {
			return
		}

		val property = searchParameters.elementToSearch as PhpDocProperty

		val providers = arrayOf(CollectionPropertyReferenceProvider(), SetValueReferenceProvider())

		PsiSearchHelper.SERVICE.getInstance(property.project)
			.processElementsWithWord({ psiElement, i ->
				if (psiElement !is StringLiteralExpression) {
					return@processElementsWithWord true
				}
				val processingContext = ProcessingContext()
				processingContext.put("field", property.name)
				for (provider in providers) {
					for (reference in provider.getReferencesByElement(psiElement, processingContext)) {
						if (reference.isReferenceTo(searchParameters.elementToSearch)) {
							processor.process(reference)
						}
					}
				}

				true
			}, searchParameters.scopeDeterminedByUser, property.name, UsageSearchContext.IN_STRINGS, true)
	}
}
