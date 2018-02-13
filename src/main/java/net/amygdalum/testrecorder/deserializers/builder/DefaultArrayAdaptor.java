package net.amygdalum.testrecorder.deserializers.builder;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.Computation.variable;
import static net.amygdalum.testrecorder.deserializers.Templates.arrayLiteral;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.util.Types.array;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.values.SerializedArray;

public class DefaultArrayAdaptor extends DefaultSetupGenerator<SerializedArray> implements SetupGenerator<SerializedArray> {

	@Override
	public Class<SerializedArray> getAdaptedClass() {
		return SerializedArray.class;
	}

	@Override
	public Computation tryDeserialize(SerializedArray value, SetupGenerators generator, DeserializerContext context) {
		TypeManager types = context.getTypes();
		Type componentType = types.bestType(value.getComponentType(), Object.class);
		types.registerTypes(value.getResultType(), value.getComponentType(), componentType);
		

		return context.forVariable(value, value.getResultType(), local -> {

			List<Computation> elementTemplates = Stream.of(value.getArray())
				.map(element -> element.accept(generator, context))
				.collect(toList());

			List<String> elements = elementTemplates.stream()
				.map(template -> context.adapt(template.getValue(), componentType, template.getType()))
				.collect(toList());

			List<String> statements = elementTemplates.stream()
				.flatMap(template -> template.getStatements().stream())
				.collect(toList());

			String arrayLiteral = arrayLiteral(types.getVariableTypeName(array(componentType)), elements);

			statements.add(assignLocalVariableStatement(types.getVariableTypeName(value.getResultType()), local.getName(), arrayLiteral));

			return variable(local.getName(), value.getResultType(), statements);
		});
	}

}
