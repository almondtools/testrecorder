package net.amygdalum.testrecorder.deserializers;

import static net.amygdalum.testrecorder.util.Types.array;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.wildcard;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.DeserializerTypeManager;

public class DeserializerTypeManagerTest {

    private DeserializerTypeManager types;

    @BeforeEach
    public void before() throws Exception {
        types = new DeserializerTypeManager("net.amygdalum.testrecorder.deserializers");
    }

    @Test
    public void testGetPackage() throws Exception {
        assertThat(types.getPackage()).isEqualTo("net.amygdalum.testrecorder.deserializers");
        assertThat(new DeserializerTypeManager().getPackage()).isEqualTo("");
    }

    @Test
    public void testRegisterTypes() throws Exception {
        types.registerTypes(Integer.class, List.class);

        assertThat(types.getImports()).containsExactlyInAnyOrder("java.util.List", "java.lang.Integer");
    }

    @Test
    public void testStaticImport() throws Exception {
        types.staticImport(Collections.class, "sort");

        assertThat(types.getImports()).containsExactly("static java.util.Collections.sort");
    }

    @Test
    public void testRegisterTypeArray() throws Exception {
        types.registerType(array(parameterized(List.class, null, String.class)));

        assertThat(types.getImports()).containsExactlyInAnyOrder("java.lang.String", "java.util.List");
    }

    @Test
    public void testRegisterTypeOther() throws Exception {
        types.registerType(mock(Type.class));

        assertThat(types.getImports()).isEmpty();
    }

    @Test
    public void testRegisterImport() throws Exception {
        types.registerImport(String.class);

        assertThat(types.getImports()).containsExactly("java.lang.String");
    }

    @Test
    public void testRegisterImportPrimitive() throws Exception {
        types.registerImport(int.class);

        assertThat(types.getImports()).isEmpty();
    }

    @Test
    public void testRegisterImportArray() throws Exception {
        types.registerImport(Integer[].class);

        assertThat(types.getImports()).containsExactly("java.lang.Integer");
    }

    @Test
    public void testRegisterImportCached() throws Exception {
        types.registerImport(String.class);
        types.registerImport(String.class);

        assertThat(types.getImports()).containsExactly("java.lang.String");
    }

    @Test
    public void testRegisterImportColliding() throws Exception {
        types.registerImport(StringTokenizer.class);
        types.registerImport(java.util.StringTokenizer.class);

        assertThat(types.getImports()).containsExactly("net.amygdalum.testrecorder.deserializers.StringTokenizer");
    }

    @Test
    public void testRegisterImportHidden() throws Exception {
        types.registerImport(Hidden.class);

        assertThat(types.getImports()).containsExactlyInAnyOrder("net.amygdalum.testrecorder.runtime.Wrapped", "static net.amygdalum.testrecorder.runtime.Wrapped.clazz");
    }

    @Test
    public void testRegisterImportHiddenCached() throws Exception {
        types.registerImport(Hidden.class);
        types.registerImport(Hidden.class);

        assertThat(types.getImports()).containsExactlyInAnyOrder("net.amygdalum.testrecorder.runtime.Wrapped", "static net.amygdalum.testrecorder.runtime.Wrapped.clazz");
    }

    @Test
    public void testIsHiddenType() throws Exception {
        assertThat(types.isHidden(Hidden.class)).isTrue();
    }

    @Test
    public void testIsHiddenConstructor() throws Exception {
        assertThat(types.isHidden(Hidden.class.getDeclaredConstructor())).isTrue();
    }

    @Test
    public void testGetVariableTypeNameWithoutImport() throws Exception {
        assertThat(types.getVariableTypeName(List.class)).isEqualTo("java.util.List");
    }

    @Test
    public void testGetVariableTypeNameWithImport() throws Exception {
        types.registerType(String.class);

        assertThat(types.getVariableTypeName(String.class)).isEqualTo("String");
    }

    @Test
    public void testGetVariableTypeNameOfArray() throws Exception {
        types.registerType(String.class);

        assertThat(types.getVariableTypeName(String[].class)).isEqualTo("String[]");
    }

    @Test
    public void testGetVariableTypeNameOfGenericArray() throws Exception {
        types.registerType(List.class);
        types.registerType(String.class);

        assertThat(types.getVariableTypeName(array(parameterized(List.class, null, String.class)))).isEqualTo("List<String>[]");
        assertThat(types.getVariableTypeName(array(parameterized(List.class, null, Date.class)))).isEqualTo("List<java.util.Date>[]");
        assertThat(types.getVariableTypeName(array(List.class))).isEqualTo("List[]");
    }

    @Test
    public void testGetVariableTypeNameGenericWithImport() throws Exception {
        types.registerType(List.class);
        types.registerType(Map.class);

        assertThat(types.getVariableTypeName(List.class)).isEqualTo("List");
        assertThat(types.getVariableTypeName(Map.class)).isEqualTo("Map");
        assertThat(types.getConstructorTypeName(parameterized(List.class, null, parameterized(List.class, null, wildcard())))).isEqualTo("List<List<?>>");
        assertThat(types.getVariableTypeName(parameterized(List.class, null, String.class))).isEqualTo("List<String>");
        assertThat(types.getVariableTypeName(parameterized(List.class, null, Date.class))).isEqualTo("List<java.util.Date>");
    }

    @Test
    public void testGetVariableTypeNameNestedType() throws Exception {
        assertThat(types.getVariableTypeName(net.amygdalum.testrecorder.util.testobjects.Hidden.VisibleInterface.class)).isEqualTo("net.amygdalum.testrecorder.util.testobjects.Hidden.VisibleInterface");
    }

    @Test
    public void testGetVariableTypeNameOther() throws Exception {
        assertThat(types.getVariableTypeName(mock(Type.class))).isEqualTo("Object");
    }

    @Test
    public void testGetConstructorTypeNameWithoutImport() throws Exception {
        assertThat(types.getConstructorTypeName(List.class)).isEqualTo("java.util.List<>");
    }
    
    @Test
    public void testGetConstructorTypeNameWithImport() throws Exception {
        types.registerType(String.class);
        
        assertThat(types.getConstructorTypeName(String.class)).isEqualTo("String");
    }
    
    @Test
    public void testGetConstructorTypeNameOfArray() throws Exception {
        types.registerType(String.class);
        
        assertThat(types.getConstructorTypeName(String[].class)).isEqualTo("String[]");
    }
    
    @Test
    public void testGetConstructorTypeNameOfGenericArray() throws Exception {
        types.registerType(List.class);
        types.registerType(String.class);
        
        assertThat(types.getConstructorTypeName(array(parameterized(List.class, null, String.class)))).isEqualTo("List<String>[]");
        assertThat(types.getConstructorTypeName(array(parameterized(List.class, null, Date.class)))).isEqualTo("List<java.util.Date>[]");
        assertThat(types.getConstructorTypeName(array(List.class))).isEqualTo("List[]");
    }
    
    @Test
    public void testGetConstructorTypeNameGenericWithImport() throws Exception {
        types.registerType(List.class);
        types.registerType(Map.class);

        assertThat(types.getConstructorTypeName(List.class)).isEqualTo("List<>");
        assertThat(types.getConstructorTypeName(Map.class)).isEqualTo("Map<>");
        assertThat(types.getConstructorTypeName(parameterized(List.class, null, parameterized(List.class, null, wildcard())))).isEqualTo("List<List<?>>");
        assertThat(types.getConstructorTypeName(parameterized(List.class, null, String.class))).isEqualTo("List<String>");
        assertThat(types.getConstructorTypeName(parameterized(List.class, null, Date.class))).isEqualTo("List<java.util.Date>");
    }
    
    @Test
    public void testGetConstructorTypeNameOther() throws Exception {
        assertThat(types.getConstructorTypeName(mock(Type.class))).isEqualTo("Object");
    }
   
    @Test
    public void testGetConstructorTypeNameNestedType() throws Exception {
        assertThat(types.getConstructorTypeName(net.amygdalum.testrecorder.util.testobjects.Hidden.VisibleInterface.class)).isEqualTo("net.amygdalum.testrecorder.util.testobjects.Hidden.VisibleInterface");
    }

    @Test
    public void testGetRawTypeNameNestedType() throws Exception {
        assertThat(types.getRawTypeName(net.amygdalum.testrecorder.util.testobjects.Hidden.VisibleInterface.class)).isEqualTo("net.amygdalum.testrecorder.util.testobjects.Hidden.VisibleInterface");
    }

    private static class Hidden {

    }

}

class StringTokenizer {

}
