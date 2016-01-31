package org.nextras.orm.intellij.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.FunctionReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import org.jetbrains.annotations.NotNull;


public class OrmCompletionContributor extends CompletionContributor
{
	private EntityPropertiesProvider entityPropertiesProvider = new EntityPropertiesProvider();


	public OrmCompletionContributor()
	{
		extend(CompletionType.BASIC, ModifiersPatterns.getModifierPattern(), new ModifiersProvider());
	}


	@Override
	public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result)
	{
		entityPropertiesProvider.fillCompletionVariants(parameters, result);
		super.fillCompletionVariants(parameters, result);
	}
}
