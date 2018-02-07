package org.nextras.orm.intellij.inspection

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.PhpPsiElementFactory
import com.jetbrains.php.lang.psi.elements.*
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor
import org.jetbrains.annotations.Nls
import org.nextras.orm.intellij.utils.OrmUtils
import org.nextras.orm.intellij.utils.PhpIndexUtils

class ReadOnlyPropertyInspection : PhpInspection() {


	override fun getShortName(): String {
		return "NextrasOrmReadOnlyProperty"
	}


	override fun buildVisitor(problemsHolder: ProblemsHolder, b: Boolean): PsiElementVisitor {
		return object : PhpElementVisitor() {
			override fun visitPhpAssignmentExpression(assignmentExpression: AssignmentExpression) {
				if (assignmentExpression.variable !is FieldReference) {
					return
				}
				if (!isReadOnlyProperty((assignmentExpression.variable as FieldReference?)!!)) {
					return
				}
				problemsHolder.registerProblem(assignmentExpression, "Field is read only", SetReadOnlyValueFix())
			}
		}
	}

	private fun isReadOnlyProperty(fieldReference: FieldReference): Boolean {
		val phpIndex = PhpIndex.getInstance(fieldReference.project)
		val classes = PhpIndexUtils.getByType(fieldReference.classReference!!.type, phpIndex)
		for (cls in classes) {
			if (!OrmUtils.OrmClass.ENTITY.`is`(cls, phpIndex)) {
				continue
			}
			val field = cls.findFieldByName(fieldReference.name, false)
			if (field is PhpDocProperty && field.parent.firstChild.text == "@property-read") {
				return true
			}
		}
		return false
	}

	private inner class SetReadOnlyValueFix : LocalQuickFix {

		@Nls
		override fun getFamilyName(): String {
			return "Change to setReadOnlyValue"
		}

		override fun applyFix(project: Project, problemDescriptor: ProblemDescriptor) {
			val assignment = problemDescriptor.psiElement as AssignmentExpression
			val fieldReference = assignment.variable as FieldReference?

			val ref = PhpPsiElementFactory.createMethodReference(project, fieldReference!!.classReference!!.text
				+ "->setReadOnlyValue('" + fieldReference.name + "', "
				+ assignment.value!!.text + ")"
			)
			assignment.replace(ref)
		}
	}
}
