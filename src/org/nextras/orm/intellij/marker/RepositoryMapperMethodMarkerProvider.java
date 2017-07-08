package org.nextras.orm.intellij.marker;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIcons;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocMethod;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;
import org.nextras.orm.intellij.utils.OrmUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class RepositoryMapperMethodMarkerProvider extends RelatedItemLineMarkerProvider
{

	private static Set<String> baseMethods = new HashSet<String>();

	static {
		baseMethods.add("findAll");
		baseMethods.add("findBy");
		baseMethods.add("findById");
		baseMethods.add("getBy");
		baseMethods.add("getById");
	}


	@Override
	protected void collectNavigationMarkers(@NotNull PsiElement element, Collection<? super RelatedItemLineMarkerInfo> result)
	{
		if (!(element instanceof PhpDocMethod)) {
			return;
		}
		PhpDocMethod method = (PhpDocMethod) element;
		PhpClass containingClass = method.getContainingClass();
		PhpIndex index = PhpIndex.getInstance(method.getProject());
		if (!OrmUtils.isRepository(containingClass, index)) {
			return;
		}
		if (baseMethods.contains(method.getName())) {
			return;
		}
		String repositoryClass = containingClass.getFQN();
		String mapperClass = repositoryClass.substring(0, repositoryClass.length() - 10) + "Mapper";


		PhpIndex phpIndex = PhpIndex.getInstance(element.getProject());
		Collection<Method> methods = new ArrayList<Method>();
		for (PhpClass cls : phpIndex.getClassesByFQN(mapperClass)) {
			if (!OrmUtils.isMapper(cls, phpIndex)) {
				continue;
			}
			final Method mapperMethod = cls.findMethodByName(method.getName());
			if (mapperMethod == null) {
				continue;
			}
			methods.add(mapperMethod);
		}
		result.add(NavigationGutterIconBuilder.create(PhpIcons.METHOD)
			.setTargets(methods)
			.setTooltipText("Navigate to mapper method")
			.createLineMarkerInfo(method)
		);

	}


}
