package net.amygdalum.testrecorder.values;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.invoke.MethodHandleInfo;
import java.lang.invoke.SerializedLambda;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.Lambdas;
import net.amygdalum.testrecorder.util.testobjects.LambdaFunctions;

public class LambdaSignatureTest {

	@Test
	void testSerializeStaticNonCapturing() throws Exception {
		SerializedLambda serializedLambda = Lambdas.serializeLambda(LambdaFunctions.splus);

		assertThat(serializedLambda.getCapturedArgCount()).isEqualTo(0);

		LambdaSignature lambda = new LambdaSignature()
			.withCapturingClass(serializedLambda.getCapturingClass())
			.withInstantiatedMethodType(serializedLambda.getInstantiatedMethodType())
			.withFunctionalInterface(serializedLambda.getFunctionalInterfaceClass(), serializedLambda.getFunctionalInterfaceMethodName(), serializedLambda.getFunctionalInterfaceMethodSignature())
			.withImplMethod(serializedLambda.getImplClass(), serializedLambda.getImplMethodKind(), serializedLambda.getImplMethodName(), serializedLambda.getImplMethodSignature());
		assertThat(lambda.getCapturingClass()).isEqualTo("net/amygdalum/testrecorder/util/testobjects/LambdaFunctions");

		assertThat(lambda.getFunctionalInterfaceClass()).isEqualTo("java/util/function/BiFunction");
		assertThat(lambda.getFunctionalInterfaceMethodName()).isEqualTo("apply");
		assertThat(lambda.getFunctionalInterfaceMethodSignature()).isEqualTo("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");

		assertThat(lambda.getImplClass()).isEqualTo("net/amygdalum/testrecorder/util/testobjects/LambdaFunctions");
		assertThat(lambda.getImplMethodKind()).isEqualTo(MethodHandleInfo.REF_invokeStatic);
		assertThat(lambda.getImplMethodSignature()).isEqualTo("(Ljava/lang/Integer;Ljava/lang/Integer;)Ljava/lang/Integer;");

		assertThat(lambda.getInstantiatedMethodType()).isEqualTo("(Ljava/lang/Integer;Ljava/lang/Integer;)Ljava/lang/Integer;");
	}

	@Test
	void testSerializeStaticCapturing() throws Exception {
		SerializedLambda serializedLambda = Lambdas.serializeLambda(LambdaFunctions.splusCapturing(42));

		assertThat(serializedLambda.getCapturedArgCount()).isEqualTo(1);
		assertThat(serializedLambda.getCapturedArg(0)).isEqualTo(42);

		LambdaSignature lambda = new LambdaSignature()
			.withCapturingClass(serializedLambda.getCapturingClass())
			.withInstantiatedMethodType(serializedLambda.getInstantiatedMethodType())
			.withFunctionalInterface(serializedLambda.getFunctionalInterfaceClass(), serializedLambda.getFunctionalInterfaceMethodName(), serializedLambda.getFunctionalInterfaceMethodSignature())
			.withImplMethod(serializedLambda.getImplClass(), serializedLambda.getImplMethodKind(), serializedLambda.getImplMethodName(), serializedLambda.getImplMethodSignature());

		assertThat(lambda.getCapturingClass()).isEqualTo("net/amygdalum/testrecorder/util/testobjects/LambdaFunctions");

		assertThat(lambda.getFunctionalInterfaceClass()).isEqualTo("java/util/function/Function");
		assertThat(lambda.getFunctionalInterfaceMethodName()).isEqualTo("apply");
		assertThat(lambda.getFunctionalInterfaceMethodSignature()).isEqualTo("(Ljava/lang/Object;)Ljava/lang/Object;");

		assertThat(lambda.getImplClass()).isEqualTo("net/amygdalum/testrecorder/util/testobjects/LambdaFunctions");
		assertThat(lambda.getImplMethodKind()).isEqualTo(MethodHandleInfo.REF_invokeStatic);
		assertThat(lambda.getImplMethodSignature()).isEqualTo("(ILjava/lang/Integer;)Ljava/lang/Integer;");

		assertThat(lambda.getInstantiatedMethodType()).isEqualTo("(Ljava/lang/Integer;)Ljava/lang/Integer;");
	}

	@Test
	void testSerializeInstanceCapturing() throws Exception {
		SerializedLambda serializedLambda = Lambdas.serializeLambda(LambdaFunctions.invokeSpecial());

		assertThat(serializedLambda.getCapturedArgCount()).isEqualTo(1);
		assertThat(serializedLambda.getCapturedArg(0)).isInstanceOf(LambdaFunctions.class);

		LambdaSignature lambda = new LambdaSignature()
			.withCapturingClass(serializedLambda.getCapturingClass())
			.withInstantiatedMethodType(serializedLambda.getInstantiatedMethodType())
			.withFunctionalInterface(serializedLambda.getFunctionalInterfaceClass(), serializedLambda.getFunctionalInterfaceMethodName(), serializedLambda.getFunctionalInterfaceMethodSignature())
			.withImplMethod(serializedLambda.getImplClass(), serializedLambda.getImplMethodKind(), serializedLambda.getImplMethodName(), serializedLambda.getImplMethodSignature());

		assertThat(lambda.getCapturingClass()).isEqualTo("net/amygdalum/testrecorder/util/testobjects/LambdaFunctions");

		assertThat(lambda.getFunctionalInterfaceClass()).isEqualTo("java/util/function/Function");
		assertThat(lambda.getFunctionalInterfaceMethodName()).isEqualTo("apply");
		assertThat(lambda.getFunctionalInterfaceMethodSignature()).isEqualTo("(Ljava/lang/Object;)Ljava/lang/Object;");

		assertThat(lambda.getImplClass()).isEqualTo("net/amygdalum/testrecorder/util/testobjects/LambdaFunctions");
		assertThat(lambda.getImplMethodKind()).isEqualTo(MethodHandleInfo.REF_invokeSpecial);
		assertThat(lambda.getImplMethodSignature()).isEqualTo("(Ljava/lang/Integer;)Ljava/lang/Integer;");

		assertThat(lambda.getInstantiatedMethodType()).isEqualTo("(Ljava/lang/Integer;)Ljava/lang/Integer;");
	}

	@SuppressWarnings("unchecked")
	@Test
	void testDeserializeInvokeStatic() throws Exception {
		LambdaSignature signature = signature(LambdaFunctions.invokeStatic());
		assertThat(signature.getImplMethodKind()).isEqualTo(MethodHandleInfo.REF_invokeStatic);
		
		Function<Integer, Integer> result = (Function<Integer, Integer>) signature.deserialize(Function.class);
		
		assertThat(result.apply(1)).isEqualTo(2);
	}

	@SuppressWarnings("unchecked")
	@Test
	void testDeserializeInvokeSpecial() throws Exception {
		LambdaSignature signature = signature(LambdaFunctions.invokeSpecial());
		assertThat(signature.getImplMethodKind()).isEqualTo(MethodHandleInfo.REF_invokeSpecial);

		Function<Integer, Integer> result = (Function<Integer, Integer>) signature.deserialize(Function.class, new LambdaFunctions(2));
		
		assertThat(result.apply(1)).isEqualTo(3);
	}

	@SuppressWarnings("unchecked")
	@Test
	void testDeserializeInvokeVirtual() throws Exception {
		LambdaSignature signature = signature(LambdaFunctions.invokeVirtual());
		assertThat(signature.getImplMethodKind()).isEqualTo(MethodHandleInfo.REF_invokeVirtual);

		Function<Integer, Integer> result = (Function<Integer, Integer>) signature.deserialize(Function.class, new LambdaFunctions.VirtualIntegerFunction());
		
		assertThat(result.apply(1)).isEqualTo(2);
	}

	@SuppressWarnings("unchecked")
	@Test
	void testDeserializeInvokeInterface() throws Exception {
		LambdaSignature signature = signature(LambdaFunctions.invokeInterface());
		assertThat(signature.getImplMethodKind()).isEqualTo(MethodHandleInfo.REF_invokeInterface);

		Function<Integer, Integer> result = (Function<Integer, Integer>) signature.deserialize(Function.class, new LambdaFunctions.InterfaceIntegerFunction());
		
		assertThat(result.apply(1)).isEqualTo(2);
	}

	@SuppressWarnings("unchecked")
	@Test
	void testDeserializeNewInvokeSpecial() throws Exception {
		LambdaSignature signature = signature(LambdaFunctions.invokeNewSpecial());
		assertThat(signature.getImplMethodKind()).isEqualTo(MethodHandleInfo.REF_newInvokeSpecial);

		Supplier<String> result = (Supplier<String>) signature.deserialize(Supplier.class);
		
		assertThat(result.get()).isEqualTo("");
	}

	private LambdaSignature signature(Object rawlambda) {
		SerializedLambda lambda = Lambdas.serializeLambda(rawlambda);
		
		return new LambdaSignature()
			.withCapturingClass(lambda.getCapturingClass())
			.withInstantiatedMethodType(lambda.getInstantiatedMethodType())
			.withFunctionalInterface(
				lambda.getFunctionalInterfaceClass(),
				lambda.getFunctionalInterfaceMethodName(),
				lambda.getFunctionalInterfaceMethodSignature())
			.withImplMethod(lambda.getImplClass(), lambda.getImplMethodKind(), lambda.getImplMethodName(), lambda.getImplMethodSignature());
	}

}
