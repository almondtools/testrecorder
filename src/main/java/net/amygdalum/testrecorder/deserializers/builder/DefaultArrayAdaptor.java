package net.amygdalum.testrecorder.deserializers.builder;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.Templates.arrayLiteral;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.types.Computation.variable;
import static net.amygdalum.testrecorder.util.Types.array;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.TypeManager;
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
		types.registerTypes(value.getComponentType(), componentType);
		types.registerTypes(value.getUsedTypes());
		
		Type usedType = types.mostSpecialOf(value.getUsedTypes()).orElse(Object[].class);
		
		return context.forVariable(value, local -> {

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

			statements.add(assignLocalVariableStatement(types.getVariableTypeName(usedType), local.getName(), arrayLiteral));

			return variable(local.getName(), usedType, statements);
		});
	}

}
