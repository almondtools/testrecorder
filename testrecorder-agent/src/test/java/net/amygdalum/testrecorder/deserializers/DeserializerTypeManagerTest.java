package net.amygdalum.testrecorder.deserializers;

import static net.amygdalum.testrecorder.util.Types.array;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.wildcard;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.GenericMethods;

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

	@Nested
	class testRegisterType {
		@Test
		public void onArray() throws Exception {
			types.registerType(array(parameterized(List.class, null, String.class)));

			assertThat(types.getImports()).containsExactlyInAnyOrder("java.lang.String", "java.util.List");
		}

		@Test
		public void onOther() throws Exception {
			types.registerType(mock(Type.class));

			assertThat(types.getImports()).isEmpty();
		}
	}

	@Nested
	class testRegisterImport {

		@Test
		public void onCommon() throws Exception {
			types.registerImport(String.class);

			assertThat(types.getImports()).containsExactly("java.lang.String");
		}

		@Test
		public void onPrimitive() throws Exception {
			types.registerImport(int.class);

			assertThat(types.getImports()).isEmpty();
		}

		@Test
		public void onArray() throws Exception {
			types.registerImport(Integer[].class);

			assertThat(types.getImports()).containsExactly("java.lang.Integer");
		}

		@Test
		public void onCached() throws Exception {
			types.registerImport(String.class);
			types.registerImport(String.class);

			assertThat(types.getImports()).containsExactly("java.lang.String");
		}

		@Test
		public void onColliding() throws Exception {
			types.registerImport(StringTokenizer.class);
			types.registerImport(java.util.StringTokenizer.class);

			assertThat(types.getImports()).containsExactly("net.amygdalum.testrecorder.deserializers.StringTokenizer");
		}

		@Test
		public void onHidden() throws Exception {
			types.registerImport(Hidden.class);

			assertThat(types.getImports()).containsExactlyInAnyOrder("net.amygdalum.testrecorder.runtime.Wrapped", "static net.amygdalum.testrecorder.runtime.Wrapped.clazz");
		}

		@Test
		public void onHiddenCached() throws Exception {
			types.registerImport(Hidden.class);
			types.registerImport(Hidden.class);

			assertThat(types.getImports()).containsExactlyInAnyOrder("net.amygdalum.testrecorder.runtime.Wrapped", "static net.amygdalum.testrecorder.runtime.Wrapped.clazz");
		}
	}

	@Nested
	class testIsHidden {
		@Test
		public void onType() throws Exception {
			assertThat(types.isHidden(Hidden.class)).isTrue();
		}

		@Test
		public void onConstructor() throws Exception {
			assertThat(types.isHidden(Hidden.class.getDeclaredConstructor())).isTrue();
		}
	}

	@Nested
	class testGetVariableTypeName {
		@Test
		public void withoutImport() throws Exception {
			assertThat(types.getVariableTypeName(List.class)).isEqualTo("java.util.List");
		}

		@Test
		public void withImport() throws Exception {
			types.registerType(String.class);

			assertThat(types.getVariableTypeName(String.class)).isEqualTo("String");
		}

		@Test
		public void ofArray() throws Exception {
			types.registerType(String.class);

			assertThat(types.getVariableTypeName(String[].class)).isEqualTo("String[]");
		}

		@Test
		public void ofGenericArray() throws Exception {
			types.registerType(List.class);
			types.registerType(String.class);

			assertThat(types.getVariableTypeName(array(parameterized(List.class, null, String.class)))).isEqualTo("List<String>[]");
			assertThat(types.getVariableTypeName(array(parameterized(List.class, null, Date.class)))).isEqualTo("List<java.util.Date>[]");
			assertThat(types.getVariableTypeName(array(List.class))).isEqualTo("List[]");
		}

		@Test
		public void ofGenericWithImport() throws Exception {
			types.registerType(List.class);
			types.registerType(Map.class);

			assertThat(types.getVariableTypeName(List.class)).isEqualTo("List");
			assertThat(types.getVariableTypeName(Map.class)).isEqualTo("Map");
			assertThat(types.getVariableTypeName(parameterized(List.class, null, wildcard()))).isEqualTo("List<?>");
			assertThat(types.getVariableTypeName(parameterized(List.class, null, parameterized(List.class, null, wildcard())))).isEqualTo("List<List<?>>");
			assertThat(types.getVariableTypeName(parameterized(List.class, null, String.class))).isEqualTo("List<String>");
			assertThat(types.getVariableTypeName(parameterized(List.class, null, Date.class))).isEqualTo("List<java.util.Date>");
		}

		@Test
		public void ofGenericWithFreeVariable() throws Exception {
			types.registerType(GenericMethods.class);
			types.registerType(Collection.class);

			Type free = GenericMethods.class.getDeclaredMethod("free", Object.class).getGenericParameterTypes()[0];
			Type freeNested = GenericMethods.class.getDeclaredMethod("freeNested", GenericMethods.class).getGenericParameterTypes()[0];
			Type freeLimited = GenericMethods.class.getDeclaredMethod("freeLimited", Collection.class).getGenericParameterTypes()[0];

			assertThat(types.getVariableTypeName(free)).isEqualTo("Object");
			assertThat(types.getVariableTypeName(freeNested)).isEqualTo("GenericMethods<?>");
			assertThat(types.getVariableTypeName(freeLimited)).isEqualTo("Collection<?>");
		}

		@Test
		public void ofGenericWithBoundVariable() throws Exception {
			types.registerType(GenericMethods.class);
			types.registerType(Collection.class);

			Type bound = GenericMethods.class.getDeclaredMethod("bound", Object.class, Object.class).getGenericParameterTypes()[0];
			Type boundNested = GenericMethods.class.getDeclaredMethod("boundNested", GenericMethods.class, Object.class).getGenericParameterTypes()[0];
			Type boundLimited = GenericMethods.class.getDeclaredMethod("boundLimited", Collection.class, Collection.class).getGenericParameterTypes()[0];

			assertThat(types.getVariableTypeName(bound)).isEqualTo("Object");
			assertThat(types.getVariableTypeName(boundNested)).isEqualTo("GenericMethods<?>"); // not entirely correct yet a bound type variable should be resolved before this method is called
			assertThat(types.getVariableTypeName(boundLimited)).isEqualTo("Collection<?>");
		}

		@Test
		public void ofNestedType() throws Exception {
			assertThat(types.getVariableTypeName(net.amygdalum.testrecorder.util.testobjects.Hidden.VisibleInterface.class)).isEqualTo("net.amygdalum.testrecorder.util.testobjects.Hidden.VisibleInterface");
		}

		@Test
		public void ofOther() throws Exception {
			assertThat(types.getVariableTypeName(mock(Type.class))).isEqualTo("Object");
		}
	}

	@Nested
	class testGetConstructorTypeName {
		@Test
		public void withoutImport() throws Exception {
			assertThat(types.getConstructorTypeName(List.class)).isEqualTo("java.util.List<>");
		}

		@Test
		public void withImport() throws Exception {
			types.registerType(String.class);

			assertThat(types.getConstructorTypeName(String.class)).isEqualTo("String");
		}

		@Test
		public void ofArray() throws Exception {
			types.registerType(String.class);

			assertThat(types.getConstructorTypeName(String[].class)).isEqualTo("String[]");
		}

		@Test
		public void ofGenericArray() throws Exception {
			types.registerType(List.class);
			types.registerType(String.class);

			assertThat(types.getConstructorTypeName(array(parameterized(List.class, null, String.class)))).isEqualTo("List<String>[]");
			assertThat(types.getConstructorTypeName(array(parameterized(List.class, null, Date.class)))).isEqualTo("List<java.util.Date>[]");
			assertThat(types.getConstructorTypeName(array(List.class))).isEqualTo("List[]");
		}

		@Test
		public void ofGenericWithImport() throws Exception {
			types.registerType(List.class);
			types.registerType(Map.class);

			assertThat(types.getConstructorTypeName(List.class)).isEqualTo("List<>");
			assertThat(types.getConstructorTypeName(Map.class)).isEqualTo("Map<>");
			assertThat(types.getConstructorTypeName(parameterized(List.class, null, parameterized(List.class, null, wildcard())))).isEqualTo("List<List<?>>");
			assertThat(types.getConstructorTypeName(parameterized(List.class, null, String.class))).isEqualTo("List<String>");
			assertThat(types.getConstructorTypeName(parameterized(List.class, null, Date.class))).isEqualTo("List<java.util.Date>");
		}

		@Test
		public void ofOther() throws Exception {
			assertThat(types.getConstructorTypeName(mock(Type.class))).isEqualTo("Object");
		}

		@Test
		public void ofNestedType() throws Exception {
			assertThat(types.getConstructorTypeName(net.amygdalum.testrecorder.util.testobjects.Hidden.VisibleInterface.class)).isEqualTo("net.amygdalum.testrecorder.util.testobjects.Hidden.VisibleInterface");
		}
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
