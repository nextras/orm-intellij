package org.nextras.orm.intellij.reference;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.search.PsiSearchHelper;
import com.intellij.psi.search.UsageSearchContext;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.ProcessingContext;
import com.intellij.util.Processor;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;

public class ReferenceSearcher extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters>
{


	@Override
	public void processQuery(ReferencesSearch.SearchParameters searchParameters, final Processor<PsiReference> processor)
	{
		if (!(searchParameters.getElementToSearch() instanceof PhpDocProperty)) {
			return;
		}

		final PhpDocProperty property = (PhpDocProperty) searchParameters.getElementToSearch();

		PsiReferenceProvider[] providers = new PsiReferenceProvider[]{
			new CollectionPropertyReferenceProvider(),
			new SetValueReferenceProvider(),
		};

		PsiSearchHelper.SERVICE.getInstance(property.getProject())
			.processElementsWithWord((psiElement, i) -> {
				if (!(psiElement instanceof StringLiteralExpression)) {
					return true;
				}
				for (PsiReferenceProvider provider : providers) {
					for (PsiReference reference : provider.getReferencesByElement(psiElement, new ProcessingContext())) {
						processor.process(reference);
					}
				}

				return true;
			}, searchParameters.getScopeDeterminedByUser(), property.getName(), UsageSearchContext.IN_STRINGS, true);
	}
}
