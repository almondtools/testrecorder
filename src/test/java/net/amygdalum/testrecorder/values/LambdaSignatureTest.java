package net.amygdalum.testrecorder.values;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.arrayContaining;
import static org.junit.Assert.assertThat;

import java.io.Serializable;
import java.lang.invoke.MethodHandleInfo;
import java.lang.invoke.SerializedLambda;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.junit.Test;

import net.amygdalum.testrecorder.runtime.LambdaSignature;

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
		assertThat(LambdaSignature.isSerializableLambda(plus.getClass()), is(false));
		assertThat(LambdaSignature.isSerializableLambda(lplus.getClass()), is(false));
		assertThat(LambdaSignature.isSerializableLambda(splus.getClass()), is(true));
		assertThat(LambdaSignature.isSerializableLambda(splusCapturing(2).getClass()), is(true));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSerializeStaticNonCapturing() throws Exception {
		SerializedLambda serializedLambda = LambdaSignature.serialize(splus);

		assertThat(serializedLambda.getCapturedArgCount(), equalTo(0));
		
		LambdaSignature lambda = new LambdaSignature()
			.withCapturingClass(serializedLambda.getCapturingClass())
			.withInstantiatedMethodType(serializedLambda.getInstantiatedMethodType())
			.withFunctionalInterface(serializedLambda.getFunctionalInterfaceClass(), serializedLambda.getFunctionalInterfaceMethodName(), serializedLambda.getFunctionalInterfaceMethodSignature())
			.withImplMethod(serializedLambda.getImplClass(), serializedLambda.getImplMethodKind(), serializedLambda.getImplMethodName(), serializedLambda.getImplMethodSignature());
		assertThat(lambda.getCapturingClass(), equalTo("net/amygdalum/testrecorder/values/LambdaSignatureTest"));

		assertThat(lambda.getFunctionalInterfaceClass(), equalTo("java/util/function/BiFunction"));
		assertThat(lambda.getFunctionalInterfaceMethod().getDeclaringClass(), equalTo(BiFunction.class));
		assertThat(lambda.getFunctionalInterfaceMethodName(), equalTo("apply"));
		assertThat(lambda.getFunctionalInterfaceMethodSignature(), equalTo("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"));
		assertThat(lambda.getFunctionalInterfaceMethod().getName(), equalTo("apply"));
		assertThat(lambda.getFunctionalInterfaceMethod().getParameterTypes(), arrayContaining(Object.class, Object.class));
		

		assertThat(lambda.getImplClass(), equalTo("net/amygdalum/testrecorder/values/LambdaSignatureTest"));
		assertThat(lambda.getImplMethod().getDeclaringClass(), equalTo(LambdaSignatureTest.class));
		assertThat(lambda.getImplMethodKind(), equalTo(MethodHandleInfo.REF_invokeStatic));
		assertThat(lambda.getImplMethodSignature(), equalTo("(Ljava/lang/Integer;Ljava/lang/Integer;)Ljava/lang/Integer;"));
		assertThat(lambda.getImplMethod().getParameterTypes(), arrayContaining(Integer.class, Integer.class));
		
		assertThat(lambda.getInstantiatedMethodType(), equalTo("(Ljava/lang/Integer;Ljava/lang/Integer;)Ljava/lang/Integer;"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSerializeStaticCapturing() throws Exception {
		SerializedLambda serializedLambda = LambdaSignature.serialize(splusCapturing(42));

		assertThat(serializedLambda.getCapturedArgCount(), equalTo(1));
		assertThat(serializedLambda.getCapturedArg(0), equalTo(42));

		LambdaSignature lambda = new LambdaSignature()
			.withCapturingClass(serializedLambda.getCapturingClass())
			.withInstantiatedMethodType(serializedLambda.getInstantiatedMethodType())
			.withFunctionalInterface(serializedLambda.getFunctionalInterfaceClass(), serializedLambda.getFunctionalInterfaceMethodName(), serializedLambda.getFunctionalInterfaceMethodSignature())
			.withImplMethod(serializedLambda.getImplClass(), serializedLambda.getImplMethodKind(), serializedLambda.getImplMethodName(), serializedLambda.getImplMethodSignature());

		assertThat(lambda.getCapturingClass(), equalTo("net/amygdalum/testrecorder/values/LambdaSignatureTest"));

		assertThat(lambda.getFunctionalInterfaceClass(), equalTo("java/util/function/Function"));
		assertThat(lambda.getFunctionalInterfaceMethod().getDeclaringClass(), equalTo(Function.class));
		assertThat(lambda.getFunctionalInterfaceMethodName(), equalTo("apply"));
		assertThat(lambda.getFunctionalInterfaceMethodSignature(), equalTo("(Ljava/lang/Object;)Ljava/lang/Object;"));
		assertThat(lambda.getFunctionalInterfaceMethod().getName(), equalTo("apply"));
		assertThat(lambda.getFunctionalInterfaceMethod().getParameterTypes(), arrayContaining(Object.class));

		assertThat(lambda.getImplClass(), equalTo("net/amygdalum/testrecorder/values/LambdaSignatureTest"));
		assertThat(lambda.getImplMethodKind(), equalTo(MethodHandleInfo.REF_invokeStatic));
		assertThat(lambda.getImplMethodSignature(), equalTo("(ILjava/lang/Integer;)Ljava/lang/Integer;"));
		assertThat(lambda.getImplMethod().getParameterTypes(), arrayContaining(int.class, Integer.class));

		assertThat(lambda.getInstantiatedMethodType(), equalTo("(Ljava/lang/Integer;)Ljava/lang/Integer;"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSerializeInstanceCapturing() throws Exception {
		SerializedLambda serializedLambda = LambdaSignature.serialize(this.splusInstanceCapturing());

		assertThat(serializedLambda.getCapturedArgCount(), equalTo(1));
		assertThat(serializedLambda.getCapturedArg(0), equalTo(this));

		LambdaSignature lambda = new LambdaSignature()
			.withCapturingClass(serializedLambda.getCapturingClass())
			.withInstantiatedMethodType(serializedLambda.getInstantiatedMethodType())
			.withFunctionalInterface(serializedLambda.getFunctionalInterfaceClass(), serializedLambda.getFunctionalInterfaceMethodName(), serializedLambda.getFunctionalInterfaceMethodSignature())
			.withImplMethod(serializedLambda.getImplClass(), serializedLambda.getImplMethodKind(), serializedLambda.getImplMethodName(), serializedLambda.getImplMethodSignature());

		assertThat(lambda.getCapturingClass(), equalTo("net/amygdalum/testrecorder/values/LambdaSignatureTest"));

		assertThat(lambda.getFunctionalInterfaceClass(), equalTo("java/util/function/Function"));
		assertThat(lambda.getFunctionalInterfaceMethod().getDeclaringClass(), equalTo(Function.class));
		assertThat(lambda.getFunctionalInterfaceMethodName(), equalTo("apply"));
		assertThat(lambda.getFunctionalInterfaceMethodSignature(), equalTo("(Ljava/lang/Object;)Ljava/lang/Object;"));
		assertThat(lambda.getFunctionalInterfaceMethod().getName(), equalTo("apply"));
		assertThat(lambda.getFunctionalInterfaceMethod().getParameterTypes(), arrayContaining(Object.class));

		assertThat(lambda.getImplClass(), equalTo("net/amygdalum/testrecorder/values/LambdaSignatureTest"));
		assertThat(lambda.getImplMethodKind(), equalTo(MethodHandleInfo.REF_invokeSpecial));
		assertThat(lambda.getImplMethodSignature(), equalTo("(Ljava/lang/Integer;)Ljava/lang/Integer;"));
		assertThat(lambda.getImplMethod().getParameterTypes(), arrayContaining(Integer.class));

		assertThat(lambda.getInstantiatedMethodType(), equalTo("(Ljava/lang/Integer;)Ljava/lang/Integer;"));
	}

}
