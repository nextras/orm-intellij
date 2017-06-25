package org.nextras.orm.intellij.actions.generation;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.ide.util.MemberChooser;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.PhpLanguage;
import com.jetbrains.php.lang.actions.PhpNamedElementNode;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty;
import com.jetbrains.php.lang.documentation.phpdoc.psi.impl.PhpDocPropertyImpl;
import com.jetbrains.php.lang.lexer.PhpTokenTypes;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.parser.PhpStubElementTypes;
import com.jetbrains.php.lang.psi.PhpCodeEditUtil;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.refactoring.importReferences.PhpClassReferenceResolver;
import org.jetbrains.annotations.NotNull;
import org.nextras.orm.intellij.utils.OrmUtils;
import org.nextras.orm.intellij.utils.PhpClassUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public abstract class GenerateActionHandler implements CodeInsightActionHandler
{
	public boolean isValidForFile(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file)
	{
		if (!(file instanceof PhpFile)) {
			return false;
		}

		PhpClass phpClass = PhpCodeEditUtil.findClassAtCaret(editor, file);
		if (phpClass == null) {
			return false;
		}

		PhpIndex phpIndex = PhpIndex.getInstance(project);

		return OrmUtils.isEntity(phpClass, phpIndex);
	}


	@Override
	public void invoke(@NotNull final Project project, @NotNull final Editor editor, @NotNull final PsiFile file)
	{
		final PhpFile phpFile = (PhpFile) file;
		final PhpClass phpClass = PhpCodeEditUtil.findClassAtCaret(editor, phpFile);

		Collection<Field> fields = getFields(phpClass);
		PhpNamedElementNode[] nodes = this.convertToNodes(fields);

		MemberChooser<PhpNamedElementNode> chooser = new MemberChooser<PhpNamedElementNode>(nodes, false, false, project);
		chooser.setTitle("Choose Fields");
		chooser.setCopyJavadocVisible(false);
		boolean isOk = chooser.showAndGet();
		List list = chooser.getSelectedElements();

		if (!isOk || list == null || list.size() == 0) {
			return;
		}

		final PhpNamedElementNode[] members = (PhpNamedElementNode[]) list.toArray(new PhpNamedElementNode[list.size()]);
		final int insertPos = getSuitableEditorPosition(editor, phpFile);

		CommonCodeStyleSettings settings = CodeStyleSettingsManager.getInstance().getCurrentSettings().getCommonSettings(PhpLanguage.INSTANCE);
		boolean currLineBreaks = settings.KEEP_LINE_BREAKS;
		int currBlankLines = settings.KEEP_BLANK_LINES_IN_CODE;
		settings.KEEP_LINE_BREAKS = false;
		settings.KEEP_BLANK_LINES_IN_CODE = 0;

		ApplicationManager.getApplication().runWriteAction(new Runnable() {
			public void run() {
				PhpClassReferenceResolver resolver = new PhpClassReferenceResolver();
				StringBuffer textBuf = new StringBuffer();

				for (PhpNamedElementNode member : members) {
					PsiElement field = member.getPsiElement();
					textBuf.append('\n');
					textBuf.append(GenerateActionHandler.this.createAccessors((Field) field, project));
				}

				if (textBuf.length() > 0 && insertPos >= 0) {
					editor.getDocument().insertString(insertPos, textBuf);
					int endPos = insertPos + textBuf.length();
					CodeStyleManager.getInstance(project).reformatText(phpFile, insertPos, endPos);
					PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());

					/*List var17 = PhpGenerateFieldAccessorHandlerBase.collectInsertedElements(file, insertPos, insertedElementCount);
					if(var17 != null && !var17.isEmpty()) {
						PhpPsiElement var18 = PhpCodeInsightUtil.findScopeForUseOperator((PsiElement) var17.get(0));
						if(var18 != null) {
							resolver.importReferences(var18, var17);
						}
					}*/
				}
			}
		});

		settings.KEEP_LINE_BREAKS = currLineBreaks;
		settings.KEEP_BLANK_LINES_IN_CODE = currBlankLines;
	}


	protected abstract boolean canShow(PhpDocProperty property, PhpClass phpClass);


	protected abstract String createAccessors(Field field, Project project);


	@Override
	public boolean startInWriteAction()
	{
		return true;
	}


	private Collection<Field> getFields(PhpClass phpClass)
	{
		ArrayList<Field> fields = new ArrayList<Field>();
		for (Field field : phpClass.getFields()) {
			if (field instanceof PhpDocPropertyImpl && GenerateActionHandler.this.canShow((PhpDocProperty) field, phpClass)) {
				fields.add(field);
			}
		}

		return fields;
	}


	private PhpNamedElementNode[] convertToNodes(Collection<Field> fields)
	{
		ArrayList<PhpNamedElementNode> nodes = new ArrayList<PhpNamedElementNode>();
		for (Field field : fields) {
			nodes.add(new PhpNamedElementNode(field));
		}
		return nodes.toArray(new PhpNamedElementNode[nodes.size()]);
	}


	private static int getSuitableEditorPosition(Editor editor, PhpFile phpFile)
	{
		PsiElement currElement = phpFile.findElementAt(editor.getCaretModel().getOffset());
		if (currElement != null) {
			PsiElement parent = currElement.getParent();

			for (PsiElement prevParent = currElement; parent != null && !(parent instanceof PhpFile); parent = parent.getParent()) {
				if (isClassMember(parent)) {
					return getNextPos(parent);
				}

				if (parent instanceof PhpClass) {
					while (prevParent != null) {
						if (isClassMember(prevParent) || prevParent.getNode().getElementType() == PhpTokenTypes.chLBRACE) {
							return getNextPos(prevParent);
						}

						prevParent = prevParent.getPrevSibling();
					}

					for (PsiElement classChild = parent.getFirstChild(); classChild != null; classChild = classChild.getNextSibling()) {
						if (classChild.getNode().getElementType() == PhpTokenTypes.chLBRACE) {
							return getNextPos(classChild);
						}
					}
				}

				prevParent = parent;
			}
		}

		return -1;
	}


	private static boolean isClassMember(PsiElement element)
	{
		if (element == null) {
			return false;
		} else {
			IElementType elementType = element.getNode().getElementType();
			return elementType == PhpElementTypes.CLASS_FIELDS || elementType == PhpElementTypes.CLASS_CONSTANTS || elementType == PhpStubElementTypes.CLASS_METHOD;
		}
	}


	private static int getNextPos(PsiElement element)
	{
		PsiElement next = element.getNextSibling();
		return next != null ? next.getTextOffset() : -1;
	}

}
