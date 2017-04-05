package net.amygdalum.testrecorder.deserializers.builder;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethodStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.newObject;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.equalTypes;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Queue;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DeserializerContext;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.values.SerializedList;

public class DefaultListAdaptor extends DefaultSetupGenerator<SerializedList> implements SetupGenerator<SerializedList> {

	private static final List<Class<?>> LIST_CLASSES = asList(List.class, Queue.class, Deque.class);

	@Override
	public Class<SerializedList> getAdaptedClass() {
		return SerializedList.class;
	}

	@Override
	public Computation tryDeserialize(SerializedList value, SetupGenerators generator, DeserializerContext context) {
		TypeManager types = generator.getTypes();
		Type type = value.getType();
        Type resultType = value.getResultType();
        types.registerTypes(resultType, type);

		Class<?> listType = listClassFor(type);

		return generator.forVariable(value, listType, local -> {

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

			String list = newObject(types.getBestName(type));
			String listInit = assignLocalVariableStatement(types.getRelaxedName(type), tempVar, list);
			statements.add(listInit);

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

			return new Computation(local.getName(), resultType, true, statements);
		});
	}

	private Class<?> listClassFor(Type type) {
		Class<?> clazz = baseType(type);
		for (Class<?> listClass : LIST_CLASSES) {
			if (listClass.isAssignableFrom(clazz)) {
				return listClass;
			}
		}
		return Collection.class;
	}

}
