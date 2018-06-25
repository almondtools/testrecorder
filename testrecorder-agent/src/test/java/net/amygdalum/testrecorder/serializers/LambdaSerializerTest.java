package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.lang.invoke.MethodHandleInfo;
import java.lang.invoke.SerializedLambda;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.util.Lambdas;
import net.amygdalum.testrecorder.values.SerializedLambdaObject;
import net.amygdalum.testrecorder.values.SerializedList;

public class LambdaSerializerTest {

	private SerializerSession session;
	private Serializer<SerializedLambdaObject> serializer;

	@BeforeEach
	void before() throws Exception {
		session = mock(SerializerSession.class);
		serializer = new LambdaSerializer();
	}

	@Test
	void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses()).isEmpty();
	}

	@Test
	void testGenerate() throws Exception {
		SerializedLambdaObject value = serializer.generate(Callable.class, session);
		value.useAs(parameterized(Callable.class, null, String.class));

		assertThat(value.getUsedTypes()).containsExactly(parameterized(Callable.class, null, String.class));
		assertThat(value.getType()).isEqualTo(Callable.class);
	}

	@Test
	void testComponentsOnNonSerializedLambda() throws Exception {
		Callable<String> nonSerialized = () -> "result";

		assertThat(serializer.components(nonSerialized, session)).isEmpty();
	}

	@Test
	void testComponents() throws Exception {
		Callable<String> nonSerialized = (Callable<String> & Serializable) () -> "result";
		SerializedLambda serialized = Lambdas.serializeLambda(nonSerialized);

		assertThat(serializer.components(serialized, session)).isEmpty();
	}

	@Test
	void testComponentsWithCapturedArgs() throws Exception {
		List<String> world = asList("World1", "World2");
		int times = 2;
		Callable<String> nonSerialized = (Callable<String> & Serializable) () -> {
			String r = "";
			for (int i = 0; i < times; i++) {
				r += "Hello " + world.get(i);
			}
			return r;
		};
		SerializedLambda serialized = Lambdas.serializeLambda(nonSerialized);

		assertThat(serializer.components(serialized, session).map(o -> (Object) o)).contains(world);
	}

	@Test
	void testPopulateOnNonSerializedLambda() throws Exception {
		Callable<String> nonSerialized = () -> "result";
		SerializedLambdaObject serializedObject = new SerializedLambdaObject(Callable.class);

		serializer.populate(serializedObject, nonSerialized, session);

		assertThat(serializedObject.getSignature()).isNull();
		assertThat(serializedObject.getCapturedArguments()).isNull();
	}

	@Test
	void testPopulate() throws Exception {
		Callable<String> nonSerialized = (Callable<String> & Serializable) () -> "result";
		SerializedLambda serialized = Lambdas.serializeLambda(nonSerialized);
		SerializedLambdaObject serializedObject = new SerializedLambdaObject(Callable.class);

		serializer.populate(serializedObject, serialized, session);

		assertThat(serializedObject.getSignature().getCapturingClass()).isEqualTo("net/amygdalum/testrecorder/serializers/LambdaSerializerTest");
		assertThat(serializedObject.getSignature().getFunctionalInterfaceClass()).isEqualTo("java/util/concurrent/Callable");
		assertThat(serializedObject.getSignature().getFunctionalInterfaceMethodName()).isEqualTo("call");
		assertThat(serializedObject.getSignature().getFunctionalInterfaceMethodSignature()).isEqualTo("()Ljava/lang/Object;");
		assertThat(serializedObject.getSignature().getImplClass()).isEqualTo("net/amygdalum/testrecorder/serializers/LambdaSerializerTest");
		assertThat(serializedObject.getSignature().getImplMethodKind()).isEqualTo(MethodHandleInfo.REF_invokeStatic);
		assertThat(serializedObject.getSignature().getImplMethodName()).contains("lambda$");
		assertThat(serializedObject.getSignature().getImplMethodSignature()).isEqualTo("()Ljava/lang/String;");
		assertThat(serializedObject.getSignature().getInstantiatedMethodType()).isEqualTo("()Ljava/lang/String;");
		assertThat(serializedObject.getCapturedArguments()).isEmpty();
	}
	
	@Test
	void testPopulateWithCapturedArgs() throws Exception {
		List<String> world = new ArrayList<>(asList("World1", "World2"));
		int times = 2;
		Callable<String> nonSerialized = (Callable<String> & Serializable) () -> {
			String r = "";
			for (int i = 0; i < times; i++) {
				r += "Hello " + world.get(i);
			}
			return r;
		};
		SerializedLambda serialized = Lambdas.serializeLambda(nonSerialized);
		SerializedLambdaObject serializedObject = new SerializedLambdaObject(Callable.class);
		SerializedList capturedList = new SerializedList(ArrayList.class);
		when(session.ref(world, ArrayList.class)).thenReturn(capturedList);

		serializer.populate(serializedObject, serialized, session);

		assertThat(serializedObject.getSignature().getCapturingClass()).isEqualTo("net/amygdalum/testrecorder/serializers/LambdaSerializerTest");
		assertThat(serializedObject.getSignature().getFunctionalInterfaceClass()).isEqualTo("java/util/concurrent/Callable");
		assertThat(serializedObject.getSignature().getFunctionalInterfaceMethodName()).isEqualTo("call");
		assertThat(serializedObject.getSignature().getFunctionalInterfaceMethodSignature()).isEqualTo("()Ljava/lang/Object;");
		assertThat(serializedObject.getSignature().getImplClass()).isEqualTo("net/amygdalum/testrecorder/serializers/LambdaSerializerTest");
		assertThat(serializedObject.getSignature().getImplMethodKind()).isEqualTo(MethodHandleInfo.REF_invokeStatic);
		assertThat(serializedObject.getSignature().getImplMethodName()).contains("lambda$");
		assertThat(serializedObject.getSignature().getImplMethodSignature()).isEqualTo("(ILjava/util/List;)Ljava/lang/String;");
		assertThat(serializedObject.getSignature().getInstantiatedMethodType()).isEqualTo("()Ljava/lang/String;");
		assertThat(serializedObject.getCapturedArguments()).contains(capturedList);
	}

}
