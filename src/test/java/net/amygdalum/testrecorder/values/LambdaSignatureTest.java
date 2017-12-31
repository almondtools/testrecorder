package net.amygdalum.testrecorder.values;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.Serializable;
import java.lang.invoke.MethodHandleInfo;
import java.lang.invoke.SerializedLambda;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.Lambdas;

public class LambdaSignatureTest {

	private static BiFunction<Integer, Integer, Integer> plus = new BiFunction<Integer, Integer, Integer>() {

		@Override
		public Integer apply(Integer t, Integer u) {
			return t + u;
		}
	};
	private static BiFunction<Integer, Integer, Integer> lplus = (x, y) -> x + y;
	private static BiFunction<Integer, Integer, Integer> splus = (BiFunction<Integer, Integer, Integer> & Serializable) (x, y) -> x + y;

	private int base = 22;

	public static Function<Integer, Integer> splusCapturing(int base) {
		return (Function<Integer, Integer> & Serializable) x -> x + base;
	}

	public Function<Integer, Integer> splusInstanceCapturing() {
		return (Function<Integer, Integer> & Serializable) x -> x + base;
	}

	@Test
	public void testIsSerializableLambda() throws Exception {
		assertThat(Lambdas.isSerializableLambda(plus.getClass())).isFalse();
		assertThat(Lambdas.isSerializableLambda(lplus.getClass())).isFalse();
		assertThat(Lambdas.isSerializableLambda(splus.getClass())).isTrue();
		assertThat(Lambdas.isSerializableLambda(splusCapturing(2).getClass())).isTrue();
	}

	@Test
	public void testSerializeStaticNonCapturing() throws Exception {
		SerializedLambda serializedLambda = Lambdas.serializeLambda(splus);

		assertThat(serializedLambda.getCapturedArgCount()).isEqualTo(0);
		
		LambdaSignature lambda = new LambdaSignature()
			.withCapturingClass(serializedLambda.getCapturingClass())
			.withInstantiatedMethodType(serializedLambda.getInstantiatedMethodType())
			.withFunctionalInterface(serializedLambda.getFunctionalInterfaceClass(), serializedLambda.getFunctionalInterfaceMethodName(), serializedLambda.getFunctionalInterfaceMethodSignature())
			.withImplMethod(serializedLambda.getImplClass(), serializedLambda.getImplMethodKind(), serializedLambda.getImplMethodName(), serializedLambda.getImplMethodSignature());
		assertThat(lambda.getCapturingClass()).isEqualTo("net/amygdalum/testrecorder/values/LambdaSignatureTest");

		assertThat(lambda.getFunctionalInterfaceClass()).isEqualTo("java/util/function/BiFunction");
		assertThat(lambda.getFunctionalInterfaceMethod().getDeclaringClass()).isEqualTo(BiFunction.class);
		assertThat(lambda.getFunctionalInterfaceMethodName()).isEqualTo("apply");
		assertThat(lambda.getFunctionalInterfaceMethodSignature()).isEqualTo("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
		assertThat(lambda.getFunctionalInterfaceMethod().getName()).isEqualTo("apply");
		assertThat(lambda.getFunctionalInterfaceMethod().getParameterTypes()).containsExactly(Object.class, Object.class);
		

		assertThat(lambda.getImplClass()).isEqualTo("net/amygdalum/testrecorder/values/LambdaSignatureTest");
		assertThat(lambda.getImplMethod().getDeclaringClass()).isEqualTo(LambdaSignatureTest.class);
		assertThat(lambda.getImplMethodKind()).isEqualTo(MethodHandleInfo.REF_invokeStatic);
		assertThat(lambda.getImplMethodSignature()).isEqualTo("(Ljava/lang/Integer;Ljava/lang/Integer;)Ljava/lang/Integer;");
		assertThat(lambda.getImplMethod().getParameterTypes()).containsExactly(Integer.class, Integer.class);
		
		assertThat(lambda.getInstantiatedMethodType()).isEqualTo("(Ljava/lang/Integer;Ljava/lang/Integer;)Ljava/lang/Integer;");
	}

	@Test
	public void testSerializeStaticCapturing() throws Exception {
		SerializedLambda serializedLambda = Lambdas.serializeLambda(splusCapturing(42));

		assertThat(serializedLambda.getCapturedArgCount()).isEqualTo(1);
		assertThat(serializedLambda.getCapturedArg(0)).isEqualTo(42);

		LambdaSignature lambda = new LambdaSignature()
			.withCapturingClass(serializedLambda.getCapturingClass())
			.withInstantiatedMethodType(serializedLambda.getInstantiatedMethodType())
			.withFunctionalInterface(serializedLambda.getFunctionalInterfaceClass(), serializedLambda.getFunctionalInterfaceMethodName(), serializedLambda.getFunctionalInterfaceMethodSignature())
			.withImplMethod(serializedLambda.getImplClass(), serializedLambda.getImplMethodKind(), serializedLambda.getImplMethodName(), serializedLambda.getImplMethodSignature());

		assertThat(lambda.getCapturingClass()).isEqualTo("net/amygdalum/testrecorder/values/LambdaSignatureTest");

		assertThat(lambda.getFunctionalInterfaceClass()).isEqualTo("java/util/function/Function");
		assertThat(lambda.getFunctionalInterfaceMethod().getDeclaringClass()).isEqualTo(Function.class);
		assertThat(lambda.getFunctionalInterfaceMethodName()).isEqualTo("apply");
		assertThat(lambda.getFunctionalInterfaceMethodSignature()).isEqualTo("(Ljava/lang/Object;)Ljava/lang/Object;");
		assertThat(lambda.getFunctionalInterfaceMethod().getName()).isEqualTo("apply");
		assertThat(lambda.getFunctionalInterfaceMethod().getParameterTypes()).containsExactly(Object.class);

		assertThat(lambda.getImplClass()).isEqualTo("net/amygdalum/testrecorder/values/LambdaSignatureTest");
		assertThat(lambda.getImplMethodKind()).isEqualTo(MethodHandleInfo.REF_invokeStatic);
		assertThat(lambda.getImplMethodSignature()).isEqualTo("(ILjava/lang/Integer;)Ljava/lang/Integer;");
		assertThat(lambda.getImplMethod().getParameterTypes()).containsExactly(int.class, Integer.class);

		assertThat(lambda.getInstantiatedMethodType()).isEqualTo("(Ljava/lang/Integer;)Ljava/lang/Integer;");
	}

	@Test
	public void testSerializeInstanceCapturing() throws Exception {
		SerializedLambda serializedLambda = Lambdas.serializeLambda(this.splusInstanceCapturing());

		assertThat(serializedLambda.getCapturedArgCount()).isEqualTo(1);
		assertThat(serializedLambda.getCapturedArg(0)).isEqualTo(this);

		LambdaSignature lambda = new LambdaSignature()
			.withCapturingClass(serializedLambda.getCapturingClass())
			.withInstantiatedMethodType(serializedLambda.getInstantiatedMethodType())
			.withFunctionalInterface(serializedLambda.getFunctionalInterfaceClass(), serializedLambda.getFunctionalInterfaceMethodName(), serializedLambda.getFunctionalInterfaceMethodSignature())
			.withImplMethod(serializedLambda.getImplClass(), serializedLambda.getImplMethodKind(), serializedLambda.getImplMethodName(), serializedLambda.getImplMethodSignature());

		assertThat(lambda.getCapturingClass()).isEqualTo("net/amygdalum/testrecorder/values/LambdaSignatureTest");

		assertThat(lambda.getFunctionalInterfaceClass()).isEqualTo("java/util/function/Function");
		assertThat(lambda.getFunctionalInterfaceMethod().getDeclaringClass()).isEqualTo(Function.class);
		assertThat(lambda.getFunctionalInterfaceMethodName()).isEqualTo("apply");
		assertThat(lambda.getFunctionalInterfaceMethodSignature()).isEqualTo("(Ljava/lang/Object;)Ljava/lang/Object;");
		assertThat(lambda.getFunctionalInterfaceMethod().getName()).isEqualTo("apply");
		assertThat(lambda.getFunctionalInterfaceMethod().getParameterTypes()).containsExactly(Object.class);

		assertThat(lambda.getImplClass()).isEqualTo("net/amygdalum/testrecorder/values/LambdaSignatureTest");
		assertThat(lambda.getImplMethodKind()).isEqualTo(MethodHandleInfo.REF_invokeSpecial);
		assertThat(lambda.getImplMethodSignature()).isEqualTo("(Ljava/lang/Integer;)Ljava/lang/Integer;");
		assertThat(lambda.getImplMethod().getParameterTypes()).containsExactly(Integer.class);

		assertThat(lambda.getInstantiatedMethodType()).isEqualTo("(Ljava/lang/Integer;)Ljava/lang/Integer;");
	}

}
