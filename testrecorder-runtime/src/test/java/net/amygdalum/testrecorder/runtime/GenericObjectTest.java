package net.amygdalum.testrecorder.runtime;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.Serializable;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.DoubleShadowingObject;
import net.amygdalum.testrecorder.util.testobjects.ShadowingObject;

@SuppressWarnings("unused")
public class GenericObjectTest {

	@Nested
	class testAs {
		@Test
		void onSimple() throws Exception {
			assertThat(new GenericObject() {
				public String str = "myStr";
			}.as(Simple.class).getStr()).isEqualTo("myStr");
		}

		@Test
		void onComplex() throws Exception {
			assertThat(new GenericObject() {
				public Simple simple = new GenericObject() {
					public String str = "nestedStr";
				}.as(Simple.class);
			}.as(Complex.class).getSimple().getStr()).isEqualTo("nestedStr");
		}

		@Test
		void onSimplePrivateConstructor() throws Exception {
			assertThat(new GenericObject() {
				public String str = "myStr";
			}.as(SimplePrivateConstructor.class).getStr()).isEqualTo("myStr");
		}

		@Test
		void onSimpleImplicitConstructor() throws Exception {
			assertThat(new GenericObject() {
				public String str = "myStr";
			}.as(SimpleImplicitConstructor.class).getStr()).isEqualTo("myStr");
		}

		@Test
		void onSimpleNoDefaultConstructor() throws Exception {
			assertThat(new GenericObject() {
				public String str = "myStr";
			}.as(SimpleNoDefaultConstructor.class).getStr()).isEqualTo("myStr");
		}

		@Test
		void onConstructorSupplied() throws Exception {
			assertThat(new GenericObject() {
				public String str = "myStr";
			}.as(Simple::new).getStr()).isEqualTo("myStr");
		}

		@Test
		void onConstructorWrapped() throws Exception {
			Wrapped obj = GenericObject.forward(Wrapped.clazz(SimplePrivateConstructor.class.getName()));

			assertThat(((SimplePrivateConstructor) new GenericObject() {
				public String str = "myStr";
			}.as(obj).value()).getStr()).isEqualTo("myStr");
		}
	}

	@Nested
	class testSetField {

		@Test
		void onCommon() throws Exception {
			Simple value = new Simple();
			Complex object = new Complex();

			GenericObject.setField(object, "simple", value);
			assertThat(object.simple).isSameAs(value);
		}

		@Test
		void onWrapped() throws Exception {
			Wrapped value = Wrapped.clazz(Simple.class.getName());
			Complex object = new Complex();

			GenericObject.setField(object, "simple", value);
			assertThat(object.simple).isSameAs(value.value());
		}

		@Test
		void onNonAssignableArray() throws Exception {
			Object[] value = new Object[] {"foo", "bar"};
			ContainingArray object = new ContainingArray();

			GenericObject.setField(object, "array", value);
			assertThat(object.array).containsExactly("foo", "bar");
		}

		@Test
		void onField() throws Exception {
			Simple value = new Simple();
			Complex object = new Complex();

			GenericObject.setField(object, Complex.class.getDeclaredField("simple"), value);
			assertThat(object.simple).isSameAs(value);
		}
	}

	@Test
	void testCopyField() throws Exception {
		Simple from = new Simple();
		Simple to = new Simple();
		from.str = "stringToCopy";

		GenericObject.copyField(Simple.class.getDeclaredField("str"), from, to);

		assertThat(from.str).isEqualTo("stringToCopy");
		assertThat(to.str).isEqualTo("stringToCopy");
	}

	@Test
	void testCopyArrayValues() throws Exception {
		String[] from = {"foo", "bar"};
		String[] to = new String[2];

		GenericObject.copyArrayValues(from, to);

		assertThat(from).containsExactly("foo", "bar");
		assertThat(to).containsExactly("foo", "bar");
	}

	@Nested
	class testForward {
		@Test
		void onCommon() throws Exception {
			Simple obj = GenericObject.forward(Simple.class);

			assertThat(obj.str).isNull();
		}

		@Test
		void onWrapped() throws Exception {
			Wrapped obj = GenericObject.forward(Wrapped.clazz(SimplePrivateConstructor.class.getName()));

			assertThat((SimplePrivateConstructor) obj.value())
				.isEqualToComparingFieldByFieldRecursively(new SimplePrivateConstructor());
		}
	}

	@Nested
	class testDefine {
		@Test
		void onCommon() throws Exception {
			Simple obj = GenericObject.forward(Simple.class);

			GenericObject.define(obj, new GenericObject() {
				String str = "definition";
			});

			assertThat(obj.str).isEqualTo("definition");
		}

		@Test
		void onWrapped() throws Exception {
			Wrapped obj = GenericObject.forward(Wrapped.clazz(SimplePrivateConstructor.class.getName()));

			GenericObject.define(obj, new GenericObject() {
				String str = "definition";
			});

			assertThat(((SimplePrivateConstructor) obj.value()).str).isEqualTo("definition");
		}
	}

	@Nested
	class testNewInstance {
		@Test
		void withNullParams() throws Exception {
			NullParamConstructor instance = GenericObject.newInstance(NullParamConstructor.class);

			assertThat(instance.getStr()).isNull();
		}

		@Test
		void withDefaultParams() throws Exception {
			DefaultParamConstructor instance = GenericObject.newInstance(DefaultParamConstructor.class);

			assertThat(instance.getStr()).isEmpty();
		}

		@Test
		void withNonDefaultParams() throws Exception {
			NonDefaultParamConstructor instance = GenericObject.newInstance(NonDefaultParamConstructor.class);

			assertThat(instance.getStr()).isEqualTo("String");
		}

		@Test
		void bruteForceReflection() throws Exception {
			ExceptionConstructor instance = GenericObject.newInstance(ExceptionConstructor.class);

			assertThat(instance.getStr()).isNull();
		}
	}

	@Nested
	class Scenarios {
		@Test
		void shadowingObjects() throws Exception {
			ShadowingObject shadowingObject = new GenericObject() {
				int ShadowedObject$field = 42;
				String ShadowingObject$field = "field";
			}.as(ShadowingObject.class);
			assertThat(shadowingObject.getField()).isEqualTo(42);
			assertThat(shadowingObject.getShadowingField()).isEqualTo("field");
		}

		@Test
		void doubleShadowingObjects() throws Exception {
			DoubleShadowingObject shadowingObject = new GenericObject() {
				int ShadowedObject$field = 42;
				String ShadowingObject$field = "field";
				String DoubleShadowingObject$field = "fieldshadowing";
			}.as(DoubleShadowingObject.class);
			assertThat(shadowingObject.getField()).isEqualTo(42);
			assertThat(shadowingObject.getShadowingField()).isEqualTo("field");
			assertThat(shadowingObject.getDoubleShadowingField()).isEqualTo("fieldshadowing");
		}
	}

	private interface AnInterface {
	}

	private enum AnEnum {
		ENUM;
	}

	private enum EmptyEnum {
	}

	private static class Simple {
		private String str;

		Simple() {
		}

		Simple(String str) {
			this.str = str;
		}

		public String getStr() {
			return str;
		}
	}

	private static class Complex {

		private Simple simple;

		Complex() {
			this.simple = new Simple("otherStr");
		}

		public Simple getSimple() {
			return simple;
		}
	}

	private static class ContainingArray {

		private String[] array;

		ContainingArray() {
			this.array = new String[0];
		}

		public String[] getArray() {
			return array;
		}
	}

	private static class SimplePrivateConstructor {
		private String str;

		private SimplePrivateConstructor() {
		}

		public String getStr() {
			return str;
		}
	}

	private static class SimpleImplicitConstructor {
		private String str;

		public String getStr() {
			return str;
		}
	}

	private static class SimpleNoDefaultConstructor {
		private String str;

		SimpleNoDefaultConstructor(String str) {
			this.str = str;
		}

		public String getStr() {
			return str;
		}
	}

	private static class NullParamConstructor {
		private String str;

		NullParamConstructor(String str) {
			this.str = str;
		}

		public String getStr() {
			return str;
		}
	}

	private static class DefaultParamConstructor {
		private String str;

		DefaultParamConstructor(String str) {
			if (str == null) {
				throw new NullPointerException();
			}
			this.str = str;
		}

		public String getStr() {
			return str;
		}
	}

	private static class NonDefaultParamConstructor {
		private String str;

		NonDefaultParamConstructor(String str) {
			if (str == null) {
				throw new NullPointerException();
			} else if (str.isEmpty()) {
				throw new IllegalArgumentException();
			}
			this.str = str;
		}

		public String getStr() {
			return str;
		}
	}

	private static class ExceptionConstructor {
		private String str;

		ExceptionConstructor(String str) {
			throw new IllegalArgumentException();
		}

		public String getStr() {
			return str;
		}
	}

	private static class NonSerializableConstructor implements Serializable {
		private String str;

		NonSerializableConstructor(String str) {
			throw new IllegalArgumentException();
		}

		public String getStr() {
			return str;
		}
	}

}
