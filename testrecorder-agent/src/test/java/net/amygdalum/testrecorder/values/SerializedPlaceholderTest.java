package net.amygdalum.testrecorder.values;

import static net.amygdalum.testrecorder.types.DeserializerContext.NULL;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.TestValueVisitor;

public class SerializedPlaceholderTest {

	@Test
	public void testGetType() throws Exception {
		SerializedPlaceholder value = new SerializedPlaceholder(String.class);

		assertThat(value.getType()).isEqualTo(String.class);
		assertThat(value.getUsedTypes()).contains(String.class);
	}

	@Test
	public void testAccept() throws Exception {
		SerializedPlaceholder value = new SerializedPlaceholder(String.class);

		assertThat(value.accept(new TestValueVisitor(), NULL)).isEqualTo("ReferenceType:SerializedPlaceholder");
	}

	@Test
	public void testReferencedValues() throws Exception {
		SerializedPlaceholder value = new SerializedPlaceholder(String.class);

		assertThat(value.referencedValues()).isEmpty();
	}

	@Test
	public void testToString() throws Exception {
		SerializedPlaceholder value = new SerializedPlaceholder(String.class);

		assertThat(value.toString()).startsWith("placeholder java.lang.String");
	}

}
