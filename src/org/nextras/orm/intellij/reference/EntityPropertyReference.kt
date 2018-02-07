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
import java.util.*

class EntityPropertyReference(psiElement: StringLiteralExpression) : PsiPolyVariantReferenceBase<StringLiteralExpression>(psiElement) {
	override fun multiResolve(b: Boolean): Array<ResolveResult> {
		val expr = element
		assert(expr.parent.parent != null)

		val method = expr.parent.parent as MethodReference
		assert(method.name != null && (method.name == "setValue" || method.name == "setReadOnlyValue"))

		val result = ArrayList<ResolveResult>()
		val phpIndex = PhpIndex.getInstance(this.element.project)
		for (cls in PhpIndexUtils.getByType(method.classReference!!.type, phpIndex)) {
			if (!OrmUtils.OrmClass.ENTITY.`is`(cls, phpIndex)) {
				continue
			}
			val field = cls.findFieldByName(expr.contents, false)
			if (field == null || field !is PhpDocProperty) {
				continue
			}
			result.add(object : ResolveResult {
				override fun getElement(): PsiElement? {
					return field
				}

				override fun isValidResult(): Boolean {
					return true
				}
			})
		}
		return result.toTypedArray()
	}

	override fun getVariants(): Array<Any> {
		return emptyArray()
	}
}
