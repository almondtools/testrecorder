package net.amygdalum.testrecorder.serializers;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.IntStream;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.values.LambdaSignature;
import net.amygdalum.testrecorder.values.SerializedLambdaObject;

public class LambdaSerializer implements Serializer<SerializedLambdaObject> {

	private SerializerFacade facade;

	public LambdaSerializer(SerializerFacade facade) {
		this.facade = facade;
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return emptyList();
	}

	@Override
	public SerializedLambdaObject generate(Type type) {
		return new SerializedLambdaObject(type);
	}

	@Override
	public void populate(SerializedLambdaObject serializedLambda, Object object) {
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
			.map(o -> facade.serialize(o.getClass(), o))
			.collect(toList());
		serializedLambda.setCapturedArguments(arguments);
	}

}
