package org.nextras.orm.intellij.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PlatformPatterns
import com.jetbrains.php.lang.psi.elements.ConstantReference
import com.jetbrains.php.lang.psi.elements.GroupStatement
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import org.nextras.orm.intellij.patterns.ModifierPatterns
import org.nextras.orm.intellij.utils.OrmUtils

class OrmCompletionContributor : CompletionContributor() {
	private val entityPropertiesProvider = EntityPropertiesProvider()

	init {
		extend(
			CompletionType.BASIC,
			ModifierPatterns.Modifier,
			ModifiersProvider()
		)
		extend(
			CompletionType.BASIC,
			ModifierPatterns.WrapperClassName,
			ClassNameProvider(OrmUtils.OrmClass.PROPERTY_WRAPPER.className, allowAbstract = false),
		)
		extend(
			CompletionType.BASIC,
			ModifierPatterns.RelationshipClassName,
			ClassNameProvider(OrmUtils.OrmClass.ENTITY.className, allowAbstract = true),
		)
		extend(
			CompletionType.BASIC,
			PlatformPatterns.psiElement()
				.withParent(StringLiteralExpression::class.java),
			PropertyNameCompletionProvider()
		)
		extend(
			CompletionType.BASIC,
			PlatformPatterns.psiElement()
				.withParent(ConstantReference::class.java)
				.withSuperParent(3, GroupStatement::class.java),
			SetReadOnlyValueCompletionProvider()
		)
	}

	override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
		entityPropertiesProvider.fillCompletionVariants(parameters, result)
		super.fillCompletionVariants(parameters, result)
	}
}
