package net.amygdalum.testrecorder.values;

import static net.amygdalum.extensions.assertj.conventions.DefaultEquality.defaultEquality;
import static net.amygdalum.testrecorder.values.SerializedNull.nullInstance;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.TestValueVisitor;

public class SerializedNullTest {

	@Test
	public void testLiteral() throws Exception {
		SerializedNull value = nullInstance();
		SerializedNull testvalue = nullInstance();

		assertThat(testvalue).isEqualTo(value);
	}

	@Test
	public void testGetResultType() throws Exception {
		SerializedNull value = nullInstance();
		value.useAs(String.class);

		assertThat(value.getUsedTypes()).containsExactly(String.class);
	}

	@Test
	public void testAccept() throws Exception {
		SerializedNull value = nullInstance();

		assertThat(value.accept(new TestValueVisitor())).isEqualTo("ReferenceType:SerializedNull");
	}

	@Test
	public void testToString() throws Exception {
		SerializedNull value = nullInstance();

		assertThat(value.toString()).isEqualTo("null");
	}

	@Test
	public void testEquals() throws Exception {
		SerializedNull typedNull = nullInstance();
		typedNull.useAs(String.class);
		
		assertThat(nullInstance()).satisfies(defaultEquality()
			.andEqualTo(nullInstance())
			.andNotEqualTo(typedNull)
			.conventions());
	}

}
