package net.amygdalum.testrecorder.deserializers;

import static net.amygdalum.testrecorder.util.Types.array;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TypeManagerTest {

    private TypeManager types;

    @Before
    public void before() throws Exception {
        types = new TypeManager("net.amygdalum.testrecorder.deserializers");
    }

    @Test
    public void testGetPackage() throws Exception {
        assertThat(types.getPackage(), equalTo("net.amygdalum.testrecorder.deserializers"));
        assertThat(new TypeManager().getPackage(), equalTo(""));
    }

    @Test
    public void testRegisterTypes() throws Exception {
        types.registerTypes(Integer.class, List.class);

        assertThat(types.getImports(), containsInAnyOrder("java.util.List", "java.lang.Integer"));
    }

    @Test
    public void testStaticImport() throws Exception {
        types.staticImport(Collections.class, "sort");

        assertThat(types.getImports(), contains("static java.util.Collections.sort"));
    }

    @Test
    public void testRegisterTypeArray() throws Exception {
        types.registerType(array(parameterized(List.class, null, String.class)));

        assertThat(types.getImports(), containsInAnyOrder("java.lang.String", "java.util.List"));
    }

    @Test
    public void testRegisterTypeOther() throws Exception {
        types.registerType(mock(Type.class));

        assertThat(types.getImports(), empty());
    }

    @Test
    public void testRegisterImport() throws Exception {
        types.registerImport(String.class);

        assertThat(types.getImports(), contains("java.lang.String"));
    }

    @Test
    public void testRegisterImportPrimitive() throws Exception {
        types.registerImport(int.class);
        
        assertThat(types.getImports(), empty());
    }
    
    @Test
    public void testRegisterImportArray() throws Exception {
        types.registerImport(Integer[].class);
        
        assertThat(types.getImports(), contains("java.lang.Integer"));
    }
    
    @Test
    public void testRegisterImportCached() throws Exception {
        types.registerImport(String.class);
        types.registerImport(String.class);

        assertThat(types.getImports(), contains("java.lang.String"));
    }

    @Test
    public void testRegisterImportColliding() throws Exception {
        types.registerImport(StringTokenizer.class);
        types.registerImport(java.util.StringTokenizer.class);

        assertThat(types.getImports(), contains("net.amygdalum.testrecorder.deserializers.StringTokenizer"));
    }

    @Test
    public void testRegisterImportHidden() throws Exception {
        types.registerImport(Hidden.class);

        assertThat(types.getImports(), containsInAnyOrder("net.amygdalum.testrecorder.Wrapped", "static net.amygdalum.testrecorder.Wrapped.clazz"));
    }
    
    @Test
    public void testRegisterImportHiddenCached() throws Exception {
        types.registerImport(Hidden.class);
        types.registerImport(Hidden.class);

        assertThat(types.getImports(), containsInAnyOrder("net.amygdalum.testrecorder.Wrapped", "static net.amygdalum.testrecorder.Wrapped.clazz"));
    }
    
    @Test
    public void testIsHiddenType() throws Exception {
        assertThat(types.isHidden(Hidden.class), is(true));
    }
    
    @Test
    public void testIsHiddenConstructor() throws Exception {
        assertThat(types.isHidden(Hidden.class.getDeclaredConstructor()), is(true));
    }
 
    @Test
    public void testGetBestNameWithoutImport() throws Exception {
        assertThat(types.getBestName(List.class), equalTo("java.util.List<>"));
    }
    
    @Test
    public void testGetBestNameWithImport() throws Exception {
        types.registerType(String.class);
        
        assertThat(types.getBestName(String.class), equalTo("String"));
    }

    @Test
    public void testGetBestNameOfArray() throws Exception {
        types.registerType(String.class);
        
        assertThat(types.getBestName(String[].class), equalTo("String[]"));
    }

    @Test
    public void testGetBestNameOfGenericArray() throws Exception {
        types.registerType(List.class);
        types.registerType(String.class);
        
        assertThat(types.getBestName(array(parameterized(List.class, null, String.class))), equalTo("List<String>[]"));
        assertThat(types.getBestName(array(parameterized(List.class, null, Date.class))), equalTo("List<java.util.Date>[]"));
        assertThat(types.getBestName(array(List.class)), equalTo("List<>[]"));
    }
    
    @Test
    public void testGetBestNameGenericWithImport() throws Exception {
        types.registerType(List.class);
        
        assertThat(types.getBestName(List.class), equalTo("List<>"));
        assertThat(types.getBestName(parameterized(List.class, null, String.class)), equalTo("List<String>"));
        assertThat(types.getBestName(parameterized(List.class, null, Date.class)), equalTo("List<java.util.Date>"));
    }
    
    @Test
    public void testGetBestNameOther() throws Exception {
        assertThat(types.getBestName(mock(Type.class)), equalTo("Object"));
    }
    
    @Test
    public void testGetBestSignatureWithoutImport() throws Exception {
        assertThat(types.getBestSignature(List.class), equalTo("java.util.List<>"));
    }
    
    @Test
    public void testGetBestSignatureWithInnerType() throws Exception {
        assertThat(types.getBestSignature(Hidden.class), equalTo("net.amygdalum.testrecorder.deserializers.TypeManagerTest$Hidden"));
    }
    
    @Test
    public void testGetBestSignatureWithImport() throws Exception {
        types.registerType(String.class);
        
        assertThat(types.getBestSignature(String.class), equalTo("String"));
    }
    
    @Test
    public void testGetBestSignatureOfArray() throws Exception {
        types.registerType(String.class);
        
        assertThat(types.getBestSignature(String[].class), equalTo("String[]"));
    }
    
    @Test
    public void testGetBestSignatureOfGenericArray() throws Exception {
        types.registerType(List.class);
        types.registerType(String.class);
        
        assertThat(types.getBestSignature(array(parameterized(List.class, null, String.class))), equalTo("List<String>[]"));
        assertThat(types.getBestSignature(array(parameterized(List.class, null, Date.class))), equalTo("List<java.util.Date>[]"));
        assertThat(types.getBestSignature(array(List.class)), equalTo("List<>[]"));
    }
    
    @Test
    public void testGetBestSignatureGenericWithImport() throws Exception {
        types.registerType(List.class);
        
        assertThat(types.getBestSignature(List.class), equalTo("List<>"));
        assertThat(types.getBestSignature(parameterized(List.class, null, String.class)), equalTo("List<String>"));
        assertThat(types.getBestSignature(parameterized(List.class, null, Date.class)), equalTo("List<java.util.Date>"));
    }
    
    @Test
    public void testGetBestSignatureOther() throws Exception {
        assertThat(types.getBestSignature(mock(Type.class)), equalTo("Object"));
    }
    
    private static class Hidden {
        
    }

}
class StringTokenizer {
    
}
