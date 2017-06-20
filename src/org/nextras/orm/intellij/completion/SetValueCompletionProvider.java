package org.nextras.orm.intellij.completion;


import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;
import org.nextras.orm.intellij.utils.PhpIndexUtils;

public class SetValueCompletionProvider extends CompletionProvider<CompletionParameters> {

	@Override
	protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
		parameters.getPosition();
		PsiElement el = parameters.getPosition();
		if (
			el.getParent() == null
				|| el.getParent().getParent() == null
				|| !(el.getParent().getParent() instanceof ParameterList)
				|| ((ParameterList) el.getParent().getParent()).getParameters()[0] != el.getParent()
				|| el.getParent().getParent().getParent() == null
				|| !(el.getParent().getParent().getParent() instanceof MethodReference)
			) {
			return;
		}
		MethodReference methodReference = (MethodReference) el.getParent().getParent().getParent();
		if (!methodReference.getName().equals("setValue") && !methodReference.getName().equals("setReadOnlyValue")) {
			return;
		}
		PhpIndex phpIndex = PhpIndex.getInstance(el.getProject());
		for (PhpClass cls : PhpIndexUtils.getByType(methodReference.getClassReference().getType(), phpIndex)) {
			for (Field field : cls.getFields()) {
				if (!(field instanceof PhpDocProperty)) {
					continue;
				}
				result.addElement(LookupElementBuilder.create(field.getName()).withTypeText(field.getType().toString()));
			}
		}
	}
}
