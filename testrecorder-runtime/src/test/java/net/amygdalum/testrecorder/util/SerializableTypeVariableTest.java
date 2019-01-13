package net.amygdalum.testrecorder.util;

import static net.amygdalum.extensions.assertj.conventions.DefaultEquality.defaultEquality;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Type;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SerializableTypeVariableTest {

	private SerializableTypeVariable<Class<?>> type = new SerializableTypeVariable<>("C", SerializableTypeVariableTest.class);
	private SerializableTypeVariable<Class<?>> boundedType = new SerializableTypeVariable<>("BC", SerializableTypeVariableTest.class);
	private SerializableTypeVariable<Class<?>> recursiveType = new SerializableTypeVariable<>("RC", SerializableTypeVariableTest.class);
	private SerializableParameterizedType recursiveBound = new SerializableParameterizedType(Enum.class, null, recursiveType);

	@BeforeEach
	void before() throws Exception {
		boundedType.boundedBy(Collection.class);
		recursiveType.boundedBy(recursiveBound);
	}
	
	@Test
	void testGetName() throws Exception {
		assertThat(type.getName()).isEqualTo("C");
		assertThat(boundedType.getName()).isEqualTo("BC");
		assertThat(recursiveType.getName()).isEqualTo("RC");
	}

	@Test
	void testGetBounds() throws Exception {
		assertThat(type.getBounds()).isEmpty();
		assertThat(boundedType.getBounds()).isEqualTo(new Type[] {Collection.class});
		assertThat(recursiveType.getBounds()).isEqualTo(new Type[] {recursiveBound});
	}

	@Test
	void testGetGenericDeclaration() throws Exception {
		assertThat(type.getGenericDeclaration()).isSameAs(SerializableTypeVariableTest.class);
		assertThat(boundedType.getGenericDeclaration()).isSameAs(SerializableTypeVariableTest.class);
		assertThat(recursiveType.getGenericDeclaration()).isSameAs(SerializableTypeVariableTest.class);
	}

	@Test
	void testGetAnnotations() throws Exception {
		assertThat(type.getAnnotations()).isEmpty();
		assertThat(boundedType.getAnnotations()).isEmpty();
		assertThat(recursiveType.getAnnotations()).isEmpty();
	}

	@Test
	void testGetDeclaredAnnotations() throws Exception {
		assertThat(type.getDeclaredAnnotations()).isEmpty();
		assertThat(boundedType.getDeclaredAnnotations()).isEmpty();
		assertThat(recursiveType.getDeclaredAnnotations()).isEmpty();
	}

	@Test
	void testGetAnnotatedBounds() throws Exception {
		assertThat(type.getAnnotatedBounds()).isEmpty();
		assertThat(boundedType.getAnnotatedBounds()).isEmpty();
		assertThat(recursiveType.getAnnotatedBounds()).isEmpty();
	}

	@Test
	void testGetAnnotation() throws Exception {
		assertThat(type.getAnnotation(Override.class)).isNull();
		assertThat(boundedType.getAnnotation(Override.class)).isNull();
		assertThat(recursiveType.getAnnotation(Override.class)).isNull();
	}

	@Test
	void testEqualsHashCode() throws Exception {
		assertThat(type).satisfies(defaultEquality()
			.andEqualTo(new SerializableTypeVariable<>("C", SerializableTypeVariableTest.class))
			.andNotEqualTo(boundedType)
			.andNotEqualTo(recursiveType)
			.conventions());
		assertThat(boundedType).satisfies(defaultEquality()
			.andEqualTo(new SerializableTypeVariable<>("BC", SerializableTypeVariableTest.class).boundedBy(Collection.class))
			.andNotEqualTo(type)
			.andNotEqualTo(recursiveType)
			.conventions());
		assertThat(recursiveType).satisfies(defaultEquality()
			.andNotEqualTo(type)
			.andNotEqualTo(boundedType)
			.conventions());
	}

	@Test
	void testToString() throws Exception {
		assertThat(type.toString()).isEqualTo("C");
		assertThat(boundedType.toString()).isEqualTo("BC extends java.util.Collection");
		assertThat(recursiveType.toString()).isEqualTo("RC extends java.lang.Enum");
	}


}
