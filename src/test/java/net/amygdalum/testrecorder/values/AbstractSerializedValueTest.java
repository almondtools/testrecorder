package net.amygdalum.testrecorder.values;

import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.lang.annotation.Annotation;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.Annotated;
import net.amygdalum.testrecorder.util.testobjects.MyAnnotation;
import net.amygdalum.testrecorder.util.testobjects.NoAnnotation;
import net.amygdalum.testrecorder.util.testobjects.SerializedValues.ASerializedValue;

public class AbstractSerializedValueTest {

	@Test
	public void testASerializedValue() throws Exception {
		ASerializedValue value = new ASerializedValue(String.class);

		assertThat(value.getType(), sameInstance(String.class));
		assertThat(value.getResultType(), sameInstance(String.class));
	}

	@Test
	public void testSetType() throws Exception {
		ASerializedValue value = new ASerializedValue(String.class);
		value.setType(Object.class);

		assertThat(value.getType(), sameInstance(Object.class));
		assertThat(value.getResultType(), sameInstance(Object.class));
	}

	@Test
	public void testGetAnnotations() throws Exception {
		ASerializedValue value = new ASerializedValue(Annotated.class);

		assertThat(value.getAnnotations(), arrayContaining((Annotation) Annotated.class.getAnnotation(MyAnnotation.class)));
		assertThat(value.getAnnotation(MyAnnotation.class).get(), equalTo(Annotated.class.getAnnotation(MyAnnotation.class)));
		assertThat(value.getAnnotation(NoAnnotation.class).isPresent(), is(false));
	}

}
