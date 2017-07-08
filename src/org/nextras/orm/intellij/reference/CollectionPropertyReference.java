package org.nextras.orm.intellij.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nextras.orm.intellij.utils.OrmUtils;
import org.nextras.orm.intellij.utils.PhpIndexUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class CollectionPropertyReference extends PsiPolyVariantReferenceBase<StringLiteralExpression>
{

	private MethodReference methodReference;

	private String[] parts;

	private int pos;

	public CollectionPropertyReference(StringLiteralExpression element, MethodReference methodReference, String[] parts, int pos)
	{
		super(element);
		this.methodReference = methodReference;
		this.parts = parts;
		this.pos = pos;
		int start = String.join("->", Arrays.copyOfRange(parts, 0, pos)).length() + (pos == 0 ? 1 : 3);
		setRangeInElement(new TextRange(start, start + parts[pos].length()));
	}

	@NotNull
	@Override
	public ResolveResult[] multiResolve(boolean b)
	{
		Collection<PhpClass> classes;
		PhpIndex index = PhpIndex.getInstance(getElement().getProject());
		if (pos == 0 || parts[0].equals("this")) {
			Collection<PhpClass> repositories = OrmUtils.findQueriedRepositories(methodReference);
			classes = OrmUtils.findRepositoryEntities(repositories);
		} else {
			classes = PhpIndexUtils.getByType(new PhpType().add(parts[0]), index);
		}
		Collection<Field> fields = new HashSet<>();
		for (int i = pos == 0 ? 0 : 1; i <= pos; i++) {
			Collection<PhpClass> newClasses = new HashSet<>();
			for (PhpClass cls : classes) {
				Field field = cls.findFieldByName(parts[i], false);
				if (field == null || !(field instanceof PhpDocProperty)) {
					continue;
				}
				OrmUtils.addEntitiesFromField(newClasses, (PhpDocProperty) field);
				if (i == pos) {
					fields.add(field);
				}
			}
			classes = newClasses;
		}
		ResolveResult[] result = new ResolveResult[fields.size()];
		int i = 0;
		for (Field field : fields) {
			result[i++] = new ResolveResult()
			{
				@Nullable
				@Override
				public PsiElement getElement()
				{
					return field;
				}

				@Override
				public boolean isValidResult()
				{
					return true;
				}
			};
		}

		return result;
	}

	@NotNull
	@Override
	public Object[] getVariants()
	{
		return new Object[0];
	}
}
