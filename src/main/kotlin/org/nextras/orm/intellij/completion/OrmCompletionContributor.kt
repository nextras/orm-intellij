package org.nextras.orm.intellij.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PlatformPatterns
import com.jetbrains.php.lang.psi.elements.ConstantReference
import com.jetbrains.php.lang.psi.elements.GroupStatement
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

class OrmCompletionContributor : CompletionContributor() {
	private val entityPropertiesProvider = EntityPropertiesProvider()

	init {
		extend(CompletionType.BASIC, ModifiersPatterns.modifierPattern, ModifiersProvider())
		extend(CompletionType.BASIC, PlatformPatterns.psiElement().withParent(StringLiteralExpression::class.java), PropertyNameCompletionProvider())
		extend(CompletionType.BASIC, PlatformPatterns.psiElement().withParent(ConstantReference::class.java).withSuperParent(3, GroupStatement::class.java), SetReadOnlyValueCompletionProvider())
	}

	override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
		entityPropertiesProvider.fillCompletionVariants(parameters, result)
		super.fillCompletionVariants(parameters, result)
	}
}
