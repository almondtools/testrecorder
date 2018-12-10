package net.amygdalum.testrecorder.util;

import static net.amygdalum.extensions.assertj.conventions.DefaultEquality.defaultEquality;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Type;

import org.junit.jupiter.api.Test;

public class SerializableWildcardTypeTest {

	private SerializableWildcardType unboundedType = new SerializableWildcardType(new Type[0], new Type[0]);
	private SerializableWildcardType upperBoundedType = new SerializableWildcardType(new Type[] {SerializableWildcardTypeTest.class}, new Type[0]);
	private SerializableWildcardType lowerBoundedType = new SerializableWildcardType(new Type[0], new Type[] {SerializableWildcardTypeTest.class});
	
	@Test
	public void testGetUpperBounds() throws Exception {
		assertThat(unboundedType.getUpperBounds()).isEmpty();
		assertThat(upperBoundedType.getUpperBounds()).contains(SerializableWildcardTypeTest.class);
		assertThat(lowerBoundedType.getUpperBounds()).isEmpty();
	}

	@Test
	public void testGetLowerBounds() throws Exception {
		assertThat(unboundedType.getLowerBounds()).isEmpty();
		assertThat(upperBoundedType.getLowerBounds()).isEmpty();
		assertThat(lowerBoundedType.getLowerBounds()).contains(SerializableWildcardTypeTest.class);
	}

	@Test
	public void testEqualsHashCode() throws Exception {
		assertThat(unboundedType).satisfies(defaultEquality()
			.andEqualTo(new SerializableWildcardType(new Type[0], new Type[0]))
			.andNotEqualTo(upperBoundedType)
			.andNotEqualTo(lowerBoundedType)
			.conventions());
		assertThat(lowerBoundedType).satisfies(defaultEquality()
			.andEqualTo(new SerializableWildcardType(new Type[0], new Type[] {SerializableWildcardTypeTest.class}))
			.andNotEqualTo(upperBoundedType)
			.andNotEqualTo(unboundedType)
			.conventions());
		assertThat(upperBoundedType).satisfies(defaultEquality()
			.andEqualTo(new SerializableWildcardType(new Type[] {SerializableWildcardTypeTest.class}, new Type[0]))
			.andNotEqualTo(lowerBoundedType)
			.andNotEqualTo(unboundedType)
			.conventions());
	}

	@Test
	public void testToString() throws Exception {
		assertThat(unboundedType.toString()).isEqualTo("?");
		assertThat(lowerBoundedType.toString()).isEqualTo("? super net.amygdalum.testrecorder.util.SerializableWildcardTypeTest");
		assertThat(upperBoundedType.toString()).isEqualTo("? extends net.amygdalum.testrecorder.util.SerializableWildcardTypeTest");
	}

}
