package net.amygdalum.testrecorder.util;

import static com.almondtools.conmatch.conventions.UtilityClassMatcher.isUtilityClass;
import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.util.Types.array;
import static net.amygdalum.testrecorder.util.Types.assignableTypes;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.boxedType;
import static net.amygdalum.testrecorder.util.Types.boxingEquivalentTypes;
import static net.amygdalum.testrecorder.util.Types.component;
import static net.amygdalum.testrecorder.util.Types.equalTypes;
import static net.amygdalum.testrecorder.util.Types.inferType;
import static net.amygdalum.testrecorder.util.Types.innerType;
import static net.amygdalum.testrecorder.util.Types.isBoxedPrimitive;
import static net.amygdalum.testrecorder.util.Types.isHidden;
import static net.amygdalum.testrecorder.util.Types.isPrimitive;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.typeArgument;
import static net.amygdalum.testrecorder.util.Types.wildcard;
import static net.amygdalum.testrecorder.util.Types.wildcardExtends;
import static net.amygdalum.testrecorder.util.Types.wildcardSuper;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.junit.Test;

import net.amygdalum.testrecorder.util.TypesTest.NestedPackagePrivate;
import net.amygdalum.testrecorder.util.TypesTest.NestedProtected;
import net.amygdalum.testrecorder.util.TypesTest.NestedPublic;

public class TypesTest {

    @Test
    public void testTypes() throws Exception {
        assertThat(Types.class, isUtilityClass());
    }

    @Test
    public void testIsHiddenTrueForPrivate() throws Exception {
        assertThat(Types.isHidden(NestedPrivate.class, "any"), is(true));
    }

    @Test
    public void testIsHiddenFalseForNestedPackagePrivate() throws Exception {
        assertThat(Types.isHidden(NestedPackagePrivate.class, "net.amygdalum.testrecorder.util"), is(false));
    }

    @Test
    public void testIsHiddenFalseForNestedProtected() throws Exception {
        assertThat(Types.isHidden(NestedProtected.class, "net.amygdalum.testrecorder.util"), is(false));
    }

    @Test
    public void testIsHiddenFalseForPackagePrivateInSamePackage() throws Exception {
        assertThat(Types.isHidden(TypesPackagePrivate.class, "net.amygdalum.testrecorder.util"), is(false));
        assertThat(Types.isHidden(TypesPackagePrivate.class, "other"), is(true));
    }

    @Test
    public void testIsHiddenFalseForNestedPublic() throws Exception {
        assertThat(Types.isHidden(NestedPublic.class, "any"), is(false));
    }

    @Test
    public void testIsHiddenFalseForPublic() throws Exception {
        assertThat(Types.isHidden(TypesPublic.class, "net.amygdalum.testrecorder.util"), is(false));
        assertThat(Types.isHidden(TypesPublic.class, "other"), is(false));
    }

    @Test
    public void testBaseTypeOnSimpleTypes() throws Exception {
        assertThat(baseType(Object.class), equalTo(Object.class));
        assertThat(baseType(String.class), equalTo(String.class));
        assertThat(baseType(StringTokenizer.class), equalTo(StringTokenizer.class));
    }

    @Test
    public void testBaseTypeOnPrimitiveTypes() throws Exception {
        assertThat(baseType(int.class), equalTo(int.class));
        assertThat(baseType(void.class), equalTo(void.class));
    }

    @Test
    public void testBaseTypeOnParameterizedTypes() throws Exception {
        assertThat(baseType(parameterized(List.class, List.class, String.class)), equalTo(List.class));
        assertThat(baseType(parameterized(Map.class, Map.class, String.class, Object.class)), equalTo(Map.class));
    }

    @Test
    public void testBaseTypeOnGenericArrayTypes() throws Exception {
        assertThat(baseType(array(parameterized(List.class, List.class, String.class))), equalTo(List[].class));
        assertThat(baseType(array(parameterized(Map.class, Map.class, String.class, Object.class))), equalTo(Map[].class));
    }

    @Test
    public void testBaseTypeOnOtherTypes() throws Exception {
        assertThat(baseType(wildcard()), equalTo(Object.class));
        assertThat(baseType(wildcardExtends(String.class)), equalTo(Object.class));
        assertThat(baseType(wildcardSuper(String.class)), equalTo(Object.class));
    }

    @Test
    public void testBoxedType() throws Exception {
        assertThat(boxedType(byte.class), equalTo(Byte.class));
        assertThat(boxedType(short.class), equalTo(Short.class));
        assertThat(boxedType(int.class), equalTo(Integer.class));
        assertThat(boxedType(long.class), equalTo(Long.class));
        assertThat(boxedType(float.class), equalTo(Float.class));
        assertThat(boxedType(double.class), equalTo(Double.class));
        assertThat(boxedType(char.class), equalTo(Character.class));
        assertThat(boxedType(boolean.class), equalTo(Boolean.class));

        assertThat(boxedType(void.class), equalTo(void.class));

        assertThat(boxedType(Integer.class), equalTo(Integer.class));

        assertThat(boxedType(parameterized(List.class, List.class, String.class)), equalTo(List.class));
    }

    @Test
    public void testComponent() throws Exception {
        assertThat(component(int[].class), equalTo(int.class));
        assertThat(component(Integer[].class), equalTo(Integer.class));

        Type parameterized = parameterized(List.class, List.class, Integer.class);
        assertThat(component(array(parameterized)), equalTo(parameterized));

        assertThat(component(Object.class), equalTo(Object.class));
    }

    @Test
    public void testAssignableTypes() throws Exception {
        assertThat(assignableTypes(String.class, String.class), is(true));
        assertThat(assignableTypes(Object.class, String.class), is(true));
        assertThat(assignableTypes(String.class, Object.class), is(false));
        assertThat(assignableTypes(Integer.class, String.class), is(false));
    }

    @Test
    public void testEqualTypes() throws Exception {
        assertThat(equalTypes(parameterized(List.class, List.class, String.class), List.class), is(true));
        assertThat(equalTypes(parameterized(Set.class, Set.class, String.class), List.class), is(false));
        assertThat(equalTypes(parameterized(Set.class, Set.class, String.class), parameterized(Set.class, Set.class, Object.class)), is(true));
    }

    @Test
    public void testBoxingEquivalentTypes() throws Exception {
        assertThat(boxingEquivalentTypes(byte.class, Byte.class), is(true));
        assertThat(boxingEquivalentTypes(short.class, Short.class), is(true));
        assertThat(boxingEquivalentTypes(int.class, Integer.class), is(true));
        assertThat(boxingEquivalentTypes(long.class, Long.class), is(true));
    }

    @Test
    public void testInferType() throws Exception {
        assertThat(inferType(asList(ArrayList.class)), equalTo(List.class));
        assertThat(inferType(asList(ArrayList.class, Collection.class)), equalTo(Collection.class));
        assertThat(inferType(asList(List.class, Set.class)), equalTo(Collection.class));
        assertThat(inferType(asList(HashSet.class, ArrayList.class)), equalTo(Collection.class));
        assertThat(inferType(asList(parameterized(HashSet.class, String.class), parameterized(ArrayList.class, Object.class))), equalTo(Collection.class));
        assertThat(inferType(asList(String.class, List.class)), equalTo(Object.class));
        assertThat(inferType(asList(Sub1.class, Sub2.class)), equalTo(Super.class));
        assertThat(inferType(asList(Integer.class, Integer.class)), equalTo(Integer.class));
        assertThat(inferType(asList(String.class, String.class)), equalTo(String.class));
    }

    @Test
    public void testIsBoxedPrimitive() throws Exception {
        assertThat(isBoxedPrimitive(Byte.class), is(true));
        assertThat(isBoxedPrimitive(Short.class), is(true));
        assertThat(isBoxedPrimitive(Integer.class), is(true));
        assertThat(isBoxedPrimitive(Long.class), is(true));
        assertThat(isBoxedPrimitive(Float.class), is(true));
        assertThat(isBoxedPrimitive(Double.class), is(true));
        assertThat(isBoxedPrimitive(Boolean.class), is(true));
        assertThat(isBoxedPrimitive(Character.class), is(true));

        assertThat(isBoxedPrimitive(String.class), is(false));
        assertThat(isBoxedPrimitive(Object.class), is(false));
        assertThat(isBoxedPrimitive(List.class), is(false));
        assertThat(isBoxedPrimitive(parameterized(List.class, List.class, String.class)), is(false));
        assertThat(isBoxedPrimitive(Super.class), is(false));
    }

    @Test
    public void testIsPrimitive() throws Exception {
        assertThat(isPrimitive(byte.class), is(true));
        assertThat(isPrimitive(short.class), is(true));
        assertThat(isPrimitive(int.class), is(true));
        assertThat(isPrimitive(long.class), is(true));
        assertThat(isPrimitive(float.class), is(true));
        assertThat(isPrimitive(double.class), is(true));
        assertThat(isPrimitive(boolean.class), is(true));
        assertThat(isPrimitive(char.class), is(true));

        assertThat(isPrimitive(Byte.class), is(false));
        assertThat(isPrimitive(Short.class), is(false));
        assertThat(isPrimitive(Integer.class), is(false));
        assertThat(isPrimitive(Long.class), is(false));
        assertThat(isPrimitive(Float.class), is(false));
        assertThat(isPrimitive(Double.class), is(false));
        assertThat(isPrimitive(Boolean.class), is(false));
        assertThat(isPrimitive(Character.class), is(false));
        assertThat(isPrimitive(String.class), is(false));
        assertThat(isPrimitive(Object.class), is(false));
        assertThat(isPrimitive(List.class), is(false));
        assertThat(isPrimitive(Super.class), is(false));
    }

    @Test
    public void testTypeArgument() throws Exception {
        assertThat(typeArgument(parameterized(List.class, null, String.class), 0).get(), equalTo(String.class));
        assertThat(typeArgument(parameterized(Map.class, null, String.class, Object.class), 0).get(), equalTo(String.class));
        assertThat(typeArgument(parameterized(Map.class, null, String.class, Object.class), 1).get(), equalTo(Object.class));
        assertThat(typeArgument(parameterized(Map.class, null), 1).isPresent(), is(false));
        assertThat(typeArgument(parameterized(Map.class, null, (Type[]) null), 1).isPresent(), is(false));
        assertThat(typeArgument(Map.class, 0).isPresent(), is(false));
        assertThat(typeArgument(String.class, 0).isPresent(), is(false));
    }

    @Test
    public void testInnerType() throws Exception {
        assertThat(innerType(TypesTest.class, "NestedPublic"), equalTo(NestedPublic.class));
    }

    @Test(expected=TypeNotPresentException.class)
    public void testInnerTypeNotResolved() throws Exception {
        innerType(TypesTest.class, "NotExistent");
    }

    @Test
    public void testIsHiddenType() throws Exception {
        assertThat(isHidden(TypesTest.class, "any"), is(false));
        assertThat(isHidden(NestedPrivate.class, "any"), is(true));
        assertThat(isHidden(NestedPackagePrivate.class, "any"), is(true));
        assertThat(isHidden(NestedPackagePrivate.class, "net.amygdalum.testrecorder.util"), is(false));
        assertThat(isHidden(TypesPackagePrivate.class, "any"), is(true));
        assertThat(isHidden(TypesPackagePrivate.class, "net.amygdalum.testrecorder.util"), is(false));
    }

    public static class NestedPublic {
    }

    private static class NestedPrivate {
    }

    static class NestedPackagePrivate {
    }

    protected static class NestedProtected {
    }

    private static class Super {
    }

    private static class Sub1 extends Super {
    }

    private static class Sub2 extends Super {
    }

}

class TypesPackagePrivate {
    //NestedPrivate privateAccessIsNotAllowedFromPackage;
    NestedPublic publicAccessIsAllowedFromPackage;
    NestedPackagePrivate packagePrivateAccessIsAllowedFromPackage;
    NestedProtected protectedAccessIsAllowedFromPackage;

}