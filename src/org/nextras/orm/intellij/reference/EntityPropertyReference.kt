package org.nextras.orm.intellij.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.ResolveResult
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import org.nextras.orm.intellij.utils.OrmUtils
import org.nextras.orm.intellij.utils.PhpIndexUtils

class EntityPropertyReference(psiElement: StringLiteralExpression) : PsiPolyVariantReferenceBase<StringLiteralExpression>(psiElement) {
	override fun multiResolve(b: Boolean): Array<ResolveResult> {
		val expr = element
		assert(expr.parent.parent != null)

		val method = expr.parent.parent as MethodReference
		val phpIndex = PhpIndex.getInstance(this.element.project)
		val result = PhpIndexUtils.getByType(method.classReference!!.type, phpIndex)
		return result
			.filter { OrmUtils.OrmClass.ENTITY.`is`(it, phpIndex) }
			.mapNotNull { it.findFieldByName(expr.contents, false) }
			.filterIsInstance<PhpDocProperty>()
			.map {
				object : ResolveResult {
					override fun getElement(): PsiElement? {
						return it
					}

					override fun isValidResult(): Boolean {
						return true
					}
				}
			}
			.toTypedArray()
	}

	override fun getVariants(): Array<Any> {
		return emptyArray()
	}
}
