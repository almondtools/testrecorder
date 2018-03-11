package net.amygdalum.testrecorder.util;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.extensions.assertj.conventions.DefaultEquality.defaultEquality;
import static net.amygdalum.extensions.assertj.conventions.UtilityClass.utilityClass;
import static net.amygdalum.testrecorder.util.Types.allFields;
import static net.amygdalum.testrecorder.util.Types.allMethods;
import static net.amygdalum.testrecorder.util.Types.array;
import static net.amygdalum.testrecorder.util.Types.assignableTypes;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.boxedType;
import static net.amygdalum.testrecorder.util.Types.boxingEquivalentTypes;
import static net.amygdalum.testrecorder.util.Types.component;
import static net.amygdalum.testrecorder.util.Types.equalTypes;
import static net.amygdalum.testrecorder.util.Types.getDeclaredConstructor;
import static net.amygdalum.testrecorder.util.Types.getDeclaredField;
import static net.amygdalum.testrecorder.util.Types.getDeclaredMethod;
import static net.amygdalum.testrecorder.util.Types.inferType;
import static net.amygdalum.testrecorder.util.Types.innerType;
import static net.amygdalum.testrecorder.util.Types.isBoxedPrimitive;
import static net.amygdalum.testrecorder.util.Types.isFinal;
import static net.amygdalum.testrecorder.util.Types.isHidden;
import static net.amygdalum.testrecorder.util.Types.isLiteral;
import static net.amygdalum.testrecorder.util.Types.isPrimitive;
import static net.amygdalum.testrecorder.util.Types.isStatic;
import static net.amygdalum.testrecorder.util.Types.isUnhandledSynthetic;
import static net.amygdalum.testrecorder.util.Types.needsCast;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.typeArgument;
import static net.amygdalum.testrecorder.util.Types.wildcard;
import static net.amygdalum.testrecorder.util.Types.wildcardExtends;
import static net.amygdalum.testrecorder.util.Types.wildcardSuper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import net.amygdalum.extensions.assertj.conventions.DefaultEquality;
import net.amygdalum.testrecorder.util.TypesTest.NestedPackagePrivate;
import net.amygdalum.testrecorder.util.TypesTest.NestedProtected;
import net.amygdalum.testrecorder.util.TypesTest.NestedPublic;
import net.amygdalum.testrecorder.util.testobjects.BiGeneric;
import net.amygdalum.testrecorder.util.testobjects.BoundGeneric;
import net.amygdalum.testrecorder.util.testobjects.Complex;
import net.amygdalum.testrecorder.util.testobjects.ElevatingToPublic;
import net.amygdalum.testrecorder.util.testobjects.Final;
import net.amygdalum.testrecorder.util.testobjects.Generic;
import net.amygdalum.testrecorder.util.testobjects.GenericCycle;
import net.amygdalum.testrecorder.util.testobjects.PartlyBoundBiGeneric;
import net.amygdalum.testrecorder.util.testobjects.PseudoSynthetic;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.util.testobjects.Static;
import net.amygdalum.testrecorder.util.testobjects.Sub;
import net.amygdalum.testrecorder.util.testobjects.Sub1;
import net.amygdalum.testrecorder.util.testobjects.Sub2;
import net.amygdalum.testrecorder.util.testobjects.SubGeneric;
import net.amygdalum.testrecorder.util.testobjects.Super;

public class TypesTest {

	@Test
	public void testTypes() throws Exception {
		assertThat(Types.class).satisfies(utilityClass().conventions());
	}

	@Test
	public void testBaseTypeOnSimpleTypes() throws Exception {
		assertThat(baseType(Object.class)).isEqualTo(Object.class);
		assertThat(baseType(String.class)).isEqualTo(String.class);
		assertThat(baseType(StringTokenizer.class)).isEqualTo(StringTokenizer.class);
	}

	@Test
	public void testBaseTypeOnPrimitiveTypes() throws Exception {
		assertThat(baseType(int.class)).isEqualTo(int.class);
		assertThat(baseType(void.class)).isEqualTo(void.class);
	}

	@Test
	public void testBaseTypeOnParameterizedTypes() throws Exception {
		assertThat(baseType(parameterized(List.class, List.class, String.class))).isEqualTo(List.class);
		assertThat(baseType(parameterized(Map.class, Map.class, String.class, Object.class))).isEqualTo(Map.class);
	}

	@Test
	public void testBaseTypeOnGenericArrayTypes() throws Exception {
		assertThat(baseType(array(parameterized(List.class, List.class, String.class)))).isEqualTo(List[].class);
		assertThat(baseType(array(parameterized(Map.class, Map.class, String.class, Object.class)))).isEqualTo(Map[].class);
	}

	@Test
	public void testBaseTypeOnOtherTypes() throws Exception {
		assertThat(baseType(wildcard())).isEqualTo(Object.class);
		assertThat(baseType(wildcardExtends(String.class))).isEqualTo(Object.class);
		assertThat(baseType(wildcardSuper(String.class))).isEqualTo(Object.class);
	}

	@Test
	public void testBoxedType() throws Exception {
		assertThat(boxedType(byte.class)).isEqualTo(Byte.class);
		assertThat(boxedType(short.class)).isEqualTo(Short.class);
		assertThat(boxedType(int.class)).isEqualTo(Integer.class);
		assertThat(boxedType(long.class)).isEqualTo(Long.class);
		assertThat(boxedType(float.class)).isEqualTo(Float.class);
		assertThat(boxedType(double.class)).isEqualTo(Double.class);
		assertThat(boxedType(char.class)).isEqualTo(Character.class);
		assertThat(boxedType(boolean.class)).isEqualTo(Boolean.class);

		assertThat(boxedType(void.class)).isEqualTo(Void.class);

		assertThat(boxedType(Integer.class)).isEqualTo(Integer.class);

		assertThat(boxedType(parameterized(List.class, List.class, String.class))).isEqualTo(List.class);
	}

	@Test
	public void testComponent() throws Exception {
		assertThat(component(int[].class)).isEqualTo(int.class);
		assertThat(component(Integer[].class)).isEqualTo(Integer.class);

		Type parameterized = parameterized(List.class, List.class, Integer.class);
		assertThat(component(array(parameterized))).isEqualTo(parameterized);

		assertThat(component(Object.class)).isEqualTo(Object.class);
	}

	@Test
	public void testAssignableTypes() throws Exception {
		assertThat(assignableTypes(String.class, String.class)).isTrue();
		assertThat(assignableTypes(Object.class, String.class)).isTrue();
		assertThat(assignableTypes(String.class, Object.class)).isFalse();
		assertThat(assignableTypes(Integer.class, String.class)).isFalse();
	}

	@Test
	public void testEqualTypes() throws Exception {
		assertThat(equalTypes(parameterized(List.class, List.class, String.class), List.class)).isTrue();
		assertThat(equalTypes(parameterized(Set.class, Set.class, String.class), List.class)).isFalse();
		assertThat(equalTypes(parameterized(Set.class, Set.class, String.class), parameterized(Set.class, Set.class, Object.class))).isTrue();
	}

	@Test
	public void testBoxingEquivalentTypes() throws Exception {
		assertThat(boxingEquivalentTypes(byte.class, Byte.class)).isTrue();
		assertThat(boxingEquivalentTypes(short.class, Short.class)).isTrue();
		assertThat(boxingEquivalentTypes(int.class, Integer.class)).isTrue();
		assertThat(boxingEquivalentTypes(long.class, Long.class)).isTrue();
	}

	@Test
	public void testInferType() throws Exception {
		assertThat(inferType(ArrayList.class)).isEqualTo(List.class);
		assertThat(inferType(ArrayList.class, Collection.class)).isEqualTo(Collection.class);
		assertThat(inferType(List.class, Set.class)).isEqualTo(Collection.class);
		assertThat(inferType(HashSet.class, ArrayList.class)).isEqualTo(Collection.class);
		assertThat(inferType(parameterized(HashSet.class, String.class), parameterized(ArrayList.class, Object.class))).isEqualTo(Collection.class);
		assertThat(inferType(String.class, List.class)).isEqualTo(Object.class);
		assertThat(inferType(Sub1.class, Sub2.class)).isEqualTo(Super.class);
		assertThat(inferType(Integer.class, Integer.class)).isEqualTo(Integer.class);
		assertThat(inferType(String.class, String.class)).isEqualTo(String.class);
	}

	@Test
	public void testIsBoxedPrimitive() throws Exception {
		assertThat(isBoxedPrimitive(Byte.class)).isTrue();
		assertThat(isBoxedPrimitive(Short.class)).isTrue();
		assertThat(isBoxedPrimitive(Integer.class)).isTrue();
		assertThat(isBoxedPrimitive(Long.class)).isTrue();
		assertThat(isBoxedPrimitive(Float.class)).isTrue();
		assertThat(isBoxedPrimitive(Double.class)).isTrue();
		assertThat(isBoxedPrimitive(Boolean.class)).isTrue();
		assertThat(isBoxedPrimitive(Character.class)).isTrue();

		assertThat(isBoxedPrimitive(String.class)).isFalse();
		assertThat(isBoxedPrimitive(Object.class)).isFalse();
		assertThat(isBoxedPrimitive(List.class)).isFalse();
		assertThat(isBoxedPrimitive(parameterized(List.class, List.class, String.class))).isFalse();
		assertThat(isBoxedPrimitive(Super.class)).isFalse();
	}

	@Test
	public void testIsPrimitive() throws Exception {
		assertThat(isPrimitive(byte.class)).isTrue();
		assertThat(isPrimitive(short.class)).isTrue();
		assertThat(isPrimitive(int.class)).isTrue();
		assertThat(isPrimitive(long.class)).isTrue();
		assertThat(isPrimitive(float.class)).isTrue();
		assertThat(isPrimitive(double.class)).isTrue();
		assertThat(isPrimitive(boolean.class)).isTrue();
		assertThat(isPrimitive(char.class)).isTrue();

		assertThat(isPrimitive(Byte.class)).isFalse();
		assertThat(isPrimitive(Short.class)).isFalse();
		assertThat(isPrimitive(Integer.class)).isFalse();
		assertThat(isPrimitive(Long.class)).isFalse();
		assertThat(isPrimitive(Float.class)).isFalse();
		assertThat(isPrimitive(Double.class)).isFalse();
		assertThat(isPrimitive(Boolean.class)).isFalse();
		assertThat(isPrimitive(Character.class)).isFalse();
		assertThat(isPrimitive(String.class)).isFalse();
		assertThat(isPrimitive(Object.class)).isFalse();
		assertThat(isPrimitive(List.class)).isFalse();
		assertThat(isPrimitive(Super.class)).isFalse();
		assertThat(isPrimitive(wildcard())).isFalse();
	}

	@Test
	public void testIsLiteral() throws Exception {
		assertThat(isLiteral(byte.class)).isTrue();
		assertThat(isLiteral(short.class)).isTrue();
		assertThat(isLiteral(int.class)).isTrue();
		assertThat(isLiteral(long.class)).isTrue();
		assertThat(isLiteral(float.class)).isTrue();
		assertThat(isLiteral(double.class)).isTrue();
		assertThat(isLiteral(boolean.class)).isTrue();
		assertThat(isLiteral(char.class)).isTrue();

		assertThat(isLiteral(Byte.class)).isTrue();
		assertThat(isLiteral(Short.class)).isTrue();
		assertThat(isLiteral(Integer.class)).isTrue();
		assertThat(isLiteral(Long.class)).isTrue();
		assertThat(isLiteral(Float.class)).isTrue();
		assertThat(isLiteral(Double.class)).isTrue();
		assertThat(isLiteral(Boolean.class)).isTrue();
		assertThat(isLiteral(Character.class)).isTrue();

		assertThat(isLiteral(String.class)).isTrue();
		assertThat(isLiteral(Object.class)).isFalse();
		assertThat(isLiteral(List.class)).isFalse();
		assertThat(isLiteral(Super.class)).isFalse();
		assertThat(isLiteral(BigDecimal.class)).isFalse();
		assertThat(isLiteral(Collection.class)).isFalse();
	}

	@Test
	public void testTypeArgument() throws Exception {
		assertThat(typeArgument(parameterized(List.class, null, String.class), 0).get()).isEqualTo(String.class);
		assertThat(typeArgument(parameterized(Map.class, null, String.class, Object.class), 0).get()).isEqualTo(String.class);
		assertThat(typeArgument(parameterized(Map.class, null, String.class, Object.class), 1).get()).isEqualTo(Object.class);
		assertThat(typeArgument(parameterized(Map.class, null), 1).isPresent()).isFalse();
		assertThat(typeArgument(parameterized(Map.class, null, (Type[]) null), 1).isPresent()).isFalse();
		assertThat(typeArgument(Map.class, 0).isPresent()).isFalse();
		assertThat(typeArgument(String.class, 0).isPresent()).isFalse();
	}

	@Test
	public void testInnerType() throws Exception {
		assertThat(innerType(TypesTest.class, "NestedPublic")).isEqualTo(NestedPublic.class);
	}

	@Test
	public void testInnerTypeNotResolved() throws Exception {
		assertThatThrownBy(() -> innerType(TypesTest.class, "NotExistent")).isInstanceOf(TypeNotPresentException.class);
	}

	@Test
	public void testIsHiddenType() throws Exception {
		assertThat(isHidden(TypesTest.class, "any")).isFalse();
		assertThat(isHidden(new NestedPrivate() {
		}.getClass(), "any")).isTrue();
		assertThat(isHidden(NestedPrivate.class, "any")).isTrue();
		assertThat(isHidden(NestedPackagePrivate.class, "any")).isTrue();
		assertThat(isHidden(NestedPackagePrivate.class, "net.amygdalum.testrecorder.util")).isFalse();
		assertThat(isHidden(NestedProtected.class, "net.amygdalum.testrecorder.util")).isFalse();
		assertThat(isHidden(NestedProtected.class, "other")).isTrue();
		assertThat(isHidden(TypesPackagePrivate.class, "any")).isTrue();
		assertThat(isHidden(TypesPackagePrivate.class, "net.amygdalum.testrecorder.util")).isFalse();
		assertThat(isHidden(NestedPublic.class, "any")).isFalse();
		assertThat(isHidden(TypesPublic.class, "net.amygdalum.testrecorder.util")).isFalse();
		assertThat(isHidden(TypesPublic.class, "other")).isFalse();
	}

	@Test
	public void testIsHiddenForArrays() throws Exception {
		assertThat(Types.isHidden(TypesPublic[].class, "any")).isFalse();
		assertThat(Types.isHidden(TypesPackagePrivate[].class, "net.amygdalum.testrecorder.util")).isFalse();
		assertThat(Types.isHidden(TypesPackagePrivate[].class, "other")).isTrue();
		assertThat(Types.isHidden(NestedProtected[].class, "net.amygdalum.testrecorder.util")).isFalse();
		assertThat(Types.isHidden(NestedProtected[].class, "other")).isTrue();
		assertThat(Types.isHidden(NestedPrivate[].class, "any")).isTrue();
	}

	@Test
	public void testIsHiddenConstructor() throws Exception {
		assertThat(isHidden(getDeclaredConstructor(TypesTest.class), "any")).isFalse();
		assertThat(isHidden(getDeclaredConstructor(NestedPrivate.class), "any")).isTrue();
		assertThat(isHidden(getDeclaredConstructor(NestedPackagePrivate.class), "any")).isTrue();
		assertThat(isHidden(getDeclaredConstructor(NestedPackagePrivate.class), "net.amygdalum.testrecorder.util")).isTrue();
		assertThat(isHidden(getDeclaredConstructor(TypesPackagePrivate.class), "any")).isTrue();
		assertThat(isHidden(getDeclaredConstructor(TypesPackagePrivate.class), "net.amygdalum.testrecorder.util")).isFalse();
	}

	@Test
	public void testIsHiddenTrueForConstructorsOfNestedTypes() throws Exception {
		assertThat(Types.isHidden(getDeclaredConstructor(NestedConstructors.class), "any")).isTrue();
		assertThat(Types.isHidden(getDeclaredConstructor(NestedConstructors.class, int.class), "any")).isFalse();
		assertThat(Types.isHidden(getDeclaredConstructor(NestedConstructors.class, boolean.class), "any")).isTrue();
		assertThat(Types.isHidden(getDeclaredConstructor(NestedConstructors.class, char.class), "any")).isTrue();
	}

	@Test
	public void testGetDeclaredField() throws Exception {
		assertThat(getDeclaredField(Sub1.class, "subAttr")).isEqualTo(Sub1.class.getDeclaredField("subAttr"));
		assertThat(getDeclaredField(Sub1.class, "str")).isEqualTo(Super.class.getDeclaredField("str"));
		assertThat(getDeclaredField(Sub2.class, "subAttr")).isEqualTo(Sub2.class.getDeclaredField("subAttr"));
		assertThat(getDeclaredField(Sub2.class, "str")).isEqualTo(Super.class.getDeclaredField("str"));
	}

	@Test
	public void testGetDeclaredMethod() throws Exception {
		assertThat(getDeclaredMethod(Sub1.class, "getSubAttr")).isEqualTo(Sub1.class.getDeclaredMethod("getSubAttr"));
		assertThat(getDeclaredMethod(Sub1.class, "getStr")).isEqualTo(Super.class.getDeclaredMethod("getStr"));
		assertThat(getDeclaredMethod(Sub2.class, "setSubAttr", boolean.class)).isEqualTo(Sub2.class.getDeclaredMethod("setSubAttr", boolean.class));
		assertThat(getDeclaredMethod(Sub2.class, "getStr")).isEqualTo(Super.class.getDeclaredMethod("getStr"));
		assertThat(getDeclaredMethod(ElevatingToPublic.class, "method")).isEqualTo(ElevatingToPublic.class.getSuperclass().getDeclaredMethod("method"));
		assertThatThrownBy(() -> getDeclaredMethod(Sub1.class, "nonExistent"))
			.isInstanceOf(NoSuchMethodException.class);
		assertThatThrownBy(() -> getDeclaredMethod(Sub2.class, "nonExistent"))
			.isInstanceOf(NoSuchMethodException.class);
		assertThatThrownBy(() -> getDeclaredMethod(Super.class, "nonExistent"))
			.isInstanceOf(NoSuchMethodException.class);
	}

	@Test
	public void testAllFields() throws Exception {
		List<Field> sub1Fields = allFields(Sub1.class);
		assertThat(sub1Fields).containsExactly(Sub1.class.getDeclaredField("subAttr"), Super.class.getDeclaredField("str"));
		List<Field> sub2Fields = allFields(Sub2.class);
		assertThat(sub2Fields).containsExactly(Sub2.class.getDeclaredField("subAttr"), Super.class.getDeclaredField("str"));
		assertThatThrownBy(() -> getDeclaredField(Sub1.class, "nonExistent"))
			.isInstanceOf(NoSuchFieldException.class);
		assertThatThrownBy(() -> getDeclaredField(Sub2.class, "nonExistent"))
			.isInstanceOf(NoSuchFieldException.class);
		assertThatThrownBy(() -> getDeclaredField(Super.class, "nonExistent"))
			.isInstanceOf(NoSuchFieldException.class);
	}

	@Test
	public void testAllMethods() throws Exception {
		List<Method> sub1Methods = allMethods(Sub1.class);
		assertThat(sub1Methods).containsExactly(Sub1.class.getDeclaredMethod("getSubAttr"), Super.class.getDeclaredMethod("getStr"));
		List<Method> sub2Methods = allMethods(Sub2.class);
		assertThat(sub2Methods).containsExactly(Sub2.class.getDeclaredMethod("setSubAttr", boolean.class), Super.class.getDeclaredMethod("getStr"));
	}

	@Test
	public void testNeedsCast() throws Exception {
		assertThat(needsCast(Object.class, String.class)).isFalse();
		assertThat(needsCast(List.class, parameterized(List.class, null, String.class))).isFalse();
		assertThat(needsCast(int.class, int.class)).isFalse();
		assertThat(needsCast(Integer.class, int.class)).isFalse();
		assertThat(needsCast(int.class, Integer.class)).isFalse();

		assertThat(needsCast(int.class, long.class)).isTrue();
		assertThat(needsCast(long.class, int.class)).isTrue();
		assertThat(needsCast(String.class, Object.class)).isTrue();
	}

	@Test
	public void testIsFinal() throws Exception {
		assertThat(isFinal(Super.class.getDeclaredField("str"))).isFalse();
		assertThat(isFinal(Final.class.getDeclaredField("attr"))).isTrue();
		assertThat(isFinal(Static.class.getDeclaredField("CONSTANT"))).isTrue();
	}

	@Test
	public void testIsStatic() throws Exception {
		assertThat(isStatic(Super.class.getDeclaredField("str"))).isFalse();
		assertThat(isStatic(Static.class.getDeclaredField("global"))).isTrue();
		assertThat(isStatic(Static.class.getDeclaredField("CONSTANT"))).isTrue();
	}

	@Test
	public void testIsUnhandledSynthetic() throws Exception {
		assertThat(isUnhandledSynthetic(Super.class.getDeclaredField("str"))).isFalse();
		assertThat(isUnhandledSynthetic(NestedTypeField.class.getDeclaredField("this$0"))).isFalse();
		assertThat(isUnhandledSynthetic(PseudoSynthetic.class.getDeclaredField("$attr"))).isTrue();
	}

	@Test
	public void testArray() throws Exception {
		assertThat(array(String.class)).isSameAs(String[].class);
		assertThat(array(parameterized(List.class, null, String.class)).getTypeName()).isEqualTo("java.util.List<java.lang.String>[]");
		assertThat(array(parameterized(List.class, null, String.class)).toString()).isEqualTo("java.util.List<java.lang.String>[]");
		assertThat(((GenericArrayType) array(parameterized(List.class, null, String.class))).getGenericComponentType())
			.isEqualTo(parameterized(List.class, null, String.class));
		assertThat(array(parameterized(List.class, null, String.class))).satisfies(defaultEquality()
			.andEqualTo(array(parameterized(List.class, null, String.class)))
			.andNotEqualTo(array(String.class))
			.conventions());
	}

	@Test
	public void testParameterized() throws Exception {
		assertThat(parameterized(List.class, null, String.class).getRawType()).isEqualTo(List.class);
		assertThat(parameterized(List.class, null, String.class).getOwnerType()).isNull();
		assertThat(parameterized(List.class, null, String.class).getActualTypeArguments()).containsExactly(String.class);
		assertThat(parameterized(List.class, null, String.class).getTypeName()).isEqualTo("java.util.List<java.lang.String>");
		assertThat(parameterized(List.class, null).getTypeName()).isEqualTo("java.util.List<>");
		assertThat(parameterized(List.class, null, (Type[]) null).getTypeName()).isEqualTo("java.util.List<>");
		assertThat(parameterized(List.class, null, String.class).toString()).isEqualTo("java.util.List<java.lang.String>");

		assertThat(parameterized(List.class, null, String.class)).satisfies(DefaultEquality.defaultEquality()
			.andNotEqualTo(parameterized(List.class, List.class, String.class))
			.andNotEqualTo(parameterized(Set.class, null, String.class))
			.andNotEqualTo(parameterized(List.class, null, Object.class))
			.conventions());
	}

	@Test
	public void testWildcard() throws Exception {
		assertThat(wildcard().getTypeName()).isEqualTo("?");
		assertThat(wildcard().getLowerBounds()).hasSize(0);
		assertThat(wildcard().getUpperBounds()).hasSize(0);
		assertThat(wildcardExtends(String.class).getTypeName()).isEqualTo("? extends java.lang.String");
		assertThat(wildcardExtends(String.class).toString()).isEqualTo("? extends java.lang.String");
		assertThat(wildcardExtends(String.class).getUpperBounds()).containsExactly(String.class);
		assertThat(wildcardSuper(String.class).getTypeName()).isEqualTo("? super java.lang.String");
		assertThat(wildcardSuper(String.class).toString()).isEqualTo("? super java.lang.String");
		assertThat(wildcardSuper(String.class).getLowerBounds()).containsExactly(String.class);
		assertThat(wildcard()).satisfies(DefaultEquality.defaultEquality()
			.andEqualTo(wildcard())
			.andNotEqualTo(wildcardExtends(String.class))
			.andNotEqualTo(wildcardSuper(String.class))
			.conventions());
	}

	@Test
	public void testInnerClasses() throws Exception {
		assertThat(Types.innerClasses(getClass())).contains(NestedPublic.class, NestedPrivate.class, NestedPackagePrivate.class);
	}

	@Test
	public void testSortByMostConcreteSubBeforeSuper() throws Exception {
		assertThat(Stream.of(Super.class, Sub.class).sorted(Types::byMostConcrete).collect(toList())).containsExactly(Sub.class, Super.class);
		assertThat(Stream.of(Sub.class, Super.class).sorted(Types::byMostConcrete).collect(toList())).containsExactly(Sub.class, Super.class);
	}

	@Test
	public void testSortByMostConcreteUnrelatedTypes() throws Exception {
		assertThat(Stream.of(Simple.class, Complex.class).sorted(Types::byMostConcrete).collect(toList())).containsExactlyInAnyOrder(Simple.class,
			Complex.class);
	}

	@Test
	public void testSortByMostConcreteClassesBeforeGenericTypes() throws Exception {
		WildcardType wildcard = Types.wildcard();

		assertThat(Stream.of(Simple.class, wildcard).sorted(Types::byMostConcrete).collect(toList())).containsExactly(Simple.class, wildcard);
		assertThat(Stream.of(wildcard, Simple.class).sorted(Types::byMostConcrete).collect(toList())).containsExactly(Simple.class, wildcard);
	}

	@Test
	public void testSortByMostConcreteOrderableBaseTypes() throws Exception {
		ParameterizedType generic = Types.parameterized(Generic.class, null, Sub.class);
		ParameterizedType subGeneric = Types.parameterized(SubGeneric.class, null, Super.class);

		assertThat(Stream.of(generic, subGeneric).sorted(Types::byMostConcrete).collect(toList())).containsExactly(subGeneric, generic);
		assertThat(Stream.of(subGeneric, generic).sorted(Types::byMostConcrete).collect(toList())).containsExactly(subGeneric, generic);
	}

	@Test
	public void testSortByMostConcreteUnrelatedBaseTypes() throws Exception {
		ParameterizedType generic = Types.parameterized(Generic.class, null, Sub.class);
		ParameterizedType otherGeneric = Types.parameterized(GenericCycle.class, null, Super.class);

		assertThat(Stream.of(generic, otherGeneric).sorted(Types::byMostConcrete).collect(toList())).containsExactlyInAnyOrder(generic, otherGeneric);
	}

	@Test
	public void testResolveOnNonGenericType() throws Exception {
		assertThat(Types.resolve(Simple.class, Generic.class)).isEqualTo(Simple.class);
	}

	@Test
	public void testResolveOnUnboundWildcard() throws Exception {
		Type unboundWildcard = Types.getDeclaredField(Generic.class, "starx").getGenericType();

		assertThat(Types.resolve(unboundWildcard, Generic.class)).isEqualTo(unboundWildcard);
	}

	@Test
	public void testResolveOnFreeGenericArray() throws Exception {
		Type genericArrayType = Types.getDeclaredField(Generic.class, "vs").getGenericType();

		assertThat(Types.resolve(genericArrayType, Generic.class)).isEqualTo(genericArrayType);
	}

	@Test
	public void testResolveOnBoundGenericArray() throws Exception {
		Type genericArrayType = Types.getDeclaredField(Generic.class, "vs").getGenericType();

		assertThat(Types.resolve(genericArrayType, BoundGeneric.class)).isEqualTo(Sub[].class);
	}

	@Test
	public void testResolveOnPartlyBound() throws Exception {
		Type freeType = Types.getDeclaredField(BiGeneric.class, "k").getGenericType();
		Type boundType = Types.getDeclaredField(BiGeneric.class, "v").getGenericType();
		Type partlyBoundType = Types.getDeclaredField(BiGeneric.class, "vx").getGenericType();
		Type unboundWildcardType = Types.getDeclaredField(BiGeneric.class, "kstarv").getGenericType();
		Type unboundSuperWildcardType = Types.getDeclaredField(BiGeneric.class, "ksupv").getGenericType();
		Type boundWildcardType = Types.getDeclaredField(BiGeneric.class, "kvstar").getGenericType();
		Type boundSuperWildcardType = Types.getDeclaredField(BiGeneric.class, "kvsup").getGenericType();
		Type unboundAndWildcardType = Types.getDeclaredField(BiGeneric.class, "kstar").getGenericType();
		Type boundAndWildcardType = Types.getDeclaredField(BiGeneric.class, "starv").getGenericType();

		assertThat(Types.resolve(freeType, PartlyBoundBiGeneric.class)).isEqualTo(freeType);
		assertThat(Types.resolve(boundType, PartlyBoundBiGeneric.class)).isEqualTo(Sub.class);
		assertThat(Types.resolve(partlyBoundType, PartlyBoundBiGeneric.class)).isEqualTo(Types.parameterized(BiGeneric.class, null, freeType, Sub.class));
		assertThat(Types.resolve(unboundWildcardType, PartlyBoundBiGeneric.class))
			.isEqualTo(Types.parameterized(BiGeneric.class, null, Types.wildcardExtends(freeType), Sub.class));
		assertThat(Types.resolve(unboundSuperWildcardType, PartlyBoundBiGeneric.class))
			.isEqualTo(Types.parameterized(BiGeneric.class, null, ((ParameterizedType) unboundSuperWildcardType).getActualTypeArguments()[0], Sub.class));
		assertThat(Types.resolve(boundWildcardType, PartlyBoundBiGeneric.class))
			.isEqualTo(Types.parameterized(BiGeneric.class, null, freeType, Types.wildcardExtends(Sub.class)));
		assertThat(Types.resolve(boundSuperWildcardType, PartlyBoundBiGeneric.class))
			.isEqualTo(Types.parameterized(BiGeneric.class, null, freeType, Types.wildcardSuper(Sub.class)));
		assertThat(Types.resolve(boundAndWildcardType, PartlyBoundBiGeneric.class))
			.isEqualTo(Types.parameterized(BiGeneric.class, null, ((ParameterizedType) boundAndWildcardType).getActualTypeArguments()[0], Sub.class));
		assertThat(Types.resolve(unboundAndWildcardType, PartlyBoundBiGeneric.class))
			.isEqualTo(Types.parameterized(BiGeneric.class, null, freeType, ((ParameterizedType) unboundAndWildcardType).getActualTypeArguments()[1]));
	}

	@Test
	public void testResolveOnPartlyBoundGenericArray() throws Exception {
		Type freeType = Types.getDeclaredField(BiGeneric.class, "ks").getGenericType();
		Type boundType = Types.getDeclaredField(BiGeneric.class, "vs").getGenericType();

		assertThat(Types.resolve(freeType, PartlyBoundBiGeneric.class)).isEqualTo(freeType);
		assertThat(Types.resolve(boundType, PartlyBoundBiGeneric.class)).isEqualTo(Sub[].class);
	}

	@Test
	public void testClassFrom() throws Exception {
		ClassLoader classLoader = new ExtensibleClassLoader(ClassLoader.getSystemClassLoader(), Complex.class.getPackage().getName());
		Class<?> clazz = Types.classFrom(Complex.class, classLoader);

		assertThat(clazz.getName()).isEqualTo(Complex.class.getName());
		assertThat(clazz.getClassLoader()).isSameAs(classLoader);
	}

	@Test
	public void testParameterTypesFrom() throws Exception {
		ClassLoader classLoader = new ExtensibleClassLoader(ClassLoader.getSystemClassLoader(), Methods.class.getPackage().getName());
		Method method = Methods.class.getDeclaredMethod("params", NestedPublic.class, NestedPackagePrivate.class);
		Class<?>[] parameterTypes = Types.parameterTypesFrom(method, classLoader);

		assertThat(parameterTypes).allSatisfy(parameterType -> assertThat(parameterType.getClassLoader()).isSameAs(classLoader));
	}

	@Test
	public void testReturnTypeFrom() throws Exception {
		ClassLoader classLoader = new ExtensibleClassLoader(ClassLoader.getSystemClassLoader(), Methods.class.getPackage().getName());
		Method method = Methods.class.getDeclaredMethod("result");
		Class<?> returnType = Types.returnTypeFrom(method, classLoader);

		assertThat(returnType.getClassLoader()).isSameAs(classLoader);
	}

	public class NestedTypeField {
	}

	public static class NestedPublic {
	}

	private static class NestedPrivate {
	}

	static class NestedPackagePrivate {
	}

	protected static class NestedProtected {
	}

	@SuppressWarnings("unused")
	public static class NestedConstructors {
		public NestedConstructors(int i) {
			
		}
		protected NestedConstructors(char c) {
			
		}
		NestedConstructors(boolean b) {
			
		}
		private NestedConstructors() {
			
		}
	}

	public static class Methods {
		public NestedPublic result() {
			return new NestedPublic();
		}

		public void params(NestedPublic nestedPublic, NestedPackagePrivate nestedPackage) {
		}
	}

}

class TypesPackagePrivate {
	// NestedPrivate privateAccessIsNotAllowedFromPackage;
	NestedPublic publicAccessIsAllowedFromPackage;
	NestedPackagePrivate packagePrivateAccessIsAllowedFromPackage;
	NestedProtected protectedAccessIsAllowedFromPackage;

}