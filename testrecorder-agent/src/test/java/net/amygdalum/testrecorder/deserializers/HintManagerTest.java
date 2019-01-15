package net.amygdalum.testrecorder.deserializers;

import static java.util.Collections.emptyList;
import static net.amygdalum.testrecorder.values.SerializedNull.nullInstance;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.FieldSignature;
import net.amygdalum.testrecorder.types.MethodSignature;
import net.amygdalum.testrecorder.types.RoleVisitor;
import net.amygdalum.testrecorder.types.SerializedArgument;
import net.amygdalum.testrecorder.types.SerializedField;
import net.amygdalum.testrecorder.types.SerializedResult;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.SerializedValueType;
import net.amygdalum.testrecorder.values.SerializedImmutable;
import net.amygdalum.testrecorder.values.SerializedObject;

public class HintManagerTest {

	private HintManager hintManager;

	@BeforeEach
	public void before() throws Exception {
		hintManager = new HintManager();
	}

	@Nested
	class testFetch {
		@Test
		void onArgument() throws Exception {
			assertThat(hintManager.fetch(Anno.class, new SerializedArgument(0, notExistingMethod(), nullInstance())))
				.isEmpty();
			assertThat(hintManager.fetch(Anno.class, new SerializedArgument(0, resultMethod(), nullInstance())))
				.isEmpty();
			assertThat(hintManager.fetch(Anno.class, new SerializedArgument(0, argumentMethod(), nullInstance())))
				.hasSize(1)
				.allMatch(hint -> hint instanceof Anno);
		}

		@Test
		void onArgumentWithCustomAnnotation() throws Exception {
			hintManager.addHint(MyObject.class.getDeclaredMethod("result", MyArgument.class), anno());
			hintManager.addHint(MyObject.class.getDeclaredMethod("result", MyArgument.class), new Annotation[] {anno()});

			assertThat(hintManager.fetch(Anno.class, new SerializedArgument(0, resultMethod(), nullInstance())))
				.hasSize(1)
				.allMatch(hint -> hint instanceof Anno);
			assertThat(hintManager.fetch(Anno.class, new SerializedArgument(0, argumentMethod(), nullInstance())))
				.hasSize(1)
				.allMatch(hint -> hint instanceof Anno);
		}

		@Test
		void onResult() throws Exception {
			assertThat(hintManager.fetch(Anno.class, new SerializedResult(notExistingMethod(), nullInstance())))
				.isEmpty();
			assertThat(hintManager.fetch(Anno.class, new SerializedResult(argumentMethod(), nullInstance())))
				.isEmpty();
			assertThat(hintManager.fetch(Anno.class, new SerializedResult(resultMethod(), nullInstance())))
				.hasSize(1)
				.allMatch(hint -> hint instanceof Anno);
			assertThat(hintManager.fetch(Anno.class, MyObject.class.getDeclaredMethod("argument", MyArgument.class)))
				.isEmpty();
			assertThat(hintManager.fetch(Anno.class, MyObject.class.getDeclaredMethod("result", MyArgument.class)))
				.hasSize(1)
				.allMatch(hint -> hint instanceof Anno);
		}

		@Test
		void onResultWithCustomAnnotation() throws Exception {
			hintManager.addHint(MyObject.class.getDeclaredMethod("argument", MyArgument.class), new Annotation[] {anno()});
			hintManager.addHint(MyObject.class.getDeclaredMethod("argument", MyArgument.class), anno());

			assertThat(hintManager.fetch(Anno.class, new SerializedResult(argumentMethod(), nullInstance())))
				.hasSize(1)
				.allMatch(hint -> hint instanceof Anno);
			assertThat(hintManager.fetch(Anno.class, new SerializedResult(resultMethod(), nullInstance())))
				.hasSize(1)
				.allMatch(hint -> hint instanceof Anno);
		}

		@Test
		void onField() throws Exception {
			assertThat(hintManager.fetch(Anno.class, new SerializedField(notExistingField(), nullInstance())))
				.isEmpty();
			assertThat(hintManager.fetch(Anno.class, new SerializedField(field(), nullInstance())))
				.isEmpty();
			assertThat(hintManager.fetch(Anno.class, new SerializedField(annotatedField(), nullInstance())))
				.hasSize(1)
				.allMatch(hint -> hint instanceof Anno);
			assertThat(hintManager.fetch(Anno.class, MyObject.class.getDeclaredField("field")))
				.isEmpty();
			assertThat(hintManager.fetch(Anno.class, MyObject.class.getDeclaredField("annotatedField")))
				.hasSize(1)
				.allMatch(hint -> hint instanceof Anno);
		}

		@Test
		void onFieldWithCustomAnnotation() throws Exception {
			hintManager.addHint(MyObject.class.getDeclaredField("field"), anno());

			assertThat(hintManager.fetch(Anno.class, new SerializedField(field(), nullInstance())))
				.hasSize(1)
				.allMatch(hint -> hint instanceof Anno);
			assertThat(hintManager.fetch(Anno.class, new SerializedField(annotatedField(), nullInstance())))
				.hasSize(1)
				.allMatch(hint -> hint instanceof Anno);
		}

		@Test
		void onReferenceType() throws Exception {
			assertThat(hintManager.fetch(Anno.class, new SerializedObject(Annotated.class)))
				.hasSize(1)
				.allMatch(hint -> hint instanceof Anno);
			assertThat(hintManager.fetch(Anno.class, new SerializedObject(NotAnnotated.class)))
				.isEmpty();
		}

		@Test
		void onReferenceTypeWithCustomAnnotation() throws Exception {
			hintManager.addHint(NotAnnotated.class, anno());

			assertThat(hintManager.fetch(Anno.class, new SerializedObject(Annotated.class)))
				.hasSize(1)
				.allMatch(hint -> hint instanceof Anno);
			assertThat(hintManager.fetch(Anno.class, new SerializedObject(NotAnnotated.class)))
				.hasSize(1)
				.allMatch(hint -> hint instanceof Anno);
		}

		@Test
		void onImmutableType() throws Exception {
			assertThat(hintManager.fetch(Anno.class, new SerializedImmutable<>(Annotated.class)))
				.hasSize(1)
				.allMatch(hint -> hint instanceof Anno);
			assertThat(hintManager.fetch(Anno.class, new SerializedImmutable<>(NotAnnotated.class)))
				.isEmpty();
		}

		@Test
		void onImmutableTypeWithCustomAnnotation() throws Exception {
			hintManager.addHint(NotAnnotated.class, anno());

			assertThat(hintManager.fetch(Anno.class, new SerializedImmutable<>(Annotated.class)))
				.hasSize(1)
				.allMatch(hint -> hint instanceof Anno);
			assertThat(hintManager.fetch(Anno.class, new SerializedImmutable<>(NotAnnotated.class)))
				.hasSize(1)
				.allMatch(hint -> hint instanceof Anno);

		}

		@Test
		void onValueType() throws Exception {
			assertThat(hintManager.fetch(Anno.class, new Value(Annotated.class)))
				.hasSize(1)
				.allMatch(hint -> hint instanceof Anno);
			assertThat(hintManager.fetch(Anno.class, new Value(NotAnnotated.class)))
				.isEmpty();
			assertThat(hintManager.fetch(Anno.class, new Value(null)))
				.isEmpty();
		}

		@Test
		void onValueTypeWithCustomAnnotation() throws Exception {
			hintManager.addHint(NotAnnotated.class, anno());

			assertThat(hintManager.fetch(Anno.class, new Value(Annotated.class)))
				.hasSize(1)
				.allMatch(hint -> hint instanceof Anno);
			assertThat(hintManager.fetch(Anno.class, new Value(NotAnnotated.class)))
				.hasSize(1)
				.allMatch(hint -> hint instanceof Anno);
		}

		@Test
		void byType() throws Exception {
			assertThat(hintManager.fetch(Anno.class, NotAnnotated.class))
				.isEmpty();
			assertThat(hintManager.fetch(Anno.class, Annotated.class))
				.hasSize(1)
				.allMatch(hint -> hint instanceof Anno);
			assertThat(hintManager.fetch(Anno.class, (AnnotatedElement) null))
				.isEmpty();
		}
	}

	private FieldSignature field() {
		return new FieldSignature(MyObject.class, Object.class, "field");
	}

	private FieldSignature annotatedField() {
		return new FieldSignature(MyObject.class, Object.class, "annotatedField");
	}

	private FieldSignature notExistingField() {
		return new FieldSignature(MyObject.class, Object.class, "notExistingField");
	}

	private MethodSignature notExistingMethod() {
		return new MethodSignature(MyObject.class, MyResult.class, "notexisting", new Type[] {MyArgument.class});
	}

	private MethodSignature resultMethod() {
		return new MethodSignature(MyObject.class, MyResult.class, "result", new Type[] {MyArgument.class});
	}

	private MethodSignature argumentMethod() {
		return new MethodSignature(MyObject.class, MyResult.class, "argument", new Type[] {MyArgument.class});
	}

	@Anno
	public static class Annotated {
	}

	public static class NotAnnotated {
	}

	public static class MyArgument {
	}

	public static class MyResult {
	}

	public static class MyObject {

		@Anno
		Object annotatedField;
		Object field;

		@Anno
		public MyResult result(MyArgument arg) {
			return null;
		}

		public MyResult argument(@Anno MyArgument arg) {
			return null;
		}
	}

	private Anno anno() {
		return new Anno() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return Anno.class;
			}

		};
	}

	@Retention(RetentionPolicy.RUNTIME)
	@interface Anno {

	}

	private class Value implements SerializedValueType {

		private Class<?> type;

		Value(Class<?> type) {
			this.type = type;
		}

		@Override
		public Type[] getUsedTypes() {
			return new Type[0];
		}

		@Override
		public Class<?> getType() {
			return type;
		}

		@Override
		public List<SerializedValue> referencedValues() {
			return emptyList();
		}

		@Override
		public <T> T accept(RoleVisitor<T> visitor) {
			return visitor.visitValueType(this);
		}

		@Override
		public Object getValue() {
			return null;
		}

	}

}
