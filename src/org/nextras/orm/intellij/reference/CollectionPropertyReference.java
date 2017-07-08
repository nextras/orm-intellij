package org.nextras.orm.intellij.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nextras.orm.intellij.utils.OrmUtils;

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
		this.parts = Arrays.copyOfRange(parts, 0, pos + 1);
		this.pos = pos;
		int end = String.join("->", this.parts).length() + 1;
		setRangeInElement(new TextRange(end - parts[pos].length(), end));
	}


	@NotNull
	@Override
	public ResolveResult[] multiResolve(boolean b)
	{
		Collection<PhpClass> classes = OrmUtils.findQueriedEntities(methodReference, this.parts);
		Collection<Field> fields = new HashSet<>();
		for (PhpClass cls : classes) {
			Field field = cls.findFieldByName(parts[pos], false);
			if (field == null || !(field instanceof PhpDocProperty)) {
				continue;
			}
			fields.add(field);
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
