package net.amygdalum.testrecorder.deserializers.builder;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethodStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.newObject;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.equalTypes;
import static net.amygdalum.testrecorder.util.Types.typeArgument;
import static net.amygdalum.testrecorder.util.Types.typeArguments;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.SerializedReferenceType;
import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DeserializerContext;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.util.Pair;
import net.amygdalum.testrecorder.util.Types;

public abstract class DefaultGenericMapAdaptor<T extends SerializedReferenceType> extends DefaultSetupGenerator<T> implements SetupGenerator<T> {

    public abstract Class<?>[] matchingTypes();

    public abstract Type keyType(T value);

    public abstract Type valueType(T value);

    public abstract Stream<Pair<SerializedValue, SerializedValue>> entries(T value);

    @Override
    public boolean matches(Type type) {
        return matchType(type).isPresent();
    }

    public Optional<Class<?>> matchType(Type type) {
        return Stream.of(matchingTypes())
            .filter(clazz -> clazz.isAssignableFrom(baseType(type)))
            .findFirst();
    }

    @Override
    public Computation tryDeserialize(T value, SetupGenerators generator, DeserializerContext context) {
        Type type = value.getType();
        Type resultType = value.getResultType();
        Type mapKeyType = keyType(value);
        Type mapValueType = valueType(value);

        Class<?> matchingType = matchType(type).get();

        TypeManager types = generator.getTypes();

        Type effectiveResultType = types.bestType(resultType, matchingType);
        Type temporaryType = types.bestType(type, effectiveResultType, matchingType);
        Type keyResultType = types.isHidden(mapKeyType) ? typeArgument(temporaryType, 0).orElse(Object.class) : mapKeyType;
        Type valueResultType = types.isHidden(mapValueType) ? typeArgument(temporaryType, 1).orElse(Object.class) : mapValueType;

        types.registerTypes(effectiveResultType, temporaryType, type, keyResultType, valueResultType);

        return generator.forVariable(value, matchingType, local -> {

            List<Pair<Computation, Computation>> elementTemplates = entries(value)
                .map(entry -> new Pair<>(
                    withResultType(entry.getElement1(), keyResultType).accept(generator),
                    withResultType(entry.getElement2(), valueResultType).accept(generator)))
                .filter(pair -> pair.getElement1() != null && pair.getElement2() != null)
                .collect(toList());

            List<Pair<String, String>> elements = elementTemplates.stream()
                .map(pair -> new Pair<>(
                    generator.adapt(pair.getElement1().getValue(), keyResultType, pair.getElement1().getType()),
                    generator.adapt(pair.getElement2().getValue(), valueResultType, pair.getElement2().getType())))
                .collect(toList());

            List<String> statements = elementTemplates.stream()
                .flatMap(pair -> Stream.concat(pair.getElement1().getStatements().stream(), pair.getElement2().getStatements().stream()))
                .distinct()
                .collect(toList());

            String tempVar = local.getName();
            if (!equalTypes(effectiveResultType, temporaryType)) {
                tempVar = generator.temporaryLocal();
            }

            String map = types.isHidden(type)
                ? generator.adapt(types.getWrappedName(type), temporaryType, types.wrapHidden(type))
                : newObject(types.getConstructorTypeName(type));
            String temporaryTypeName = Optional.of(temporaryType)
                .filter(t -> typeArguments(t).count() > 0)
                .filter(t -> typeArguments(t).allMatch(Types::isActual))
                .map(t -> types.getVariableTypeName(t))
                .orElse(types.getRawTypeName(temporaryType));
            String mapInit = assignLocalVariableStatement(temporaryTypeName, tempVar, map);
            statements.add(mapInit);

            for (Pair<String, String> element : elements) {
                String putEntry = callMethodStatement(tempVar, "put", element.getElement1(), element.getElement2());
                statements.add(putEntry);
            }

            if (generator.needsAdaptation(effectiveResultType, temporaryType)) {
                tempVar = generator.adapt(tempVar, effectiveResultType, temporaryType);
                statements.add(assignLocalVariableStatement(types.getVariableTypeName(effectiveResultType), local.getName(), tempVar));
            } else if (!equalTypes(effectiveResultType, temporaryType)) {
                statements.add(assignLocalVariableStatement(types.getVariableTypeName(effectiveResultType), local.getName(), tempVar));
            }

            return new Computation(local.getName(), effectiveResultType, true, statements);
        });
    }

    private SerializedValue withResultType(SerializedValue value, Type type) {
        if (value instanceof SerializedReferenceType) {
            ((SerializedReferenceType) value).setResultType(type);
        }
        return value;
    }

}
