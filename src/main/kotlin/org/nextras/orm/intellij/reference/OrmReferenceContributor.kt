package org.nextras.orm.intellij.reference

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.jetbrains.php.lang.parser.PhpElementTypes

class OrmReferenceContributor : PsiReferenceContributor() {
	override fun registerReferenceProviders(psiReferenceRegistrar: PsiReferenceRegistrar) {
		psiReferenceRegistrar.registerReferenceProvider(
			ModifierClassNameProvider.PATTERN,
			ModifierClassNameProvider()
		)
		psiReferenceRegistrar.registerReferenceProvider(
			ModifierClassPropertyProvider.PATTERN,
			ModifierClassPropertyProvider()
		)
		psiReferenceRegistrar.registerReferenceProvider(
			PlatformPatterns.psiElement(PhpElementTypes.STRING),
			EntityPropertyNameReferenceProvider()
		)
		psiReferenceRegistrar.registerReferenceProvider(
			PlatformPatterns.psiElement(PhpElementTypes.STRING),
			CollectionPropertyReferenceProvider()
		)
	}
}
