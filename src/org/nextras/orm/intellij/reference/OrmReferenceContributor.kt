package org.nextras.orm.intellij.reference

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.jetbrains.php.lang.parser.PhpElementTypes

class OrmReferenceContributor : PsiReferenceContributor() {
	override fun registerReferenceProviders(psiReferenceRegistrar: PsiReferenceRegistrar) {
		psiReferenceRegistrar.registerReferenceProvider(PlatformPatterns.psiElement(PhpElementTypes.STRING), SetValueReferenceProvider())
		psiReferenceRegistrar.registerReferenceProvider(PlatformPatterns.psiElement(PhpElementTypes.STRING), CollectionPropertyReferenceProvider())
	}
}
