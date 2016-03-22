package net.amygdalum.testrecorder.visitors.builder;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.visitors.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.visitors.Templates.callMethodStatement;
import static net.amygdalum.testrecorder.visitors.Templates.newObject;

import java.util.List;
import java.util.Set;

import net.amygdalum.testrecorder.values.SerializedSet;
import net.amygdalum.testrecorder.visitors.Adaptor;
import net.amygdalum.testrecorder.visitors.Computation;
import net.amygdalum.testrecorder.visitors.DefaultAdaptor;
import net.amygdalum.testrecorder.visitors.TypeManager;

public class DefaultSetAdaptor extends DefaultAdaptor<SerializedSet, ObjectToSetupCode> implements Adaptor<SerializedSet, ObjectToSetupCode> {

	@Override
	public Computation tryDeserialize(SerializedSet value, TypeManager types, ObjectToSetupCode generator) {
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

		String name = generator.localVariable(value, Set.class);

		String set = newObject(types.getBestName(value.getValueType()));
		String setInit = assignLocalVariableStatement(types.getSimpleName(value.getType()), name, set);
		statements.add(setInit);

		for (String element : elements) {
			String addElement = callMethodStatement(name, "add", element);
			statements.add(addElement);
		}

		return new Computation(name, true, statements);
	}

}
