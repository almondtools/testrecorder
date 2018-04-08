package net.amygdalum.testrecorder.dynamiccompile;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class DynamicClassCompilerExceptionTest {

	@Test
	public void testDynamicClassCompilerExceptionWithoutMessages() throws Exception {
		DynamicClassCompilerException exception = new DynamicClassCompilerException("message");
		assertThat(exception.getMessage()).isEqualTo("message");
		assertThat(exception.getDetailMessages()).isEmpty();
	}

	@Test
	public void testDynamicClassCompilerExceptionWithMessages() throws Exception {
		DynamicClassCompilerException exception = new DynamicClassCompilerException("message", asList("msg1","msg2"));
		assertThat(exception.getMessage()).isEqualTo("message");
		assertThat(exception.getDetailMessages()).containsExactly("msg1","msg2");
	}
}
