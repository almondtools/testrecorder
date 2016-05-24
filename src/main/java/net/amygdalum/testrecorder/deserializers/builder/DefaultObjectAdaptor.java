package net.amygdalum.testrecorder.deserializers.builder;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethodStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.genericObject;
import static net.amygdalum.testrecorder.deserializers.Templates.genericObjectConverter;

import java.lang.reflect.Type;
import java.util.List;

import net.amygdalum.testrecorder.deserializers.Adaptor;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DefaultAdaptor;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.util.GenericObject;
import net.amygdalum.testrecorder.values.SerializedObject;

public class DefaultObjectAdaptor extends DefaultAdaptor<SerializedObject, ObjectToSetupCode> implements Adaptor<SerializedObject, ObjectToSetupCode> {

	@Override
	public Computation tryDeserialize(SerializedObject value, ObjectToSetupCode generator) {
		TypeManager types = generator.getTypes();
		types.registerTypes(value.getType(), value.getResultType(), GenericObject.class);

		Type type = value.getType();
		Type resultType = value.getResultType();
		return generator.forVariable(value, type, definition -> {

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

			if (definition.isDefined() && !definition.isReady()) {
				String genericObject = genericObject(types.getRawTypeName(type), elements);
				statements.add(callMethodStatement(types.getBestName(GenericObject.class), "define", definition.getName(), genericObject));
			} else {
				String genericObject = genericObjectConverter(types.getRawTypeName(type), elements);
				genericObject = generator.adapt(genericObject, resultType, type);
				statements.add(assignLocalVariableStatement(types.getRawName(types.wrapHidden(resultType)), definition.getName(), genericObject));
			}

			return new Computation(definition.getName(), resultType, statements);
		});
	}

}
