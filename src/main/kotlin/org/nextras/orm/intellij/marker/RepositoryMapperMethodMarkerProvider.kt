package org.nextras.orm.intellij.marker

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import com.jetbrains.php.PhpIcons
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocMethod
import org.nextras.orm.intellij.utils.OrmUtils

class RepositoryMapperMethodMarkerProvider : RelatedItemLineMarkerProvider() {
	override fun collectNavigationMarkers(element: PsiElement, result: MutableCollection<in RelatedItemLineMarkerInfo<*>>) {
		if (element !is PhpDocMethod) {
			return
		}

		val containingClass = element.containingClass
		val index = PhpIndex.getInstance(element.project)
		if (!OrmUtils.OrmClass.REPOSITORY.`is`(containingClass!!, index)) {
			return
		}
		if (baseMethods.contains(element.name)) {
			return
		}

		val repositoryClass = containingClass.fqn
		val mapperClass = repositoryClass.substring(0, repositoryClass.length - 10) + "Mapper"
		val phpIndex = PhpIndex.getInstance(element.getProject())

		val methods = phpIndex.getClassesByFQN(mapperClass)
			.filter { OrmUtils.OrmClass.MAPPER.`is`(it, phpIndex) }
			.mapNotNull { it.findMethodByName(element.name) }

		result.add(
			NavigationGutterIconBuilder.create(PhpIcons.METHOD)
				.setTargets(methods)
				.setTooltipText("Navigate to mapper method")
				.createLineMarkerInfo(element)
		)
	}

	companion object {
		private val baseMethods = hashSetOf("findAll", "findBy", "findById", "getBy", "getByChecked", "getById", "getByIdChecked")
	}
}
