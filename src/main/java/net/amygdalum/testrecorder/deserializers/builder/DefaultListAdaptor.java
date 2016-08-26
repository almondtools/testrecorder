package net.amygdalum.testrecorder.deserializers.builder;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethodStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.cast;
import static net.amygdalum.testrecorder.deserializers.Templates.newObject;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Queue;

import net.amygdalum.testrecorder.deserializers.Adaptor;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DefaultAdaptor;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.util.Types;
import net.amygdalum.testrecorder.values.SerializedList;

public class DefaultListAdaptor extends DefaultAdaptor<SerializedList, ObjectToSetupCode> implements Adaptor<SerializedList, ObjectToSetupCode> {

	private static final List<Class<?>> LIST_CLASSES = asList(List.class, Queue.class, Deque.class);

	@Override
	public Computation tryDeserialize(SerializedList value, ObjectToSetupCode generator) {
		TypeManager types = generator.getTypes();
		types.registerTypes(value.getResultType(), value.getType());

		Class<?> listType = listClassFor(value.getType());
		
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

			String tempVar = equalResultTypes(value) ? local.getName() : generator.temporaryLocal();
			
			String list = newObject(types.getBestName(value.getType()));
			String listInit = assignLocalVariableStatement(types.getSimpleName(value.getType()), tempVar, list);
			statements.add(listInit);

			for (String element : elements) {
				String addElement = callMethodStatement(tempVar, "add", element);
				statements.add(addElement);
			}

			if (!equalResultTypes(value)) {
				String leftValue = assignableResultTypes(value) ? tempVar : cast(types.getSimpleName(value.getResultType()), tempVar);
				statements.add(assignLocalVariableStatement(types.getSimpleName(value.getResultType()), local.getName(), leftValue));
			}

			return new Computation(local.getName(), value.getResultType(), true, statements);
		});
	}

	private Class<?> listClassFor(Type type) {
		Class<?> clazz = Types.baseType(type);
		for (Class<?> listClass : LIST_CLASSES) {
			if (listClass.isAssignableFrom(clazz)) {
				return listClass;
			}
		}
		return Collection.class;
	}

}
