package org.nextras.orm.intellij.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiDocumentManager
import com.intellij.util.ProcessingContext
import com.jetbrains.php.PhpIcons
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty
import com.jetbrains.php.lang.psi.PhpPsiUtil
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpNamespace
import org.nextras.orm.intellij.utils.OrmUtils

class SetReadOnlyValueCompletionProvider : CompletionProvider<CompletionParameters>() {
	override fun addCompletions(
		parameters: CompletionParameters,
		context: ProcessingContext,
		result: CompletionResultSet
	) {
		val el = parameters.position
		val cls = PhpPsiUtil.getParentByCondition<PhpClass>(el, PhpClass.INSTANCEOF, PhpNamespace.INSTANCEOF) ?: return

		val phpIndex = PhpIndex.getInstance(el.project)
		if (!OrmUtils.OrmClass.ENTITY.`is`(cls, phpIndex)) {
			return
		}

		cls.fields
			.filter { it is PhpDocProperty && it.getParent().firstChild.text == "@property-read" }
			.map {
				LookupElementBuilder.create(it.name)
					.withInsertHandler { insertionContext, lookupElement ->
						val phpCode = "\$this->setReadOnlyValue('" + lookupElement.lookupString + "', );"
						val document = insertionContext.document
						document.replaceString(insertionContext.startOffset, insertionContext.tailOffset, phpCode)
						PsiDocumentManager.getInstance(insertionContext.project).commitDocument(document)
						insertionContext.editor.caretModel.moveToOffset(insertionContext.startOffset + phpCode.length - 2)
					}
					.withTypeText(it.type.toString())
					.withIcon(PhpIcons.READONLY_FIELD)
					.withPresentableText(it.name + " = ...")
			}
			.forEach { result.addElement(it) }
	}
}
