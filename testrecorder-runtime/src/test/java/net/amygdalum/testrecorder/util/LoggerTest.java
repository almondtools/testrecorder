package net.amygdalum.testrecorder.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class LoggerTest {

	@Nested
	class testLog {
		@Test
		void string() throws Exception {
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			new Logger(new PrintStream(out)).log("msg");

			assertThat(out.toString()).isEqualTo("msg" + System.lineSeparator());
		}

		@Test
		void object() throws Exception {
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			new Logger(new PrintStream(out)).log(new ArrayList<>());

			assertThat(out.toString()).isEqualTo("[]" + System.lineSeparator());
		}

		@Test
		void exception() throws Exception {
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			new Logger(new PrintStream(out)).log(new RuntimeException("exc"));

			assertThat(out.toString())
				.contains("java.lang.RuntimeException: exc")
				.contains("at net.amygdalum.testrecorder.util.LoggerTest$testLog.exception");
		}
	}

	@Test
	void testDebug() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Logger.setDEBUG(new Logger(new PrintStream(out)));

		Logger.debug("msg1", "msg2");

		assertThat(out.toString())
			.contains("msg1" + System.lineSeparator())
			.contains("msg2" + System.lineSeparator());

	}

	@Test
	void testInfo() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Logger.setINFO(new Logger(new PrintStream(out)));

		Logger.info("msg1", "msg2");

		assertThat(out.toString())
			.contains("msg1" + System.lineSeparator())
			.contains("msg2" + System.lineSeparator());

	}

	@Test
	void testWarn() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Logger.setWARN(new Logger(new PrintStream(out)));

		Logger.warn("msg1", "msg2");

		assertThat(out.toString())
			.contains("msg1" + System.lineSeparator())
			.contains("msg2" + System.lineSeparator());

	}

	@Test
	void testError() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Logger.setERROR(new Logger(new PrintStream(out)));

		Logger.error("msg1", "msg2");

		assertThat(out.toString())
			.contains("msg1" + System.lineSeparator())
			.contains("msg2" + System.lineSeparator());

	}

}
