package org.nextras.orm.intellij.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocPropertyTag;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.*;
import org.jetbrains.annotations.NotNull;
import org.nextras.orm.intellij.utils.OrmUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class EntityPropertiesProvider
{
	public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result)
	{
		PsiElement element = parameters.getPosition().getParent();

		ParameterList parameterList = PsiTreeUtil.getParentOfType(element, ParameterList.class);
		if (parameterList == null) {
			return;
		}
		if (element.getParent() instanceof PhpPsiElement
			&& ((PhpPsiElement) element.getParent()).getPrevPsiSibling() != null
			&& ((PhpPsiElement) element.getParent()).getPrevPsiSibling().getNode().getElementType() == PhpElementTypes.ARRAY_KEY) {
			return;
		}
		PsiElement context = parameterList.getContext();
		if (!(context instanceof MethodReference)) {
			return;
		}
		MethodReference methodReference = (MethodReference) context;
		PsiElement methodEl = methodReference.resolve();
		if (methodEl == null || !(methodEl instanceof Method)) {
			return;
		}
		Method method = (Method) methodEl;
		if (!(method.getName().equals("findBy") || method.getName().equals("getBy") || method.getName().equals("orderBy"))) {
			return;
		}

		Project project = parameters.getEditor().getProject();
		if (project == null) {
			return;
		}


		String fieldExpression = parameters.getOriginalPosition().getText();
		String[] path = fieldExpression.split("->", -1);

		Collection<PhpClass> queriedEntities = OrmUtils.findQueriedEntities(methodReference, path);
		for (PhpClass cls : queriedEntities) {

			if (cls.getDocComment() == null) {
				continue;
			}
			for (PhpDocPropertyTag phpDocPropertyTag : cls.getDocComment().getPropertyTags()) {

				Stream<String> types = phpDocPropertyTag.getType().getTypesSorted().stream()
					.filter(s -> !s.contains("Nextras\\Orm\\Relationships") && !s.equals("?"))
					.map(s -> s.startsWith("\\") ? s.substring(1) : s);

				String strPath = String.join("->", Arrays.copyOfRange(path, 0, path.length - 1));
				if (strPath.length() > 0) {
					strPath += "->";
				}
				String fieldName = phpDocPropertyTag.getProperty().getText().substring(1);
				strPath += fieldName;

				result.addElement(LookupElementBuilder.create(strPath)
					.withPresentableText(fieldName)
					.withTypeText(types.collect(Collectors.joining("|"))));
			}
			if (path.length == 1) {
				result.addElement(LookupElementBuilder.create("this").withTypeText(cls.getType().toString()));
				result.addElement(LookupElementBuilder.create(cls.getFQN()).withTypeText(cls.getType().toString()));
			}
		}
	}
}
