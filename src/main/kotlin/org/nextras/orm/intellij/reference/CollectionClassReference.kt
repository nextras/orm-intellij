package org.nextras.orm.intellij.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.ResolveResult
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import org.nextras.orm.intellij.utils.OrmUtils

class CollectionClassReference constructor(
	psiElement: StringLiteralExpression,
	private val methodReference: MethodReference,
	private val name: String
) : PsiPolyVariantReferenceBase<StringLiteralExpression>(psiElement, TextRange(1, name.length + 1)) {
	override fun multiResolve(b: Boolean): Array<ResolveResult> {
		val classes = OrmUtils.findQueriedEntities(methodReference, name, arrayOf())
		return classes.map {
			object : ResolveResult {
				override fun getElement(): PsiElement {
					return it
				}

				override fun isValidResult(): Boolean {
					return true
				}
			}
		}.toTypedArray()
	}

	override fun getVariants(): Array<Any> {
		return emptyArray()
	}
}
