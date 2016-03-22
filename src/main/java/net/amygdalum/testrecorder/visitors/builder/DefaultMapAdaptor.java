package net.amygdalum.testrecorder.visitors.builder;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static net.amygdalum.testrecorder.visitors.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.visitors.Templates.callMethodStatement;
import static net.amygdalum.testrecorder.visitors.Templates.newObject;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.values.SerializedMap;
import net.amygdalum.testrecorder.visitors.Adaptor;
import net.amygdalum.testrecorder.visitors.Computation;
import net.amygdalum.testrecorder.visitors.DefaultAdaptor;
import net.amygdalum.testrecorder.visitors.TypeManager;

public class DefaultMapAdaptor extends DefaultAdaptor<SerializedMap, ObjectToSetupCode> implements Adaptor<SerializedMap, ObjectToSetupCode> {

	@Override
	public Computation tryDeserialize(SerializedMap value, TypeManager types, ObjectToSetupCode generator) {
		types.registerTypes(value.getType(), value.getValueType());

		Map<Computation, Computation> elementTemplates = value.entrySet().stream()
			.collect(toMap(entry -> entry.getKey().accept(generator), entry -> entry.getValue().accept(generator)));

		Map<String, String> elements = elementTemplates.entrySet().stream()
			.collect(toMap(entry -> entry.getKey().getValue(), entry -> entry.getValue().getValue()));

		List<String> statements = elementTemplates.entrySet().stream()
			.flatMap(entry -> Stream.concat(entry.getKey().getStatements().stream(), entry.getValue().getStatements().stream()))
			.distinct()
			.collect(toList());

		String name = generator.localVariable(value, Map.class);

		String map = newObject(types.getBestName(value.getValueType()));
		String mapInit = assignLocalVariableStatement(types.getSimpleName(value.getType()), name, map);
		statements.add(mapInit);

		for (Map.Entry<String, String> element : elements.entrySet()) {
			String putEntry = callMethodStatement(name, "put", element.getKey(), element.getValue());
			statements.add(putEntry);
		}

		return new Computation(name, true, statements);
	}

}
