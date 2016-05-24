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

		return generator.forVariable(value, Set.class, local -> {

			List<Computation> elementTemplates = value.stream()
				.map(element -> element.accept(generator))
				.collect(toList());

			List<String> elements = elementTemplates.stream()
				.map(template -> generator.adapt(template.getValue(), value.getComponentType(), template.getType()))
				.collect(toList());

			List<String> statements = elementTemplates.stream()
				.flatMap(template -> template.getStatements().stream())
				.collect(toList());

			String set = newObject(types.getBestName(value.getType()));
			String setInit = assignLocalVariableStatement(types.getSimpleName(value.getResultType()), local.getName(), set);
			statements.add(setInit);

			for (String element : elements) {
				String addElement = callMethodStatement(local.getName(), "add", element);
				statements.add(addElement);
			}

			return new Computation(local.getName(), value.getResultType(), true, statements);
		});
	}

}
