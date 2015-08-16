package org.nextras.orm.intellij.completion;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.documentation.phpdoc.parser.tags.PhpDocPropertyTagParser;
import com.jetbrains.php.lang.psi.PhpFile;


public class ModifierInsertHandler implements InsertHandler<LookupElement>
{
	private boolean insertSpace;


	public ModifierInsertHandler(boolean insertSpace)
	{
		this.insertSpace = insertSpace;
	}

	@Override
	public void handleInsert(InsertionContext context, LookupElement element)
	{
		Editor editor = context.getEditor();
		CaretModel caretModel = editor.getCaretModel();
		Document document = context.getEditor().getDocument();
		PhpFile file = (PhpFile) context.getFile();

		PsiElement currElement = file.findElementAt(editor.getCaretModel().getOffset());

		StringBuilder buffer = new StringBuilder();
		int newOffset = caretModel.getOffset();
		if (insertSpace) {
			buffer.append(' ');
			newOffset++;
		}

		if (currElement != null && !(currElement.getNode().getElementType().equals(PhpDocPropertyTagParser.DOC_RBRACE))) {
			buffer.append('}');
		}

		document.insertString(caretModel.getOffset(), buffer.toString());
		caretModel.moveToOffset(newOffset);
	}
}
