package org.nextras.orm.intellij.reference;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import org.jetbrains.annotations.NotNull;

public class OrmReferenceContributor extends PsiReferenceContributor
{

	@Override
	public void registerReferenceProviders(@NotNull PsiReferenceRegistrar psiReferenceRegistrar)
	{
		psiReferenceRegistrar.registerReferenceProvider(PlatformPatterns.psiElement(PhpElementTypes.STRING), new SetValueReferenceProvider());
	}
}
