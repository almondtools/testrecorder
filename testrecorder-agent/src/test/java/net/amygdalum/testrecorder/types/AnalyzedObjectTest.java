package net.amygdalum.testrecorder.types;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class AnalyzedObjectTest {

	@Test
	void testAnalyzedObject() throws Exception {
		assertThat(new AnalyzedObject(null).effectiveType).isNull();
		assertThat(new AnalyzedObject(null).object).isNull();
		assertThat(new AnalyzedObject(null).effectiveObject).isNull();
		assertThat(new AnalyzedObject("").effectiveType).isSameAs(String.class);
		assertThat(new AnalyzedObject("").object).isEqualTo("");
		assertThat(new AnalyzedObject("").effectiveObject).isEqualTo("");
	}

}
