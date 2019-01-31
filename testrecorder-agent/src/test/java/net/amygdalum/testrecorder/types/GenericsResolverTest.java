package net.amygdalum.testrecorder.types;

import static net.amygdalum.testrecorder.util.Types.genericArray;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.wildcardExtends;
import static net.amygdalum.testrecorder.util.Types.wildcardSuper;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.SerializableGenericArrayType;

public class GenericsResolverTest {

	@Nested
	class testResolveType {
		@Nested
		class onClasses {

			@Test
			public void ground() throws Exception {
				Method method = ClassExamples.class.getDeclaredMethod("classes", SimpleObject.class);
				GenericsResolver resolver = new GenericsResolver(method, new Type[] {SimpleObject.class});
				Type type = method.getGenericParameterTypes()[0];
				Type resolved = resolver.resolve(type);

				assertThat(resolved).isSameAs(SimpleObject.class);
			}

		}

		@Nested
		class onGenericArrays {

			@Test
			public void ground() throws Exception {
				Method method = GenericArrayExamples.class.getDeclaredMethod("ground", GenericObject[].class);
				GenericsResolver resolver = new GenericsResolver(method, new Type[] {GenericObject.class});
				Type type = method.getGenericParameterTypes()[0];
				Type resolved = resolver.resolve(type);

				assertThat(resolved).isSameAs(type);
			}

			@Test
			public void free() throws Exception {
				Method method = GenericArrayExamples.class.getDeclaredMethod("free", GenericObject[].class);
				GenericsResolver resolver = new GenericsResolver(method, new Type[] {GenericObject.class});
				Type type = method.getGenericParameterTypes()[0];
				Type resolved = resolver.resolve(type);

				assertThat(resolved).isSameAs(type);
			}

			@Test
			public void bound() throws Exception {
				Method method = GenericArrayExamples.class.getDeclaredMethod("bound", GenericObject[].class, Object.class);
				GenericsResolver resolver = new GenericsResolver(method, new Class[] {GenericObject[].class, String.class});
				Type type = method.getGenericParameterTypes()[0];
				Type resolved = resolver.resolve(type);

				SerializableGenericArrayType genericArray = genericArray(parameterized(GenericObject.class, GenericsResolverTest.class, String.class));
				assertThat(resolved).isEqualTo(genericArray);
			}

		}

		@Nested
		class onTypeVariables {

			@Test
			public void unbound() throws Exception {
				Method method = TypeVariableExamples.class.getDeclaredMethod("unbound", Object.class);
				GenericsResolver resolver = new GenericsResolver(method, new Type[] {SimpleObject.class});
				Type type = method.getGenericParameterTypes()[0];
				Type resolved = resolver.resolve(type);

				assertThat(resolved).isEqualTo(SimpleObject.class);
			}

			@Test
			public void free() throws Exception {
				Method method = TypeVariableExamples.class.getDeclaredMethod("free", Object.class);
				GenericsResolver resolver = new GenericsResolver(method, new Type[] {SimpleObject.class});
				Type type = method.getGenericParameterTypes()[0];
				Type resolved = resolver.resolve(type);

				assertThat(resolved).isEqualTo(SimpleObject.class);
			}

			@Test
			public void bound() throws Exception {
				Method method = TypeVariableExamples.class.getDeclaredMethod("bound", Object.class, List.class);
				GenericsResolver resolver = new GenericsResolver(method, new Type[] {SimpleObject.class, parameterized(List.class, null, SimpleObject.class)});
				Type type = method.getGenericParameterTypes()[0];
				Type resolved = resolver.resolve(type);

				assertThat(resolved).isEqualTo(SimpleObject.class);
			}
		}

		@Nested
		class onWildcards {

			@Test
			public void variable() throws Exception {
				Method method = WildcardExamples.class.getDeclaredMethod("variable", List.class);
				GenericsResolver resolver = new GenericsResolver(method, new Type[] {SimpleObjectList.class});
				Type type = method.getGenericParameterTypes()[0];
				Type resolved = resolver.resolve(type);

				assertThat(resolved).isSameAs(type);
			}

			@Test
			public void upperbound() throws Exception {
				Method method = WildcardExamples.class.getDeclaredMethod("upperbound", List.class);
				GenericsResolver resolver = new GenericsResolver(method, new Type[] {parameterized(List.class, null, InheritedObject.class)});
				Type type = method.getGenericParameterTypes()[0];
				Type resolved = resolver.resolve(type);
				
				assertThat(resolved).isSameAs(type);
			}
			
			@Test
			public void lowerbound() throws Exception {
				Method method = WildcardExamples.class.getDeclaredMethod("lowerbound", List.class);
				GenericsResolver resolver = new GenericsResolver(method, new Type[] {parameterized(List.class, null, SimpleObject.class)});
				Type type = method.getGenericParameterTypes()[0];
				Type resolved = resolver.resolve(type);
				
				assertThat(resolved).isSameAs(type);
			}
			
			@Test
			public void limitbound() throws Exception {
				Method method = WildcardExamples.class.getDeclaredMethod("limitbound", List.class);
				GenericsResolver resolver = new GenericsResolver(method, new Type[] {parameterized(List.class, null, SimpleObject.class)});
				Type type = method.getGenericParameterTypes()[0];
				Type resolved = resolver.resolve(type);
				
				assertThat(resolved).isSameAs(type);
			}
			
			@Test
			public void boundupper() throws Exception {
				Method method = WildcardExamples.class.getDeclaredMethod("boundupper", Object.class, List.class);
				GenericsResolver resolver = new GenericsResolver(method, new Type[] {SimpleObject.class, ArrayList.class});
				Type type = method.getGenericParameterTypes()[1];
				Type resolved = resolver.resolve(type);
				
				assertThat(resolved).isEqualTo(parameterized(List.class, null, wildcardExtends(SimpleObject.class)));
			}
			
			@Test
			public void boundlower() throws Exception {
				Method method = WildcardExamples.class.getDeclaredMethod("boundlower", Object.class, List.class);
				GenericsResolver resolver = new GenericsResolver(method, new Type[] {SimpleObject.class, ArrayList.class});
				Type type = method.getGenericParameterTypes()[1];
				Type resolved = resolver.resolve(type);
				
				assertThat(resolved).isEqualTo(parameterized(List.class, null, wildcardSuper(SimpleObject.class)));
			}
			
		}

		@Nested
		class onOther {

			@Test
			public void impossible() throws Exception {
				Method method = OtherExamples.class.getDeclaredMethod("other", Object.class);
				GenericsResolver resolver = new GenericsResolver(method, new Type[] {SimpleObject.class});
				Type type = new Type() {
					
				};
				Type resolved = resolver.resolve(type);
				
				assertThat(resolved).isSameAs(type);
			}
			
		}
		@Nested
		class onTypeArray {
			
			@Test
			public void returningSameIfSingleResolved() throws Exception {
				Method method = MethodExamples.class.getDeclaredMethod("method", SimpleObject.class);
				GenericsResolver resolver = new GenericsResolver(method, new Type[] {SimpleObject.class});
				Type[] unresolved = method.getGenericParameterTypes();
				
				Type[] resolved = resolver.resolve(unresolved);

				assertThat(resolved).isSameAs(unresolved);
			}
			
			@Test
			public void returningSameIfMultipleResolved() throws Exception {
				Method method = MethodExamples.class.getDeclaredMethod("method", SimpleObject.class, SimpleObject.class);
				GenericsResolver resolver = new GenericsResolver(method, new Type[] {SimpleObject.class, SimpleObject.class});
				Type[] unresolved = method.getGenericParameterTypes();
				
				Type[] resolved = resolver.resolve(unresolved);
				
				assertThat(resolved).isSameAs(unresolved);
			}
			
			@Test
			public void resolvingGenerics() throws Exception {
				Method method = MethodExamples.class.getDeclaredMethod("method", Object.class, List.class);
				GenericsResolver resolver = new GenericsResolver(method, new Type[] {SimpleObject.class, List.class});
				Type[] unresolved = method.getGenericParameterTypes();
				
				Type[] resolved = resolver.resolve(unresolved);

				assertThat(resolved).isNotSameAs(unresolved);
				assertThat(resolved).contains(SimpleObject.class, SimpleObject.class);
			}
			
		}
	}

	private static class GenericObject<T> {
	}

	private static class SimpleObject {
	}

	private static class InheritedObject extends SimpleObject {
	}
	
	private abstract static class SimpleObjectList implements List<SimpleObject> {
	}

	@SuppressWarnings("unused")
	private static class MethodExamples {
		void method(SimpleObject o) {
		}
		void method(SimpleObject o1, SimpleObject o2) {
		}
		<S> void method(S o1, List<S> o2) {
		}
	}

	@SuppressWarnings("unused")
	private static class ClassExamples {
		void classes(SimpleObject o) {
		}
	}

	@SuppressWarnings("unused")
	private static class GenericArrayExamples<T> {

		void ground(GenericObject<String>[] o) {
		}

		<S> void free(GenericObject<S>[] o) {
		}

		<S> void bound(GenericObject<S>[] o, S s) {
		}

	}

	@SuppressWarnings("unused")
	private static class TypeVariableExamples<T> {
		void unbound(T o) {
		}

		<S> void bound(S o, List<S> list) {
		}

		<S> void free(S o) {
		}

	}

	@SuppressWarnings("unused")
	private static class WildcardExamples<T> {
		void variable(List<?> o) {
		}

		void upperbound(List<? extends SimpleObject> o) {
		}

		void lowerbound(List<? super InheritedObject> o) {
		}

		void limitbound(List<? extends T> o) {
		}

		<S> void boundupper(S s, List<? extends S> o) {
		}
		<S> void boundlower(S s, List<? super S> o) {
		}
	}

	@SuppressWarnings("unused")
	private static class OtherExamples {
		void other(Object o) {
		}
	}

}
