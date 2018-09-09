package net.amygdalum.testrecorder.deserializers.builder;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethodStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.newObject;
import static net.amygdalum.testrecorder.types.Computation.variable;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.equalGenericTypes;
import static net.amygdalum.testrecorder.util.Types.typeArgument;
import static net.amygdalum.testrecorder.util.Types.typeArguments;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.deserializers.Deserializer;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.util.Types;

public abstract class DefaultGenericCollectionAdaptor<T extends SerializedReferenceType> extends DefaultSetupGenerator<T> implements SetupGenerator<T> {

	public abstract Class<?>[] matchingTypes();

	public abstract Type componentType(T value);

	public abstract Stream<SerializedValue> elements(T value);

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
	public Computation tryDeserialize(T value, Deserializer generator) {
		DeserializerContext context = generator.getContext();
		TypeManager types = context.getTypes();

		Type type = value.getType();
		Type usedType = types.mostSpecialOf(value.getUsedTypes()).orElse(Object.class);
		boolean uniqueUsageType = value.getUsedTypes().length == 1 && Collection.class.isAssignableFrom(baseType(usedType));
		Type componentType = componentType(value);

		Class<?> matchingType = matchType(type).get();

		Type effectiveResultType = types.bestType(usedType, matchingType);
		
		Type temporaryType = uniqueUsageType ? effectiveResultType : types.bestType(type, effectiveResultType, matchingType);
		Type componentResultType = types.isHidden(componentType) ? typeArgument(temporaryType, 0).orElse(Object.class) : componentType;

		types.registerTypes(effectiveResultType, type, componentResultType);

		return context.forVariable(value, effectiveResultType, local -> {

			List<Computation> elementTemplates = elements(value)
				.map(element -> element.accept(generator))
				.filter(element -> element != null)
				.collect(toList());

			List<String> elements = elementTemplates.stream()
				.map(template -> context.adapt(template.getValue(), componentResultType, template.getType()))
				.collect(toList());

			List<String> statements = elementTemplates.stream()
				.flatMap(template -> template.getStatements().stream())
				.collect(toList());

			String tempVar = local.getName();
			if (!equalGenericTypes(effectiveResultType, temporaryType)) {
				tempVar = context.temporaryLocal();
			}

			String set = types.isHidden(type)
				? context.adapt(types.getWrappedName(type), temporaryType, types.wrapHidden(type))
				: newObject(types.getConstructorTypeName(type));
			String temporaryTypeName = Optional.of(temporaryType)
				.filter(t -> typeArguments(t).count() > 0)
				.filter(t -> typeArguments(t).allMatch(Types::isBound))
				.map(t -> types.getVariableTypeName(t))
				.orElse(types.getRawTypeName(temporaryType));
			String setInit = assignLocalVariableStatement(temporaryTypeName, tempVar, set);
			statements.add(setInit);

			for (String element : elements) {
				String addElement = callMethodStatement(tempVar, "add", element);
				statements.add(addElement);
			}

			if (local.isDefined() && !local.isReady()) {
				statements.add(callMethodStatement(local.getName(), "addAll", tempVar));
			} else if (context.needsAdaptation(effectiveResultType, temporaryType)) {
				tempVar = context.adapt(tempVar, effectiveResultType, temporaryType);
				statements.add(assignLocalVariableStatement(types.getVariableTypeName(effectiveResultType), local.getName(), tempVar));
			} else if (!equalGenericTypes(effectiveResultType, temporaryType)) {
				statements.add(assignLocalVariableStatement(types.getVariableTypeName(effectiveResultType), local.getName(), tempVar));
			}
			return variable(local.getName(), local.getType(), statements);
		});
	}

}
