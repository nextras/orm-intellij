package org.nextras.orm.intellij.completion;


import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.PhpIcons;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty;
import com.jetbrains.php.lang.psi.PhpPsiUtil;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;
import org.nextras.orm.intellij.utils.OrmUtils;

public class SetReadOnlyValueCompletionProvider extends CompletionProvider<CompletionParameters>
{

	@Override
	protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result)
	{
		parameters.getPosition();
		PsiElement el = parameters.getPosition();
		PhpClass cls = PhpPsiUtil.getParentByCondition(el, PhpClass.INSTANCEOF);
		if (cls == null) {
			return;
		}

		PhpIndex phpIndex = PhpIndex.getInstance(el.getProject());
		if (!OrmUtils.OrmClass.ENTITY.is(cls, phpIndex)) {
			return;
		}
		for (Field field : cls.getFields()) {
			if (!(field instanceof PhpDocProperty) || !field.getParent().getFirstChild().getText().equals("@property-read")) {
				continue;
			}
			LookupElementBuilder element = LookupElementBuilder.create(field.getName())
				.withInsertHandler(new InsertHandler<LookupElement>()
				{
					@Override
					public void handleInsert(InsertionContext insertionContext, LookupElement lookupElement)
					{
						String phpCode = "$this->setReadOnlyValue('" + lookupElement.getLookupString() + "', );";
						Document document = insertionContext.getDocument();
						document.replaceString(insertionContext.getStartOffset(), insertionContext.getTailOffset(), phpCode);
						PsiDocumentManager.getInstance(insertionContext.getProject()).commitDocument(document);
						insertionContext.getEditor().getCaretModel().moveToOffset(insertionContext.getStartOffset() + phpCode.length() - 2);
					}
				})
				.withTypeText(field.getType().toString())
				.withIcon(PhpIcons.VARIABLE_WRITE_ACCESS)
				.withPresentableText(field.getName() + " = ...");
			result.addElement(element);
		}
	}
}
