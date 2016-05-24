package net.amygdalum.testrecorder.deserializers.builder;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.Templates.arrayLiteral;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;

import java.util.List;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.deserializers.Adaptor;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DefaultAdaptor;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.values.SerializedArray;

public class DefaultArrayAdaptor extends DefaultAdaptor<SerializedArray, ObjectToSetupCode> implements Adaptor<SerializedArray, ObjectToSetupCode> {

	@Override
	public Computation tryDeserialize(SerializedArray value, ObjectToSetupCode generator) {
		TypeManager types = generator.getTypes();
		types.registerType(value.getResultType());

		return generator.forVariable(value, value.getResultType(), local -> {

			List<Computation> elementTemplates = Stream.of(value.getArray())
				.map(element -> element.accept(generator))
				.collect(toList());

			List<String> elements = elementTemplates.stream()
				.map(template -> generator.adapt(template.getValue(), value.getComponentType(), template.getType()))
				.collect(toList());

			List<String> statements = elementTemplates.stream()
				.flatMap(template -> template.getStatements().stream())
				.collect(toList());

			String arrayLiteral = arrayLiteral(types.getSimpleName(value.getResultType()), elements);

			statements.add(assignLocalVariableStatement(types.getSimpleName(value.getResultType()), local.getName(), arrayLiteral));

			return new Computation(local.getName(), value.getResultType(), statements);
		});
	}

}
