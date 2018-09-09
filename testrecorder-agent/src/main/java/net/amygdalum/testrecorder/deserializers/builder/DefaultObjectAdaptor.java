package net.amygdalum.testrecorder.deserializers.builder;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.FieldNamingStrategy.ensureUniqueNames;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethodStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.genericObject;
import static net.amygdalum.testrecorder.deserializers.Templates.genericObjectConverter;
import static net.amygdalum.testrecorder.types.Computation.variable;

import java.lang.reflect.Type;
import java.util.List;

import net.amygdalum.testrecorder.deserializers.Deserializer;
import net.amygdalum.testrecorder.runtime.GenericObject;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.values.SerializedObject;

public class DefaultObjectAdaptor extends DefaultSetupGenerator<SerializedObject> implements SetupGenerator<SerializedObject> {

    @Override
    public Class<SerializedObject> getAdaptedClass() {
        return SerializedObject.class;
    }

    @Override
    public Computation tryDeserialize(SerializedObject value, Deserializer generator) {
		DeserializerContext context = generator.getContext();
        TypeManager types = context.getTypes();
        types.registerTypes(value.getType(), GenericObject.class);
        types.registerTypes(value.getUsedTypes());

        Type type = value.getType();
        Type usedType = types.mostSpecialOf(value.getUsedTypes()).orElse(Object.class);
        return context.forVariable(value, usedType, local -> {

            List<Computation> elementTemplates = ensureUniqueNames(value.getFields()).stream()
                .sorted()
                .map(field -> field.accept(generator))
                .collect(toList());

            List<String> elements = elementTemplates.stream()
                .map(template -> template.getValue())
                .collect(toList());

            List<String> statements = elementTemplates.stream()
                .flatMap(template -> template.getStatements().stream())
                .collect(toList());

            Type effectiveResultType = usedType;
            if (local.isDefined() && !local.isReady()) {
                effectiveResultType = local.getType();
                String genericObject = genericObject(types.getRawClass(type), elements);
                statements.add(callMethodStatement(types.getVariableTypeName(GenericObject.class), "define", local.getName(), genericObject));
            } else {
                effectiveResultType = types.wrapHidden(usedType);
                String genericObject = genericObjectConverter(types.getRawClass(type), elements);
                genericObject = context.adapt(genericObject, effectiveResultType, type);
                statements.add(assignLocalVariableStatement(types.getRawTypeName(effectiveResultType), local.getName(), genericObject));
            }

            return variable(local.getName(), local.getType(), statements);
        });
    }

}
