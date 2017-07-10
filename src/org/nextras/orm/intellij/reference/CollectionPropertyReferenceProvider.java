package org.nextras.orm.intellij.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CollectionPropertyReferenceProvider extends PsiReferenceProvider {
	private static Pattern fieldExpression = Pattern.compile("^([\\w\\\\]+(?:->\\w*)*)(!|!=|<=|>=|=|>|<)?$");

	@NotNull
	@Override
	public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
		MethodReference ref = getMethodReference(psiElement);
		if (ref == null) {
			return new PsiReference[0];
		}

		String content = ((StringLiteralExpression) psiElement).getContents();
		Matcher matcher = fieldExpression.matcher(content);
		if (!matcher.matches()) {
			return new PsiReference[0];
		}


		String[] parts = matcher.group(1).split("->", -1);
		if (parts.length == 0) {
			return new PsiReference[0];
		}
		Collection<PsiReference> result = processExpression((StringLiteralExpression) psiElement, ref, parts, (String) processingContext.get("field"));
		return result.toArray(new PsiReference[result.size()]);
	}

	@Nullable
	private MethodReference getMethodReference(PsiElement el) {
		// incomplete array
		if (el.getParent().getNode().getElementType() == PhpElementTypes.ARRAY_VALUE
			&& ((PhpPsiElement) el.getParent()).getPrevPsiSibling() == null
			&& el.getParent().getParent() instanceof ArrayCreationExpression
			&& el.getParent().getParent().getParent() instanceof ParameterList
			&& el.getParent().getParent().getParent().getParent() instanceof MethodReference) {
			MethodReference ref = (MethodReference) el.getParent().getParent().getParent().getParent();
			if (ref.getName().equals("findBy") || ref.getName().equals("getBy")) {
				return ref;
			}
			return null;
		}
		if (el.getParent().getNode().getElementType() == PhpElementTypes.ARRAY_KEY
			&& el.getParent().getParent() instanceof ArrayHashElement
			&& el.getParent().getParent().getParent() instanceof ArrayCreationExpression
			&& el.getParent().getParent().getParent().getParent() instanceof ParameterList
			&& el.getParent().getParent().getParent().getParent().getParent() instanceof MethodReference) {
			MethodReference ref = (MethodReference) el.getParent().getParent().getParent().getParent().getParent();
			if (ref.getName().equals("findBy") || ref.getName().equals("getBy")) {
				return ref;
			}
			return null;
		}
		if (el.getParent() instanceof ParameterList && el.getParent().getParent() instanceof MethodReference) {
			MethodReference ref = (MethodReference) el.getParent().getParent();
			if (ref.getName().equals("orderBy")) {
				return ref;
			}
		}

		return null;
	}

	private Collection<PsiReference> processExpression(StringLiteralExpression el, MethodReference ref, String[] parts, @Nullable String fieldName) {
		Collection<PsiReference> result = new ArrayList<>();
		if (parts.length > 1 && fieldName == null) {
			result.add(new CollectionClassReference(el, ref, parts[0]));
		}
		for (int i = parts.length == 1 ? 0 : 1; i < parts.length; i++) {
			if ((!parts[i].equals("") && fieldName == null) || (fieldName != null && fieldName.equals(parts[i]))) {
				result.add(new CollectionPropertyReference(el, ref, parts, i));
			}
		}

		return result;
	}

}
