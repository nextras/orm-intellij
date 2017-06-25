package org.nextras.orm.intellij.inspection;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty;
import com.jetbrains.php.lang.inspections.PhpInspection;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.nextras.orm.intellij.utils.PhpClassUtils;
import org.nextras.orm.intellij.utils.PhpIndexUtils;

import java.util.Collection;

public class ReadOnlyPropertyInspection extends PhpInspection
{


	@NotNull
	@Override
	public String getShortName()
	{
		return "NextrasOrmReadOnlyProperty";
	}


	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder problemsHolder, boolean b)
	{
		return new PhpElementVisitor()
		{
			@Override
			public void visitPhpAssignmentExpression(AssignmentExpression assignmentExpression)
			{
				if (!(assignmentExpression.getVariable() instanceof FieldReference)) {
					return;
				}
				if (!isReadOnlyProperty((FieldReference) assignmentExpression.getVariable())) {
					return;
				}
				problemsHolder.registerProblem(assignmentExpression, "Field is read only", new SetReadOnlyValueFix());
			}
		};
	}

	private boolean isReadOnlyProperty(FieldReference fieldReference)
	{
		PhpIndex phpIndex = PhpIndex.getInstance(fieldReference.getProject());
		Collection<PhpClass> classes = PhpIndexUtils.getByType(fieldReference.getClassReference().getType(), phpIndex);
		for (PhpClass cls : classes) {
			PhpClass entityInterface = PhpClassUtils.getInterface(phpIndex, "\\Nextras\\Orm\\Entity\\IEntity");
			if (!PhpClassUtils.isImplementationOfInterface(cls, entityInterface)) {
				continue;
			}
			Field field = cls.findFieldByName(fieldReference.getName(), false);
			if (field instanceof PhpDocProperty && field.getParent().getFirstChild().getText().equals("@property-read")) {
				return true;
			}
		}
		return false;
	}

	private class SetReadOnlyValueFix implements LocalQuickFix
	{

		@Nls
		@NotNull
		@Override
		public String getFamilyName()
		{
			return "Change to setReadOnlyValue";
		}

		@Override
		public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor)
		{
			AssignmentExpression assignment = (AssignmentExpression) problemDescriptor.getPsiElement();
			FieldReference fieldReference = (FieldReference) assignment.getVariable();

			MethodReference ref = PhpPsiElementFactory.createMethodReference(project, fieldReference.getClassReference().getText()
				+ "->setReadOnlyValue('" + fieldReference.getName() + "', "
				+ assignment.getValue().getText() + ")"
			);
			assignment.replace(ref);
		}
	}
}
