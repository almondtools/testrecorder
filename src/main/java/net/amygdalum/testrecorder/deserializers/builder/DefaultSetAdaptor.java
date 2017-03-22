package net.amygdalum.testrecorder.deserializers.builder;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethodStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.newObject;
import static net.amygdalum.testrecorder.util.Types.equalTypes;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.values.SerializedSet;

public class DefaultSetAdaptor extends DefaultSetupGenerator<SerializedSet> implements SetupGenerator<SerializedSet> {

	@Override
	public Class<SerializedSet> getAdaptedClass() {
		return SerializedSet.class;
	}

	@Override
	public Computation tryDeserialize(SerializedSet value, SetupGenerators generator) {
		TypeManager types = generator.getTypes();
        Type type = value.getType();
        Type resultType = value.getResultType();
        types.registerTypes(resultType, type);

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

            String tempVar = local.getName();
            if (generator.needsAdaptation(resultType, type) || !equalTypes(resultType, type)) {
                tempVar = generator.temporaryLocal();
            }

			String set = newObject(types.getBestName(value.getType()));
			String setInit = assignLocalVariableStatement(types.getRelaxedName(value.getType()), tempVar, set);
			statements.add(setInit);

			for (String element : elements) {
				String addElement = callMethodStatement(tempVar, "add", element);
				statements.add(addElement);
			}

            if (generator.needsAdaptation(resultType, type)) {
                tempVar = generator.adapt(tempVar, resultType, type);
                statements.add(assignLocalVariableStatement(types.getRelaxedName(resultType), local.getName(), tempVar));
            } else if (!equalTypes(resultType, type)) {
                statements.add(assignLocalVariableStatement(types.getRelaxedName(resultType), local.getName(), tempVar));
            }

			return new Computation(local.getName(), value.getResultType(), true, statements);
		});
	}

}
