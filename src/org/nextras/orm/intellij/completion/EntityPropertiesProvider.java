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

		String entityClassName = OrmUtils.findRepositoryEntities(functionClass);
		if (entityClassName == null) {
			return;
		}
		PhpClass entityClass = null;
		for (PhpClass cls : phpIndex.getClassesByFQN(entityClassName)) {
			entityClass = cls;
			break;
		}
		if (entityClass == null || entityClass.getDocComment() == null) {
			return;
		}
		for (PhpDocPropertyTag phpDocPropertyTag : entityClass.getDocComment().getPropertyTags()) {
			result.addElement(LookupElementBuilder.create(phpDocPropertyTag.getProperty().getText().substring(1)));
		}
	}
}
