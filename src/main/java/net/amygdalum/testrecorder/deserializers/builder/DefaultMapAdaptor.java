package net.amygdalum.testrecorder.deserializers.builder;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethodStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.newObject;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.equalTypes;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DeserializerContext;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.util.Pair;
import net.amygdalum.testrecorder.values.SerializedMap;

public class DefaultMapAdaptor extends DefaultSetupGenerator<SerializedMap> implements SetupGenerator<SerializedMap> {

    @Override
    public Class<SerializedMap> getAdaptedClass() {
        return SerializedMap.class;
    }

    @Override
    public boolean matches(Type type) {
        return Map.class.isAssignableFrom(baseType(type));
    }

    @Override
    public Computation tryDeserialize(SerializedMap value, SetupGenerators generator, DeserializerContext context) {
        TypeManager types = generator.getTypes();
        Type type = value.getType();
        Type resultType = value.getResultType();
        types.registerTypes(resultType, type);

        return generator.forVariable(value, Map.class, local -> {

            List<Pair<Computation, Computation>> elementTemplates = value.entrySet().stream()
                .map(entry -> new Pair<>(
                    entry.getKey().accept(generator), 
                    entry.getValue().accept(generator)))
                .filter(pair -> pair.getElement1() != null && pair.getElement2() != null)
                .collect(toList());

            List<Pair<String, String>> elements = elementTemplates.stream()
                .map(pair -> new Pair<>(
                    generator.adapt(pair.getElement1().getValue(), value.getMapKeyType(), pair.getElement1().getType()),
                    generator.adapt(pair.getElement2().getValue(), value.getMapValueType(), pair.getElement2().getType())))
                .collect(toList());

            List<String> statements = elementTemplates.stream()
                .flatMap(pair -> Stream.concat(pair.getElement1().getStatements().stream(), pair.getElement2().getStatements().stream()))
                .distinct()
                .collect(toList());

            Type effectiveResultType = types.isHidden(resultType) ? Map.class : resultType;
            Type temporaryType = (!types.isHidden(type) && Map.class.isAssignableFrom(baseType(type)))
                ? type
                : Map.class.isAssignableFrom(baseType(effectiveResultType))
                    ? effectiveResultType
                    : Map.class;

            String tempVar = local.getName();
            if (!equalTypes(effectiveResultType, temporaryType)) {
                tempVar = generator.temporaryLocal();
            }

            String map = types.isHidden(type) ? generator.adapt(types.getWrappedName(type), temporaryType, types.wrapHidden(type)) : newObject(types.getBestName(type));
            String mapInit = assignLocalVariableStatement(types.getRelaxedName(temporaryType), tempVar, map);
            statements.add(mapInit);

            for (Pair<String, String> element : elements) {
                String putEntry = callMethodStatement(tempVar, "put", element.getElement1(), element.getElement2());
                statements.add(putEntry);
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
