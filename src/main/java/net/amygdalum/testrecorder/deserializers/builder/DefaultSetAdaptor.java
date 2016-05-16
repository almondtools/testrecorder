package net.amygdalum.testrecorder.deserializers.builder;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethodStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.newObject;

import java.util.List;
import java.util.Set;

import net.amygdalum.testrecorder.deserializers.Adaptor;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DefaultAdaptor;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.values.SerializedSet;

public class DefaultSetAdaptor extends DefaultAdaptor<SerializedSet, ObjectToSetupCode> implements Adaptor<SerializedSet, ObjectToSetupCode> {

	@Override
	public Computation tryDeserialize(SerializedSet value, ObjectToSetupCode generator) {
		TypeManager types = generator.getTypes();
		types.registerTypes(value.getResultType(), value.getType());

		String name = generator.localVariable(value, Set.class);

		List<Computation> elementTemplates = value.stream()
			.map(element -> element.accept(generator))
			.collect(toList());

		List<String> elements = elementTemplates.stream()
			.map(template -> template.getValue())
			.collect(toList());

		List<String> statements = elementTemplates.stream()
			.flatMap(template -> template.getStatements().stream())
			.collect(toList());

		String set = newObject(types.getBestName(value.getType()));
		String setInit = assignLocalVariableStatement(types.getSimpleName(value.getResultType()), name, set);
		statements.add(setInit);

		for (String element : elements) {
			String addElement = callMethodStatement(name, "add", element);
			statements.add(addElement);
		}

		generator.finishVariable(value);
		
		return new Computation(name, true, statements);
	}

}
