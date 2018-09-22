package net.amygdalum.testrecorder.deserializers;

import static net.amygdalum.extensions.assertj.conventions.DefaultEquality.defaultEquality;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;

import org.junit.jupiter.api.Test;

public class CustomAnnotationTest {

	@Test
	void testGetAnnotation() throws Exception {
		CustomAnnotation annotation = new CustomAnnotation(Object.class, deprecated());

		assertThat(annotation.getAnnotation()).isInstanceOf(Deprecated.class);
	}

	@Test
	void testGetTarget() throws Exception {
		CustomAnnotation annotation = new CustomAnnotation(Object.class, deprecated());

		assertThat(annotation.getTarget()).isSameAs(Object.class);
	}

	@Test
	public void testEquals() throws Exception {
		Deprecated deprecated = deprecated();
		CustomAnnotation annotation = new CustomAnnotation(Object.class, deprecated);

		assertThat(annotation).satisfies(defaultEquality()
			.andEqualTo(new CustomAnnotation(Object.class, deprecated))
			.andNotEqualTo(new CustomAnnotation(Object.class, deprecated()))
			.andNotEqualTo(new CustomAnnotation(String.class, deprecated))
			.andNotEqualTo(new CustomAnnotation(Object.class, override()))
			.conventions());
	}

	private Deprecated deprecated() {
		return new Deprecated() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return Deprecated.class;
			}
		};
	}

	private Override override() {
		return new Override() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return Override.class;
			}
		};
	}

}
