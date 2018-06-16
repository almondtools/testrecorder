package net.amygdalum.testrecorder.serializers;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.lang.invoke.SerializedLambda;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.LambdaSignature;
import net.amygdalum.testrecorder.values.SerializedLambdaObject;

public class LambdaSerializer extends AbstractCompositeSerializer implements Serializer<SerializedLambdaObject> {

	public LambdaSerializer() {
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return emptyList();
	}

	@Override
	public Stream<?> components(Object object, SerializerSession session) {
		if (!(object instanceof SerializedLambda)) {
			return Stream.empty();
		}

		SerializedLambda lambda = (SerializedLambda) object;

		return IntStream.range(0, lambda.getCapturedArgCount())
			.mapToObj(lambda::getCapturedArg);
	}

	@Override
	public SerializedLambdaObject generate(Class<?> type, SerializerSession session) {
		return new SerializedLambdaObject(type);
	}

	@Override
	public void populate(SerializedLambdaObject serializedLambda, Object object, SerializerSession session) {
		if (!(object instanceof SerializedLambda)) {
			return;
		}
		SerializedLambda lambda = (SerializedLambda) object;

		serializedLambda.setSignature(new LambdaSignature()
			.withCapturingClass(lambda.getCapturingClass())
			.withInstantiatedMethodType(lambda.getInstantiatedMethodType())
			.withFunctionalInterface(
				lambda.getFunctionalInterfaceClass(),
				lambda.getFunctionalInterfaceMethodName(),
				lambda.getFunctionalInterfaceMethodSignature())
			.withImplMethod(lambda.getImplClass(), lambda.getImplMethodKind(), lambda.getImplMethodName(), lambda.getImplMethodSignature()));

		List<SerializedValue> arguments = IntStream.range(0, lambda.getCapturedArgCount())
			.mapToObj(lambda::getCapturedArg)
			.map(o -> serializedValueOf(session, o.getClass(), o))
			.collect(toList());
		serializedLambda.setCapturedArguments(arguments);
	}

}
