package org.nextras.orm.intellij.actions.generation

import com.intellij.codeInsight.CodeInsightActionHandler
import com.intellij.ide.util.MemberChooser
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleManager
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.actions.PhpNamedElementNode
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty
import com.jetbrains.php.lang.lexer.PhpTokenTypes
import com.jetbrains.php.lang.parser.PhpElementTypes
import com.jetbrains.php.lang.parser.PhpStubElementTypes
import com.jetbrains.php.lang.psi.PhpCodeEditUtil
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.PhpClass
import org.nextras.orm.intellij.utils.OrmUtils

abstract class GenerateActionHandler : CodeInsightActionHandler {
	fun isValidForFile(project: Project, editor: Editor, file: PsiFile): Boolean {
		if (file !is PhpFile) {
			return false
		}

		val phpClass = PhpCodeEditUtil.findClassAtCaret(editor, file) ?: return false
		val phpIndex = PhpIndex.getInstance(project)
		return OrmUtils.OrmClass.ENTITY.`is`(phpClass, phpIndex)
	}

	override fun invoke(project: Project, editor: Editor, file: PsiFile) {
		val phpFile = file as PhpFile
		val phpClass = PhpCodeEditUtil.findClassAtCaret(editor, phpFile)

		val fields = getFields(phpClass!!)
		val nodes = this.convertToNodes(fields)

		val chooser = MemberChooser(nodes, false, false, project)
		chooser.title = "Choose Fields"
		chooser.setCopyJavadocVisible(false)
		val isOk = chooser.showAndGet()
		val list = chooser.selectedElements

		if (!isOk || list == null || list.size == 0) {
			return
		}

		val insertPos = getSuitableEditorPosition(editor, phpFile)

		ApplicationManager.getApplication().runWriteAction {
			val textBuf = StringBuffer()

			for (member in list) {
				val field = member.psiElement
				textBuf.append('\n')
				textBuf.append(this@GenerateActionHandler.createAccessors(field as Field, project))
			}

			if (textBuf.isNotEmpty() && insertPos >= 0) {
				editor.document.insertString(insertPos, textBuf)
				val endPos = insertPos + textBuf.length
				CodeStyleManager.getInstance(project).reformatText(phpFile, insertPos, endPos)
				PsiDocumentManager.getInstance(project).commitDocument(editor.document)
			}
		}
	}

	protected abstract fun canShow(property: PhpDocProperty, phpClass: PhpClass): Boolean

	protected abstract fun createAccessors(field: Field, project: Project): String

	override fun startInWriteAction(): Boolean {
		return true
	}

	private fun getFields(phpClass: PhpClass): List<Field> {
		return phpClass.fields.filter {
			it is PhpDocProperty && canShow(it, phpClass)
		}
	}

	private fun convertToNodes(fields: Collection<Field>): Array<PhpNamedElementNode> {
		return fields.map { PhpNamedElementNode(it) }.toTypedArray()
	}

	private fun getSuitableEditorPosition(editor: Editor, phpFile: PhpFile): Int {
		val currElement = phpFile.findElementAt(editor.caretModel.offset)
		if (currElement != null) {
			var parent: PsiElement? = currElement.parent

			var prevParent = currElement
			while (parent != null && parent !is PhpFile) {
				if (isClassMember(parent)) {
					return getNextPos(parent)
				}

				if (parent is PhpClass) {
					while (prevParent != null) {
						if (isClassMember(prevParent) || prevParent.node.elementType === PhpTokenTypes.chLBRACE) {
							return getNextPos(prevParent)
						}

						prevParent = prevParent.prevSibling
					}

					var classChild: PsiElement? = parent.firstChild
					while (classChild != null) {
						if (classChild.node.elementType === PhpTokenTypes.chLBRACE) {
							return getNextPos(classChild)
						}
						classChild = classChild.nextSibling
					}
				}

				prevParent = parent
				parent = parent.parent
			}
		}

		return -1
	}

	private fun isClassMember(element: PsiElement?): Boolean {
		return if (element == null) {
			false
		} else {
			val elementType = element.node.elementType
			elementType === PhpElementTypes.CLASS_FIELDS || elementType === PhpElementTypes.CLASS_CONSTANTS || elementType === PhpStubElementTypes.CLASS_METHOD
		}
	}

	private fun getNextPos(element: PsiElement): Int {
		val next = element.nextSibling
		return next?.textOffset ?: -1
	}
}
