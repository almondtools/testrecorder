package net.amygdalum.testrecorder.util;

import static net.amygdalum.extensions.assertj.conventions.UtilityClass.utilityClass;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.invoke.SerializedLambda;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.NoLambda;
import net.amygdalum.testrecorder.util.testobjects.NonSerializableLambda;
import net.amygdalum.testrecorder.util.testobjects.SerializableLambda;

public class LambdasTest {

	@Test
	void testLambdas() throws Exception {
		assertThat(Lambdas.class).satisfies(utilityClass().conventions());
	}

	@Test
	void testIsSerializableLambdaForNoLambda() throws Exception {
		NoLambda nolambda = new NoLambda() {
		};
		
		assertThat(Lambdas.isSerializableLambda(nolambda.getClass())).isFalse();
	}
	
	@Test
	void testIsSerializableLambdaForNonserializableLambda() throws Exception {
		NonSerializableLambda lambda = l -> (int) l;;
		
		assertThat(Lambdas.isSerializableLambda(lambda.getClass())).isFalse();
	}
	
	@Test
	void testIsSerializableLambdaForSerializableLambda() throws Exception {
		SerializableLambda lambda = l -> (int) l;;
		
		assertThat(Lambdas.isSerializableLambda(lambda.getClass())).isTrue();
	}
	
	@Test
	void testSerializeLambdaForSerializableLambda() throws Exception {
		SerializableLambda lambda = l -> (int) l;;
		
		SerializedLambda serializedLambda = Lambdas.serializeLambda(lambda);
		
		assertThat(serializedLambda.getFunctionalInterfaceClass()).isEqualTo(SerializableLambda.class.getName().replace('.', '/'));
	}
	
	@Test
	void testSerializeLambdaForNonSerializableLambda() throws Exception {
		NonSerializableLambda lambda = l -> (int) l;;
		
		SerializedLambda serializedLambda = Lambdas.serializeLambda(lambda);
		
		assertThat(serializedLambda).isNull();
	}

}
