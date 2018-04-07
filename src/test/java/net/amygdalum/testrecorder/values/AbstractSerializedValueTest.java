package net.amygdalum.testrecorder.values;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.Annotated;
import net.amygdalum.testrecorder.util.testobjects.MyAnnotation;
import net.amygdalum.testrecorder.util.testobjects.NoAnnotation;

public class AbstractSerializedValueTest {

	@Test
	public void testASerializedValue() throws Exception {
		ASerializedValue value = new ASerializedValue(String.class);

		assertThat(value.getType()).isSameAs(String.class);
		assertThat(value.getUsedTypes()).containsExactly(String.class);
	}

	@Test
	public void testSetType() throws Exception {
		ASerializedValue value = new ASerializedValue(String.class);
		value.setType(Object.class);

		assertThat(value.getType()).isSameAs(Object.class);
		assertThat(value.getUsedTypes()).containsExactly(Object.class);
	}

	@Test
	public void testGetAnnotations() throws Exception {
		ASerializedValue value = new ASerializedValue(Annotated.class);

		assertThat(value.getAnnotations()).containsExactly((Annotation) Annotated.class.getAnnotation(MyAnnotation.class));
		assertThat(value.getAnnotation(MyAnnotation.class).get()).isEqualTo(Annotated.class.getAnnotation(MyAnnotation.class));
		assertThat(value.getAnnotation(NoAnnotation.class).isPresent()).isFalse();
	}

}
