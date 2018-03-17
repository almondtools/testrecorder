package net.amygdalum.testrecorder.deserializers.builder;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethod;
import static net.amygdalum.testrecorder.deserializers.Templates.newObject;
import static net.amygdalum.testrecorder.util.Literals.asLiteral;
import static net.amygdalum.testrecorder.util.Literals.classOf;
import static net.amygdalum.testrecorder.util.Types.baseType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.values.LambdaSignature;
import net.amygdalum.testrecorder.values.SerializedLambdaObject;

public class DefaultLambdaAdaptor extends DefaultSetupGenerator<SerializedLambdaObject> implements SetupGenerator<SerializedLambdaObject> {

	@Override
	public Class<SerializedLambdaObject> getAdaptedClass() {
		return SerializedLambdaObject.class;
	}

	@Override
	public boolean matches(Type type) {
		return true;
	}

	@Override
	public Computation tryDeserialize(SerializedLambdaObject value, SetupGenerators generator, DeserializerContext context) {
		TypeManager types = context.getTypes();
		types.registerImport(LambdaSignature.class);
		types.registerTypes(value.getUsedTypes());
		
		return context.forVariable(value, local -> {
			LambdaSignature signature = value.getSignature();

			Class<?> functionalInterfaceType = baseType(local.getType());
			types.registerTypes(local.getType(), functionalInterfaceType);

			List<String> statements = new ArrayList<>();
			List<String> deserializeArguments = new ArrayList<>();
			deserializeArguments.add(classOf(functionalInterfaceType.getName()));

			List<Computation> argumentTemplates = value.getCapturedArguments().stream()
				.map(element -> element.accept(generator, context))
				.collect(toList());

			argumentTemplates.stream()
				.map(Computation::getValue)
				.forEach(deserializeArguments::add);

			argumentTemplates.stream()
				.flatMap(template -> template.getStatements().stream())
				.forEach(statements::add);

			String lambda = newObject(types.getConstructorTypeName(LambdaSignature.class));
			lambda = callMethod(lambda, "withCapturingClass", asLiteral(signature.getCapturingClass()));
			lambda = callMethod(lambda, "withInstantiatedMethodType", asLiteral(signature.getInstantiatedMethodType()));
			lambda = callMethod(lambda, "withFunctionalInterface",
				asLiteral(signature.getFunctionalInterfaceClass()),
				asLiteral(signature.getFunctionalInterfaceMethodName()),
				asLiteral(signature.getFunctionalInterfaceMethodSignature()));
			lambda = callMethod(lambda, "withImplMethod",
				asLiteral(signature.getImplClass()),
				asLiteral(signature.getImplMethodKind()),
				asLiteral(signature.getImplMethodName()),
				asLiteral(signature.getImplMethodSignature()));
			lambda = callMethod(lambda, "deserialize", deserializeArguments);

			statements.add(assignLocalVariableStatement(types.getVariableTypeName(local.getType()), local.getName(), lambda));
			return Computation.variable(local.getName(), local.getType(), statements);
		});
	}

}
