package org.nextras.orm.intellij.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocPropertyTag;
import com.jetbrains.php.lang.psi.elements.*;
import org.jetbrains.annotations.NotNull;
import org.nextras.orm.intellij.utils.OrmUtils;
import org.nextras.orm.intellij.utils.PhpClassUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
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
		if (!(method.getName().equals("findBy") || method.getName().equals("getBy"))) {
			return;
		}

		Project project = parameters.getEditor().getProject();
		if (project == null) {
			return;
		}
		PhpIndex phpIndex = PhpIndex.getInstance(project);

		PhpExpression classReference = methodReference.getClassReference();
		if (classReference == null) {
			return;
		}

		PhpClass functionClass = null;
		for (String fqn : classReference.getType().getTypes()) {
			for (PhpClass cls : phpIndex.getClassesByFQN(fqn)) {
				functionClass = cls;
				break;
			}
		}
		if (functionClass == null) {
			return;
		}

		//PhpClass ICollectionInterface = PhpClassUtils.getInterface(phpIndex, "\\Nextras\\Orm\\Collection\\ICollection");
		PhpClass IRepositoryInterface = PhpClassUtils.getInterface(phpIndex, "\\Nextras\\Orm\\Repository\\IRepository");

		if (!(PhpClassUtils.isImplementationOfInterface(functionClass, IRepositoryInterface))) {
			return;
		}

		for (PhpClass cls : OrmUtils.findRepositoryEntities(functionClass)) {

			if (cls.getDocComment() == null) {
				return;
			}
			for (PhpDocPropertyTag phpDocPropertyTag : cls.getDocComment().getPropertyTags()) {

				Stream<String> types = phpDocPropertyTag.getType().getTypesSorted().stream().filter(s -> !s.contains("Nextras\\Orm\\Relationships") && !s.equals("?"));

				result.addElement(LookupElementBuilder.create(phpDocPropertyTag.getProperty().getText().substring(1))
					.withTypeText(types.collect(Collectors.joining("|"))));
			}
			result.addElement(LookupElementBuilder.create("this").withTypeText(cls.getType().toString()));
			result.addElement(LookupElementBuilder.create(cls.getFQN()).withTypeText(cls.getType().toString()));
		}
	}
}
