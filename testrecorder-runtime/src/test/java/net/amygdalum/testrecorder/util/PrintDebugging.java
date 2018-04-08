package net.amygdalum.testrecorder.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(LoggerExtension.class)
public class PrintDebugging {

	@Test
	public void testDebugPrint(@LogLevel("info") ByteArrayOutputStream info) throws Exception {
		String result = Debug.print("text");

		assertThat(result).isEqualTo("text");
		assertThat(info.toString()).contains("text");
	}

	@Test
	public void testDebugPrintWitMapping(@LogLevel("info") ByteArrayOutputStream info) throws Exception {
		String result = Debug.print("text", s -> s + s);

		assertThat(result).isEqualTo("text");
		assertThat(info.toString()).contains("texttext");
	}

}
