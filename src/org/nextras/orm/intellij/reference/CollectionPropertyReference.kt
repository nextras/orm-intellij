package org.nextras.orm.intellij.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.ResolveResult
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import org.nextras.orm.intellij.utils.OrmUtils
import java.util.*

class CollectionPropertyReference(
	element: StringLiteralExpression,
	private val methodReference: MethodReference,
	parts: Array<String>,
	private val pos: Int
) : PsiPolyVariantReferenceBase<StringLiteralExpression>(element) {
	private val parts: Array<String>

	init {
		this.parts = Arrays.copyOfRange(parts, 0, pos + 1)
		val end = this.parts.joinToString("->").length + 1
		rangeInElement = TextRange(end - parts[pos].length, end)
	}


	override fun multiResolve(b: Boolean): Array<ResolveResult> {
		val classes = OrmUtils.findQueriedEntities(methodReference, this.parts)
		val fields = HashSet<Field>()
		for (cls in classes) {
			val field = cls.findFieldByName(parts[pos], false)
			if (field == null || field !is PhpDocProperty) {
				continue
			}
			fields.add(field)
		}
		return fields.map {
			object : ResolveResult {
				override fun getElement(): PsiElement? {
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
