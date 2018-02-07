package org.nextras.orm.intellij.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.elements.*


class OrmCompletionContributor : CompletionContributor() {
	private val entityPropertiesProvider = EntityPropertiesProvider()

	init {
		extend(CompletionType.BASIC, ModifiersPatterns.modifierPattern, ModifiersProvider())
		extend(CompletionType.BASIC, PlatformPatterns.psiElement().withParent(StringLiteralExpression::class.java), SetValueCompletionProvider())
		extend(CompletionType.BASIC, PlatformPatterns.psiElement().withParent(ConstantReference::class.java).withSuperParent(3, GroupStatement::class.java), SetReadOnlyValueCompletionProvider())
	}


	override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
		entityPropertiesProvider.fillCompletionVariants(parameters, result)
		super.fillCompletionVariants(parameters, result)
	}
}
