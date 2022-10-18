package org.nextras.orm.intellij.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.ResolveResult
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import org.nextras.orm.intellij.utils.OrmUtils
import org.nextras.orm.intellij.utils.PhpIndexUtils

class ModifierClassProperty(
	psiElement: PsiElement
) : PsiPolyVariantReferenceBase<PsiElement>(psiElement) {
	override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
		val classElement = element.prevSibling?.prevSibling ?: return emptyArray()
		val fqnClass = PhpIndexUtils.getFqnForClassNameByContext(classElement, classElement.text)
		val phpIndex = PhpIndex.getInstance(this.element.project)
		val result = PhpIndexUtils.getByType(PhpType().add(fqnClass), phpIndex)
		return result
			.filter { OrmUtils.OrmClass.ENTITY.`is`(it, phpIndex) }
			.mapNotNull { it.findFieldByName(element.text.substring(1), false) }
			.filterIsInstance<PhpDocProperty>()
			.map {
				object : ResolveResult {
					override fun getElement(): PsiElement {
						return it
					}

					override fun isValidResult(): Boolean {
						return true
					}
				}
			}
			.toTypedArray()
	}

	override fun getRangeInElement(): TextRange {
		return TextRange.create(0, myElement.textLength)
	}
}
