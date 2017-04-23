package net.amygdalum.testrecorder.deserializers.builder;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethodStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.newObject;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.equalTypes;
import static net.amygdalum.testrecorder.util.Types.typeArgument;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DeserializerContext;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.values.SerializedSet;

public class DefaultSetAdaptor extends DefaultSetupGenerator<SerializedSet> implements SetupGenerator<SerializedSet> {

    @Override
    public Class<SerializedSet> getAdaptedClass() {
        return SerializedSet.class;
    }

    @Override
    public boolean matches(Type type) {
        return Set.class.isAssignableFrom(baseType(type));
    }

    @Override
    public Computation tryDeserialize(SerializedSet value, SetupGenerators generator, DeserializerContext context) {
        Type type = value.getType();
        Type resultType = value.getResultType();
        Type componentType = value.getComponentType();

        TypeManager types = generator.getTypes();

        Type effectiveResultType = types.bestType(resultType, Set.class);
        Type temporaryType = types.bestType(type, effectiveResultType, Set.class);
        Type componentResultType = types.isHidden(componentType) ? typeArgument(temporaryType, 0).orElse(Object.class) : componentType;

        types.registerTypes(effectiveResultType, type, componentResultType);

        return generator.forVariable(value, Set.class, local -> {

            List<Computation> elementTemplates = value.stream()
                .map(element -> withResultType(element, componentResultType).accept(generator))
                .filter(element -> element != null)
                .collect(toList());

            List<String> elements = elementTemplates.stream()
                .map(template -> generator.adapt(template.getValue(), componentResultType, template.getType()))
                .collect(toList());

            List<String> statements = elementTemplates.stream()
                .flatMap(template -> template.getStatements().stream())
                .collect(toList());

            String tempVar = local.getName();
            if (!equalTypes(effectiveResultType, temporaryType)) {
                tempVar = generator.temporaryLocal();
            }

            String set = types.isHidden(type)
                ? generator.adapt(types.getWrappedName(type), temporaryType, types.wrapHidden(type))
                : newObject(types.getBestName(type));
            String setInit = assignLocalVariableStatement(types.getRelaxedName(temporaryType), tempVar, set);
            statements.add(setInit);

            for (String element : elements) {
                String addElement = callMethodStatement(tempVar, "add", element);
                statements.add(addElement);
            }

            if (generator.needsAdaptation(effectiveResultType, temporaryType)) {
                tempVar = generator.adapt(tempVar, effectiveResultType, temporaryType);
                statements.add(assignLocalVariableStatement(types.getRelaxedName(effectiveResultType), local.getName(), tempVar));
            } else if (!equalTypes(effectiveResultType, temporaryType)) {
                statements.add(assignLocalVariableStatement(types.getRelaxedName(effectiveResultType), local.getName(), tempVar));
            }

            return new Computation(local.getName(), effectiveResultType, true, statements);
        });
    }

}
