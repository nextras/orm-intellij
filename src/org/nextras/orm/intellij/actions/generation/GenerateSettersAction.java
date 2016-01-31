package org.nextras.orm.intellij.actions.generation;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.actions.CodeInsightAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;


public class GenerateSettersAction extends CodeInsightAction
{
	private final GenerateActionHandler actionHandler = new GenerateActionHandler()
	{
		@Override
		protected boolean canShow(PhpDocProperty property, PhpClass phpClass)
		{
			String name = property.getName();
			String methodName = "setter" + name.substring(0, 1).toUpperCase() + name.substring(1);
			Method method = phpClass.findMethodByName(methodName);
			return method == null;
		}

		@Override
		protected String createAccessors(Field field, Project project)
		{
			String name = field.getName();
			String methodName = "setter" + name.substring(0, 1).toUpperCase() + name.substring(1);
			Method method = PhpPsiElementFactory.createMethod(project, "protected function " + methodName + "($value) { return $value; }");
			return method.getText();
		}
	};


	public GenerateSettersAction()
	{
	}


	@Override
	protected boolean isValidForFile(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file)
	{
		return this.actionHandler.isValidForFile(project, editor, file);
	}


	@NotNull
	@Override
	protected CodeInsightActionHandler getHandler()
	{
		return this.actionHandler;
	}
}
