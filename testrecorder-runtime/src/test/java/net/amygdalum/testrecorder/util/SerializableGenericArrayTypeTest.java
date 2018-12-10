package net.amygdalum.testrecorder.util;

import static net.amygdalum.extensions.assertj.conventions.DefaultEquality.defaultEquality;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class SerializableGenericArrayTypeTest {

	private SerializableParameterizedType componentType = new SerializableParameterizedType(List.class, null, String.class);
	private SerializableGenericArrayType type = new SerializableGenericArrayType(componentType);

	@Test
	public void testGetGenericComponentType() throws Exception {
		assertThat(type.getGenericComponentType()).isSameAs(componentType);
	}

	@Test
	public void testEqualsHashCode() throws Exception {
		assertThat(type).satisfies(defaultEquality()
			.andEqualTo(new SerializableGenericArrayType(componentType))
			.andNotEqualTo(new SerializableGenericArrayType(new SerializableParameterizedType(Set.class, null, String.class)))
			.conventions());
	}

	@Test
	public void testToString() throws Exception {
		assertThat(type.toString()).isEqualTo("java.util.List<java.lang.String>[]");
	}

}
