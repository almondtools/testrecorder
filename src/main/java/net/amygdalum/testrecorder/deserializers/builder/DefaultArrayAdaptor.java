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

		String name = generator.localVariable(value, value.getResultType());

		List<Computation> elementTemplates = Stream.of(value.getArray())
			.map(element -> element.accept(generator))
			.collect(toList());

		List<String> elements = elementTemplates.stream()
			.map(template -> template.getValue())
			.collect(toList());

		List<String> statements = elementTemplates.stream()
			.flatMap(template -> template.getStatements().stream())
			.collect(toList());

		String arrayLiteral = arrayLiteral(types.getSimpleName(value.getResultType()), elements);

		statements.add(assignLocalVariableStatement(types.getSimpleName(value.getResultType()), name, arrayLiteral));

		generator.finishVariable(value);
		
		return new Computation(name, value.getResultType(), statements);
	}

}
