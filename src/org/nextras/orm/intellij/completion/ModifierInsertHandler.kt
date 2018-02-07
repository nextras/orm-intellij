package org.nextras.orm.intellij.completion

import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.editor.CaretModel
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.documentation.phpdoc.parser.tags.PhpDocPropertyTagParser
import com.jetbrains.php.lang.psi.PhpFile


class ModifierInsertHandler(private val insertSpace: Boolean) : InsertHandler<LookupElement> {

	override fun handleInsert(context: InsertionContext, element: LookupElement) {
		val editor = context.editor
		val caretModel = editor.caretModel
		val document = context.editor.document
		val file = context.file as PhpFile

		val currElement = file.findElementAt(editor.caretModel.offset)

		val buffer = StringBuilder()
		var newOffset = caretModel.offset
		if (insertSpace) {
			buffer.append(' ')
			newOffset++
		}

		var testElement = currElement
		if (testElement != null && testElement.node.elementType != PhpDocPropertyTagParser.DOC_WHITESPACE) {
			testElement = testElement.nextSibling
		}

		if (testElement != null && testElement.node.elementType != PhpDocPropertyTagParser.DOC_RBRACE) {
			buffer.append('}')
		}

		document.insertString(caretModel.offset, buffer.toString())
		caretModel.moveToOffset(newOffset)
	}
}
