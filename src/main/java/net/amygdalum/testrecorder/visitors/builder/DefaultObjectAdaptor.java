package net.amygdalum.testrecorder.visitors.builder;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.visitors.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.visitors.Templates.genericObjectConverter;

import java.util.List;

import net.amygdalum.testrecorder.util.GenericObject;
import net.amygdalum.testrecorder.values.SerializedObject;
import net.amygdalum.testrecorder.visitors.Adaptor;
import net.amygdalum.testrecorder.visitors.Computation;
import net.amygdalum.testrecorder.visitors.DefaultAdaptor;
import net.amygdalum.testrecorder.visitors.TypeManager;

public class DefaultObjectAdaptor extends DefaultAdaptor<SerializedObject, ObjectToSetupCode> implements Adaptor<SerializedObject, ObjectToSetupCode> {

	@Override
	public Computation tryDeserialize(SerializedObject value, TypeManager types, ObjectToSetupCode generator) {
		types.registerTypes(value.getType(), GenericObject.class);

		List<Computation> elementTemplates = value.getFields().stream()
			.sorted()
			.map(element -> element.accept(generator))
			.collect(toList());

		List<String> elements = elementTemplates.stream()
			.map(template -> template.getValue())
			.collect(toList());

		List<String> statements = elementTemplates.stream()
			.flatMap(template -> template.getStatements().stream())
			.collect(toList());

		String genericObject = genericObjectConverter(types.getRawTypeName(value.getValueType()), elements);

		String name = generator.localVariable(value, value.getValueType());
		statements.add(assignLocalVariableStatement(types.getRawName(value.getValueType()), name, genericObject));

		return new Computation(name, statements);
	}

}
