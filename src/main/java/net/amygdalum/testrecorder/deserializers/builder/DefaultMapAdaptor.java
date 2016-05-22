package net.amygdalum.testrecorder.deserializers.builder;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethodStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.newObject;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.deserializers.Adaptor;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DefaultAdaptor;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.values.SerializedMap;

public class DefaultMapAdaptor extends DefaultAdaptor<SerializedMap, ObjectToSetupCode> implements Adaptor<SerializedMap, ObjectToSetupCode> {

	@Override
	public Computation tryDeserialize(SerializedMap value, ObjectToSetupCode generator) {
		TypeManager types = generator.getTypes();
		types.registerTypes(value.getResultType(), value.getType());

		String name = generator.localVariable(value, Map.class);

		Map<Computation, Computation> elementTemplates = value.entrySet().stream()
			.collect(toMap(entry -> entry.getKey().accept(generator), entry -> entry.getValue().accept(generator)));

		Map<String, String> elements = elementTemplates.entrySet().stream()
			.collect(toMap(
				entry -> generator.adapt(entry.getKey().getValue(), value.getMapKeyType(), entry.getKey().getType()), 
				entry -> generator.adapt(entry.getValue().getValue(), value.getMapValueType(), entry.getValue().getType())));

		List<String> statements = elementTemplates.entrySet().stream()
			.flatMap(entry -> Stream.concat(entry.getKey().getStatements().stream(), entry.getValue().getStatements().stream()))
			.distinct()
			.collect(toList());

		String map = newObject(types.getBestName(value.getType()));
		String mapInit = assignLocalVariableStatement(types.getSimpleName(value.getResultType()), name, map);
		statements.add(mapInit);

		for (Map.Entry<String, String> element : elements.entrySet()) {
			String putEntry = callMethodStatement(name, "put", element.getKey(), element.getValue());
			statements.add(putEntry);
		}

		generator.finishVariable(value);
		
		return new Computation(name, value.getResultType(), true, statements);
	}

}
