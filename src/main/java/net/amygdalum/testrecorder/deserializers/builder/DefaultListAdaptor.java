package net.amygdalum.testrecorder.deserializers.builder;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethodStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.newObject;

import java.util.List;

import net.amygdalum.testrecorder.deserializers.Adaptor;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DefaultAdaptor;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.values.SerializedList;

public class DefaultListAdaptor extends DefaultAdaptor<SerializedList, ObjectToSetupCode> implements Adaptor<SerializedList, ObjectToSetupCode> {

	@Override
	public Computation tryDeserialize(SerializedList value, TypeManager types, ObjectToSetupCode generator) {
		types.registerTypes(value.getType(), value.getValueType());

		List<Computation> elementTemplates = value.stream()
			.map(element -> element.accept(generator))
			.collect(toList());

		List<String> elements = elementTemplates.stream()
			.map(template -> template.getValue())
			.collect(toList());

		List<String> statements = elementTemplates.stream()
			.flatMap(template -> template.getStatements().stream())
			.collect(toList());

		String name = generator.localVariable(value, List.class);

		String list = newObject(types.getBestName(value.getValueType()));
		String listInit = assignLocalVariableStatement(types.getSimpleName(value.getType()), name, list);
		statements.add(listInit);

		for (String element : elements) {
			String addElement = callMethodStatement(name, "add", element);
			statements.add(addElement);
		}

		return new Computation(name, true, statements);
	}

}
