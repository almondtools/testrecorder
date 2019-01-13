package net.amygdalum.testrecorder.util;

import static net.amygdalum.extensions.assertj.conventions.DefaultEquality.defaultEquality;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class SerializableParameterizedTypeTest {

	private SerializableParameterizedType type = new SerializableParameterizedType(List.class, null, String.class);
	private SerializableParameterizedType typeWithOwner = new SerializableParameterizedType(List.class, SerializableParameterizedTypeTest.class, String.class);
	
	@Test
	void testGetRawType() throws Exception {
		assertThat(type.getRawType()).isSameAs(List.class);
		assertThat(typeWithOwner.getRawType()).isSameAs(List.class);
	}

	@Test
	void testGetOwnerType() throws Exception {
		assertThat(type.getOwnerType()).isSameAs(null);
		assertThat(typeWithOwner.getOwnerType()).isSameAs(SerializableParameterizedTypeTest.class);
	}

	@Test
	void testGetActualTypeArguments() throws Exception {
		assertThat(type.getActualTypeArguments()).isEqualTo(new Type[]{String.class});
		assertThat(typeWithOwner.getActualTypeArguments()).isEqualTo(new Type[]{String.class});
	}

	@Test
	void testEqualsHashCode() throws Exception {
		assertThat(type).satisfies(defaultEquality()
			.andNotEqualTo(typeWithOwner)
			.andEqualTo(new SerializableParameterizedType(List.class, null, String.class))
			.andNotEqualTo(new SerializableParameterizedType(Set.class, null, String.class))
			.andNotEqualTo(new SerializableParameterizedType(List.class, null, Integer.class))
			.conventions());
		assertThat(typeWithOwner).satisfies(defaultEquality()
			.andNotEqualTo(type)
			.andEqualTo(new SerializableParameterizedType(List.class, SerializableParameterizedTypeTest.class, String.class))
			.andNotEqualTo(new SerializableParameterizedType(Set.class, SerializableParameterizedTypeTest.class, String.class))
			.andNotEqualTo(new SerializableParameterizedType(List.class, SerializableParameterizedTypeTest.class, Integer.class))
			.conventions());
	}

	@Test
	void testToString() throws Exception {
		assertThat(type.toString()).isEqualTo("java.util.List<java.lang.String>");
		assertThat(typeWithOwner.toString()).isEqualTo("java.util.List<java.lang.String>");
	}

}
