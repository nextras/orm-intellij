package org.nextras.orm.intellij.reference

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.jetbrains.php.lang.parser.PhpElementTypes
import org.nextras.orm.intellij.patterns.ModifierPatterns

class OrmReferenceContributor : PsiReferenceContributor() {
	override fun registerReferenceProviders(psiReferenceRegistrar: PsiReferenceRegistrar) {
		psiReferenceRegistrar.registerReferenceProvider(
			ModifierPatterns.RelationshipClassName,
			ModifierClassNameProvider()
		)
		psiReferenceRegistrar.registerReferenceProvider(
			ModifierPatterns.WrapperClassName,
			ModifierClassNameProvider()
		)
		psiReferenceRegistrar.registerReferenceProvider(
			ModifierPatterns.RelationshipProperty,
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
