package org.nextras.orm.intellij;

import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.ID;
import com.jetbrains.php.PhpCaches;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Constant;
import com.jetbrains.php.lang.psi.elements.Function;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.PhpNamespace;
import com.jetbrains.php.lang.psi.elements.PhpTypedElement;
import com.jetbrains.php.lang.psi.elements.PhpUse;
import com.jetbrains.php.lang.psi.elements.Variable;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

public class PhpIndexFix extends PhpIndex {

	PhpIndex index;

	Project project;

	public PhpIndexFix(PhpIndex index, Project project) {
		this.index = index;
		this.project = project;
	}

	@NotNull
	public static PhpIndex getInstance(@NotNull Project project) {
		return PhpIndex.getInstance(project);
	}

	@Override
	public Collection<PhpNamespace> getNamespacesByName(String name) {
		return index.getNamespacesByName(name);
	}

	@Override
	@NotNull
	public Collection<String> getAllConstantNames(@Nullable PrefixMatcher prefixMatcher) {
		return index.getAllConstantNames(prefixMatcher);
	}

	@Override
	@NotNull
	public Collection<String> getAllVariableNames(@Nullable PrefixMatcher prefixMatcher) {
		return index.getAllVariableNames(prefixMatcher);
	}

	@Override
	@NotNull
	public Collection<String> getAllFunctionNames(@Nullable PrefixMatcher prefixMatcher) {
		return index.getAllFunctionNames(prefixMatcher);
	}

	@Override
	@NotNull
	public Collection<String> getAllClassNames(@Nullable PrefixMatcher prefixMatcher) {
		return index.getAllClassNames(prefixMatcher);
	}

	@Override
	@NotNull
	public Collection<String> getAllInterfaceNames() {
		return index.getAllInterfaceNames();
	}

	@Override
	@NotNull
	public Collection<String> getAllTraitNames() {
		return index.getAllTraitNames();
	}

	@Override
	@NotNull
	public Collection<String> getChildNamespacesByParentName(@Nullable String name) {
		return index.getChildNamespacesByParentName(name);
	}

	@Override
	@NotNull
	public Collection<String> getTraitUsagesByFQN(@Nullable String name) {
		return index.getTraitUsagesByFQN(name);
	}

	@Override
	public Collection<PhpUse> getUseAliasesByName(@Nullable String name) {
		return index.getUseAliasesByName(name);
	}

	@Override
	@NotNull
	public Collection<PhpUse> getUseAliasesByReferenceName(@Nullable String name) {
		return index.getUseAliasesByReferenceName(name);
	}

	@Override
	@NotNull
	public Collection<Constant> getConstantsByFQN(@Nullable String fqn) {
		return index.getConstantsByFQN(fqn);
	}

	@Override
	@NotNull
	public Collection<Constant> getConstantsByName(@Nullable String name) {
		return index.getConstantsByName(name);
	}

	@Override
	@NotNull
	public Collection<Variable> getVariablesByName(@Nullable String name) {
		return index.getVariablesByName(name);
	}

	@Override
	@NotNull
	public Collection<Function> getFunctionsByName(@Nullable String name) {
		return index.getFunctionsByName(name);
	}

	@Override
	@NotNull
	public Collection<Function> getFunctionsByFQN(@Nullable String fqn) {
		return index.getFunctionsByFQN(fqn);
	}

	@Override
	@NotNull
	public Collection<PhpClass> getInterfacesByName(@Nullable String name) {
		return index.getInterfacesByName(name);
	}

	@Override
	public Collection<PhpClass> getTraitsByName(String name) {
		return index.getTraitsByName(name);
	}

	@Override
	@NotNull
	public Collection<PhpClass> getClassesByName(@Nullable String name) {
		return index.getClassesByName(name);
	}

	@Override
	@NotNull
	public Collection<PhpClass> getClassesByNameInScope(@Nullable String name, GlobalSearchScope scope) {
		return index.getClassesByNameInScope(name, scope);
	}

	@Override
	@Nullable
	public PhpClass getClassByName(@Nullable String name) {
		return index.getClassByName(name);
	}

	@Override
	@NotNull
	public Collection<PhpClass> getDirectSubclasses(@Nullable String fqn) {
		return index.getDirectSubclasses(fqn);
	}

	@Override
	@NotNull
	public Collection<PhpClass> getAllSubclasses(@Nullable String fqn) {
		return index.getAllSubclasses(fqn);
	}

	@Override
	public GlobalSearchScope getSearchScope() {
		return index.getSearchScope();
	}

	@Override
	@NotNull
	public Collection<? extends PhpNamedElement> getBySignature(@NotNull String s) {
		return index.getBySignature(s);
	}

	@Override
	@NotNull
	public Collection<? extends PhpNamedElement> getBySignature(@NotNull String s, @Nullable Set<String> visited, int depth) {
		return index.getBySignature(s, visited, depth);
	}

	@Override
	@NotNull
	protected Collection<? extends PhpNamedElement> getBySignatureInternal(@NotNull String s, @Nullable Set<String> visited, int depth) {
		return Collections.emptyList();
	}

	@Override
	public Collection<PhpClass> getClasses(@Nullable Set<String> visited, @NotNull String classRef) {
		return index.getClasses(visited, classRef);
	}

	@Override
	@NotNull
	public Collection<PhpClass> getClassesByFQN(String fqn) {
		return index.getClassesByFQN(fqn);
	}

	@Override
	@NotNull
	public Collection<PhpClass> getInterfacesByFQN(String fqn) {
		return index.getInterfacesByFQN(fqn);
	}

	@Override
	@NotNull
	public Collection<PhpClass> getTraitsByFQN(String fqn) {
		return index.getTraitsByFQN(fqn);
	}

	@Override
	@NotNull
	public Collection<PhpClass> getAnyByFQN(String fqn) {
		return index.getAnyByFQN(fqn);
	}

	@Override
	public Collection<PhpClass> getTraitUsages(PhpClass me) {
		return index.getTraitUsages(me);
	}

	@Override
	public Collection<PhpClass> getNestedTraitUsages(PhpClass me, @Nullable Collection<String> visited) {
		return index.getNestedTraitUsages(me, visited);
	}

	@Override
	public Collection<String> filterKeys(Collection<String> keys, ID id) {
		return Collections.emptyList();
	}

	@Override
	@NotNull
	public <T extends PhpNamedElement> Collection<T> filterByNamespace(@NotNull Collection<T> elements, @Nullable String namespaceName, boolean allowGlobal) {
		return index.filterByNamespace(elements, namespaceName, allowGlobal);
	}

	@Override
	@NotNull
	public <T extends PhpNamedElement> Collection<T> filterByNamespace(@NotNull Collection<T> elements, @Nullable String namespaceName) {
		return index.filterByNamespace(elements, namespaceName);
	}

	@NotNull
	public PhpType completeThis(@NotNull PhpType rawTypeInput, @Nullable String thisClass, @Nullable Set<String> visited) {
		PhpType out = new PhpType();
		Iterator var5 = rawTypeInput.getTypes().iterator();

		while(true) {
			while(var5.hasNext()) {
				String raw = (String)var5.next();

				StringBuilder rawArray;
				for(rawArray = new StringBuilder(); raw.endsWith("[]"); raw = raw.substring(0, raw.length() - 2)) {
					rawArray.append("[]");
				}

				if (raw.startsWith("#")) {
					String classRef;
					if (raw.contains("#Cstatic.")) {
						classRef = "#C" + thisClass + ".";
						raw = StringUtil.replace(raw, "#Cstatic.", classRef);
					}

					classRef = StringUtil.trimStart(raw, "#E");
					int dot = classRef.lastIndexOf(46);
					classRef = dot < 2 ? classRef : classRef.substring(2, dot);
					classRef = StringUtil.trimStart(classRef, "#E");
					PhpType global = (new PhpType()).add(raw).global(this.project);
					PhpType aware = this.completeThis(global, classRef, visited);
					for (String type : aware.getTypes()) {
						out.add(type + rawArray);
					}
				} else if (!"static".equals(raw) && !"$this".equals(raw) && !"self".equals(raw) && !"\\___PHPSTORM_HELPERS\\static".equals(raw) && !"\\___PHPSTORM_HELPERS\\this".equals(raw)) {
					out.add(raw + rawArray);
				} else if (thisClass != null) {
					PhpType aware = this.completeThis((new PhpType()).add(thisClass), (String)null, visited);
					Iterator var9 = aware.getTypes().iterator();

					while(var9.hasNext()) {
						String s = (String)var9.next();
						out.add(s + rawArray);
					}
				}
			}

			return out;
		}
	}

	@NotNull
	public PhpType completeType(@NotNull Project p, @NotNull PhpType type, @NotNull Set<String> visited) {
		PhpType completeType;
		for(; !type.isComplete(); type = completeType) {
			completeType = new PhpType();
			Collection<String> types = type.getTypes();
			Iterator var6 = types.iterator();

			while(var6.hasNext()) {
				String typeName = (String)var6.next();
				long t = System.currentTimeMillis();
				if (typeName.length() > 1 && typeName.charAt(0) == '#') {
					if (!visited.contains(typeName)) {
						visited.add(typeName);
						StringBuilder rawArray;
						for(rawArray = new StringBuilder(); typeName.endsWith("[]"); typeName = typeName.substring(0, typeName.length() - 2)) {
							rawArray.append("[]");
						}
						PhpType cacheInfo = (PhpType) PhpCaches.getInstance(p).TYPE_COMPLETION_CACHE.get(typeName);
						if (cacheInfo != null) {
							completeType.add(cacheInfo);
						} else {
							cacheInfo = new PhpType();
							Collection<? extends PsiElement> targets = getInstance(p).getBySignature(typeName, visited, 0);
							if (!targets.isEmpty()) {
								Iterator var12 = targets.iterator();

								while(var12.hasNext()) {
									PsiElement element = (PsiElement)var12.next();
									if (element instanceof PhpTypedElement) {
										for (String typeStr : ((PhpTypedElement) element).getType().getTypes()) {
											cacheInfo.add(typeStr + rawArray);
											completeType.add(typeStr + rawArray);
										}
									}
								}
							} else {
								cacheInfo.add("?" + typeName);
								completeType.add("?" + typeName);
							}

							PhpCaches.getInstance(p).TYPE_COMPLETION_CACHE.put(typeName, cacheInfo);
						}
					} else {
						completeType.add("?" + typeName);
					}
				} else {
					completeType.add(typeName);
				}

				if (completeType.size() > 15) {
					break;
				}
			}
		}

		return type;
	}
}
