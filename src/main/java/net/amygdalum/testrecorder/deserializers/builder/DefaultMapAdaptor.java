package net.amygdalum.testrecorder.deserializers.builder;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethodStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.cast;
import static net.amygdalum.testrecorder.deserializers.Templates.newObject;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.util.Pair;
import net.amygdalum.testrecorder.values.SerializedMap;

public class DefaultMapAdaptor extends DefaultSetupGenerator<SerializedMap> implements SetupGenerator<SerializedMap> {

	@Override
	public Class<SerializedMap> getAdaptedClass() {
		return SerializedMap.class;
	}

	@Override
	public Computation tryDeserialize(SerializedMap value, SetupGenerators generator) {
		TypeManager types = generator.getTypes();
		types.registerTypes(value.getResultType(), value.getType());

		return generator.forVariable(value, Map.class, local -> {

			List<Pair<Computation, Computation>> elementTemplates = value.entrySet().stream()
				.map(entry -> new Pair<>(entry.getKey().accept(generator), entry.getValue().accept(generator)))
				.collect(toList());

			List<Pair<String, String>> elements = elementTemplates.stream()
				.map(pair -> new Pair<>(
					generator.adapt(pair.getElement1().getValue(), value.getMapKeyType(), pair.getElement1().getType()),
					generator.adapt(pair.getElement2().getValue(), value.getMapValueType(), pair.getElement2().getType())))
				.collect(toList());

			List<String> statements = elementTemplates.stream()
				.flatMap(pair -> Stream.concat(pair.getElement1().getStatements().stream(), pair.getElement2().getStatements().stream()))
				.distinct()
				.collect(toList());

			String tempVar = equalResultTypes(value) ? local.getName() : generator.temporaryLocal();

			String map = newObject(types.getBestName(value.getType()));
			String mapInit = assignLocalVariableStatement(types.getSimpleName(value.getType()), tempVar, map);
			statements.add(mapInit);

			for (Pair<String, String> element : elements) {
				String putEntry = callMethodStatement(tempVar, "put", element.getElement1(), element.getElement2());
				statements.add(putEntry);
			}

			if (!equalResultTypes(value)) {
				String leftValue = assignableResultTypes(value) ? tempVar : cast(types.getSimpleName(value.getResultType()), tempVar);
				statements.add(assignLocalVariableStatement(types.getSimpleName(value.getResultType()), local.getName(), leftValue));
			}

			return new Computation(local.getName(), value.getResultType(), true, statements);
		});
	}

}
