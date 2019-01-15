package net.amygdalum.testrecorder.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.amygdalum.testrecorder.util.testobjects.PseudoSynthetic;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.util.testobjects.SimpleExceptionConstructor;
import net.amygdalum.testrecorder.util.testobjects.SimpleExceptionStandardConstructor;
import net.amygdalum.testrecorder.util.testobjects.SimpleNoDefaultConstructor;
import net.amygdalum.testrecorder.util.testobjects.SimpleOnlyExceptionConstructor;
import net.amygdalum.testrecorder.util.testobjects.SimplePrivateConstructor;

public class GenericObjectGeneratorTest {

	@Nested
	class testCreate {
		@Test
		void onSimpleClass() throws Exception {
			Simple object = new GenericObjectGenerator<>(Simple.class).create(new TestDataGenerator());

			assertThat(object).isNotNull();
		}

		@Test
		void onNoDefaultConstructorClass() throws Exception {
			SimpleNoDefaultConstructor object = new GenericObjectGenerator<>(SimpleNoDefaultConstructor.class).create(new TestDataGenerator());

			assertThat(object).isNotNull();
		}

		@Test
		void onPrivateConstructorClass() throws Exception {
			SimplePrivateConstructor object = new GenericObjectGenerator<>(SimplePrivateConstructor.class).create(new TestDataGenerator());
			assertThat(object).isNotNull();
		}

		@Test
		void onExceptionConstructorClass() throws Exception {
			SimpleExceptionConstructor object = new GenericObjectGenerator<>(SimpleExceptionConstructor.class).create(new TestDataGenerator());
			assertThat(object).isNotNull();
		}

		@Test
		void onExceptionStandardConstructorClass() throws Exception {
			SimpleExceptionStandardConstructor object = new GenericObjectGenerator<>(SimpleExceptionStandardConstructor.class).create(new TestDataGenerator());
			assertThat(object).isNotNull();
		}

		@Test
		void onOnlyExceptionConstructorClass() throws Exception {
			SimpleOnlyExceptionConstructor object = new GenericObjectGenerator<>(SimpleOnlyExceptionConstructor.class).create(new TestDataGenerator());
			assertThat(object).isNull();
		}
	}

	@Nested
	class testGenerateField {
		@Test
		void onSyntheticFields() throws Exception {
			TestDataGenerator generator = Mockito.mock(TestDataGenerator.class);

			new GenericObjectGenerator<>(PseudoSynthetic.class).generateField(PseudoSynthetic.class.getDeclaredField("$attr"), generator, new PseudoSynthetic());

			verifyZeroInteractions(generator);
		}

		@Test
		void onNonSyntheticFields() throws Exception {
			TestDataGenerator generator = Mockito.mock(TestDataGenerator.class);

			new GenericObjectGenerator<>(Simple.class).generateField(Simple.class.getDeclaredField("str"), generator, new Simple());

			verify(generator).create(String.class);
		}
	}
}
