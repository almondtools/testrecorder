package net.amygdalum.testrecorder.runtime;

import static net.amygdalum.testrecorder.runtime.LambdaMatcher.lambda;
import static org.assertj.core.api.Assertions.assertThat;

import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.Lambdas;
import net.amygdalum.testrecorder.util.testobjects.NoLambda;
import net.amygdalum.testrecorder.util.testobjects.NonSerializableLambda;
import net.amygdalum.testrecorder.util.testobjects.SerializableLambda;

public class LambdaMatcherTest {

	@Nested
	class testMatches {
		@Test
		void onSerializableLambda() throws Exception {
			SerializableLambda lambda = l -> (int) l;
			String name = Lambdas.serializeLambda(lambda).getImplMethodName();
			assertThat(lambda(name).matches(lambda)).isTrue();
		}

		@Test
		void onNonSerializableLambda() throws Exception {
			NonSerializableLambda lambda = l -> (int) l;
			assertThat(lambda("lambda$0").matches(lambda)).isFalse();
		}

		@Test
		void onNoLambda() throws Exception {
			NoLambda lambda = new NoLambda() {
			};
			assertThat(lambda("lambda$0").matches(lambda)).isFalse();
		}
	}

	@Test
	void testDescribeTo() throws Exception {
		StringDescription description = new StringDescription();

		lambda("lambda$0").describeTo(description);

		assertThat(description.toString()).isEqualTo("with implementation \"lambda$0\"");
	}

}
