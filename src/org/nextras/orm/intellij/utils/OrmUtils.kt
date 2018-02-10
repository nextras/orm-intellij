package org.nextras.orm.intellij.utils

import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty
import com.jetbrains.php.lang.psi.elements.*
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import java.util.*

object OrmUtils {
	enum class OrmClass constructor(private val className: String) {
		COLLECTION("\\Nextras\\Orm\\Collection\\ICollection"),
		MAPPER("\\Nextras\\Orm\\Mapper\\IMapper"),
		REPOSITORY("\\Nextras\\Orm\\Repository\\IRepository"),
		ENTITY("\\Nextras\\Orm\\Entity\\IEntity"),
		HAS_MANY("\\Nextras\\Orm\\Relationships\\HasMany");

		fun `is`(cls: PhpClass, index: PhpIndex): Boolean {
			val classes = index.getAnyByFQN(className)
			val instanceOf = when {
				classes.isEmpty() -> null
				else -> classes.iterator().next()
			} ?: return false
			return instanceOf.type.isConvertibleFrom(cls.type, index)
		}
	}

	fun findRepositoryEntities(repositories: Collection<PhpClass>): Collection<PhpClass> {
		val entities = HashSet<PhpClass>(1)
		for (repositoryClass in repositories) {

			val entityNamesMethod = repositoryClass.findMethodByName("getEntityClassNames") ?: return emptyList()
			if (entityNamesMethod.lastChild !is GroupStatement) {
				return emptyList()
			}
			if ((entityNamesMethod.lastChild as GroupStatement).firstPsiChild !is PhpReturn) {
				return emptyList()
			}
			if ((entityNamesMethod.lastChild as GroupStatement).firstPsiChild!!.firstPsiChild !is ArrayCreationExpression) {
				return emptyList()
			}
			val arr = (entityNamesMethod.lastChild as GroupStatement).firstPsiChild!!.firstPsiChild as ArrayCreationExpression?
			val phpIndex = PhpIndex.getInstance(repositoryClass.project)
			for (el in arr!!.children) {
				if (el.firstChild !is ClassConstantReference) {
					continue
				}
				val ref = el.firstChild as ClassConstantReference
				if (ref.name != "class") {
					continue
				}
				entities.addAll(PhpIndexUtils.getByType(ref.classReference!!.type, phpIndex))
			}
		}
		return entities
	}

	fun findQueriedEntities(ref: MemberReference): Collection<PhpClass> {
		val classReference = ref.classReference ?: return emptyList()
		val entities = HashSet<PhpClass>()
		val phpIndex = PhpIndex.getInstance(ref.project)
		val classes = PhpIndexUtils.getByType(classReference.type, phpIndex)
		val repositories = classes.filter { cls -> OrmClass.REPOSITORY.`is`(cls, phpIndex) }
		if (repositories.size > 0) {
			entities.addAll(findRepositoryEntities(repositories))
		}
		for (type in classReference.type.types) {
			if (!type.endsWith("[]")) {
				continue
			}
			val typeWithoutArray = PhpType().add(type.substring(0, type.length - 2))
			val maybeEntities = PhpIndexUtils.getByType(typeWithoutArray, phpIndex)
			entities.addAll(maybeEntities.filter { cls -> OrmClass.ENTITY.`is`(cls, phpIndex) })
		}

		return entities
	}

	fun findQueriedEntities(reference: MethodReference, path: Array<String>): Collection<PhpClass> {
		if (path.size == 0) {
			return emptyList()
		}
		val rootEntities: Collection<PhpClass>
		if (path.size == 1 || path[0] == "this") {
			rootEntities = findQueriedEntities(reference)
		} else {
			val index = PhpIndex.getInstance(reference.project)
			rootEntities = PhpIndexUtils.getByType(PhpType().add(path[0]), index)

		}
		if (rootEntities.size == 0) {
			return emptyList()
		}
		return if (path.size <= 1) {
			rootEntities
		} else findTargetEntities(rootEntities, path, 1)
	}

	private fun findTargetEntities(currentEntities: Collection<PhpClass>, path: Array<String>, pos: Int): Collection<PhpClass> {
		if (path.size == pos + 1) {
			return currentEntities
		}
		val entities = HashSet<PhpClass>()
		for (cls in currentEntities) {
			val field = cls.findFieldByName(path[pos], false) as? PhpDocProperty ?: continue
			addEntitiesFromField(entities, field)
		}
		return findTargetEntities(entities, path, pos + 1)
	}

	fun addEntitiesFromField(entities: MutableCollection<PhpClass>, field: PhpDocProperty) {
		val index = PhpIndex.getInstance(field.project)
		for (type in field.type.types) {
			if (type.contains("Nextras\\Orm\\Relationship")) {
				continue
			}
			val addType = if (type.endsWith("[]")) {
				type.substring(0, type.length - 2)
			} else {
				type
			}
			for (entityCls in PhpIndexUtils.getByType(PhpType().add(addType), index)) {
				if (!OrmClass.ENTITY.`is`(entityCls, index)) {
					continue
				}
				entities.add(entityCls)
			}
		}
	}
}
