package org.nextras.orm.intellij.typeProvider

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeSignatureKey
import org.nextras.orm.intellij.utils.OrmUtils
import org.nextras.orm.intellij.utils.PhpIndexUtils

class CollectionTypeProvider : PhpTypeProvider4 {
	override fun getKey(): Char {
		return '\u0241'
	}

	override fun getType(element: PsiElement): PhpType? {
		if (element !is MethodReference || element.classReference == null) {
			return null
		}

		val methodName = element.name
		if (!allMethods.contains(methodName)) {
			return null
		}

		val objectTypeGeneral = element.classReference!!.type
		val objectTypes = objectTypeGeneral.filterMixed().filterNull().filterPrimitives().types
		val objectTypesFiltered = objectTypes.filterNot { it.startsWith("#V") } // filter out PhpTypeSignatureKey.VARIABLE
		val arraySuffix = if (pluralMethods.contains(methodName)) "[]" else ""

		val resultType = PhpType()
		objectTypesFiltered.forEach { objectType ->
			resultType.add("#$key$objectType.$methodName$arraySuffix")
		}
		return resultType
	}

	override fun complete(expression: String, project: Project): PhpType? {
		return null
	}

	override fun getBySignature(expression: String, visited: Set<String>, depth: Int, project: Project): Collection<PhpNamedElement>? {
		if (expression.endsWith("[]")) {
			return null
		}

		val pos = expression.lastIndexOf(".")
		var refSignature = expression.substring(0, pos)
		if (refSignature.endsWith("[]")) {
			refSignature = refSignature.substring(0, refSignature.length - 2)
		}
		var methodName = expression.substring(pos + 1)
		if (methodName.endsWith("[]")) {
			methodName = methodName.substring(0, methodName.length - 2)
		}

		val index = PhpIndex.getInstance(project)
		val classTypes = PhpIndexUtils.getByType(PhpType().add(refSignature), index)

		// $orm->books->getById()
		val repoClassesList = classTypes.filter { OrmUtils.OrmClass.REPOSITORY.`is`(it, index) }
		if (repoClassesList.isNotEmpty() && collectionMethods.contains(methodName)) {
			return OrmUtils.findRepositoryEntities(repoClassesList);
		}

		// $books->tags->toCollection()->getById()
		val hasHasManyType = classTypes.any { OrmUtils.OrmClass.HAS_MANY.`is`(it, index) }
		if (hasHasManyType && relationshipMethods.contains(methodName) && refSignature.startsWith("#")) {
			val arrayElementType = PhpType().add(PhpTypeSignatureKey.ARRAY_ELEMENT.sign(refSignature))
			return PhpIndexUtils.getByType(arrayElementType, index, visited, depth)
		}

		// $presenter->myCollection->getById()
		// unable to check here that is called on ICollection, probably complete() is needed to provide such
		// behavior
		val entityClassesList = classTypes.filter { OrmUtils.OrmClass.ENTITY.`is`(it, index) }
		if (entityClassesList.isNotEmpty() && collectionMethods.contains(methodName)) {
			return entityClassesList
		}

		return null
	}

	companion object {
		private val relationshipMethods = setOf("get", "toCollection")
		private val collectionPluralMethods = setOf("findBy", "orderBy", "limitBy", "fetchAll", "findAll", "findById")
		private val collectionSingularMethods = setOf("fetch", "getBy", "getById", "getByChecked", "getByIdChecked")
		private val collectionMethods = collectionPluralMethods + collectionSingularMethods
		private val pluralMethods = relationshipMethods + collectionPluralMethods
		private val allMethods = relationshipMethods + collectionMethods
	}
}
