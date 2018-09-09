package net.amygdalum.testrecorder.deserializers.builder;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callLocalMethod;
import static net.amygdalum.testrecorder.types.Computation.variable;
import static net.amygdalum.testrecorder.util.TypeFilters.startingWith;
import static net.amygdalum.testrecorder.util.Types.equalBaseTypes;
import static net.amygdalum.testrecorder.util.Types.innerClasses;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.wildcard;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.amygdalum.testrecorder.deserializers.Deserializer;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializationException;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.values.SerializedList;

public class CollectionsListAdaptor implements SetupGenerator<SerializedList> {

	private DefaultListAdaptor adaptor;

	public CollectionsListAdaptor() {
		this.adaptor = new DefaultListAdaptor();
	}

	@Override
	public Class<SerializedList> getAdaptedClass() {
		return SerializedList.class;
	}

	@Override
	public Class<? extends SetupGenerator<SerializedList>> parent() {
		return DefaultListAdaptor.class;
	}

	@Override
	public boolean matches(Type type) {
		return innerClasses(Collections.class).stream()
			.filter(startingWith("Unmodifiable", "Synchronized", "Checked", "Empty", "Singleton"))
			.filter(element -> List.class.isAssignableFrom(element))
			.anyMatch(element -> equalBaseTypes(element, type));
	}

	@Override
	public Computation tryDeserialize(SerializedList value, Deserializer generator) {
		DeserializerContext context = generator.getContext();
		TypeManager types = context.getTypes();
		types.registerImport(List.class);
		types.registerType(value.getComponentType());

		String name = types.getRawTypeName(value.getType());
		if (name.contains("Empty")) {
			return tryDeserializeEmpty(value, generator);
		} else if (name.contains("Singleton")) {
			return tryDeserializeSingleton(value, generator);
		} else if (name.contains("Unmodifiable")) {
			return tryDeserializeUnmodifiable(value, generator);
		} else if (name.contains("Synchronized")) {
			return tryDeserializeSynchronized(value, generator);
		} else if (name.contains("Checked")) {
			return tryDeserializeChecked(value, generator);
		} else {
			throw new DeserializationException("failed deserializing: " + value);
		}
	}

	private Computation createOrdinaryList(SerializedList value, Deserializer generator, DeserializerContext context) {
		SerializedList baseValue = new SerializedList(ArrayList.class);
		baseValue.useAs(parameterized(ArrayList.class, null, value.getComponentType()));
		baseValue.addAll(value);
		return adaptor.tryDeserialize(baseValue, generator);
	}

	private Computation tryDeserializeEmpty(SerializedList value, Deserializer generator) {
		DeserializerContext context = generator.getContext();
		Type componentType = value.getComponentType();
		String factoryMethod = "emptyList";
		TypeManager types = context.getTypes();
		types.staticImport(Collections.class, factoryMethod);

		if (types.isHidden(componentType)) {
			componentType = wildcard();
		}
		Type resultType = parameterized(List.class, null, componentType);
		return context.forVariable(value, resultType, local -> {

			String decoratingStatement = assignLocalVariableStatement(types.getVariableTypeName(resultType), local.getName(), callLocalMethod(factoryMethod));

			return variable(local.getName(), local.getType(), asList(decoratingStatement));
		});
	}

	private Computation tryDeserializeSingleton(SerializedList value, Deserializer generator) {
		DeserializerContext context = generator.getContext();
		Type componentType = value.getComponentType();
		String factoryMethod = "singletonList";
		TypeManager types = context.getTypes();
		types.registerImport(List.class);
		types.staticImport(Collections.class, factoryMethod);

		if (types.isHidden(componentType)) {
			componentType = wildcard();
		}
		Type resultType = parameterized(List.class, null, componentType);
		return context.forVariable(value, resultType, local -> {

			Computation computation = value.get(0).accept(generator);
			List<String> statements = new LinkedList<>(computation.getStatements());
			String resultBase = computation.getValue();

			String decoratingStatement = assignLocalVariableStatement(types.getVariableTypeName(resultType), local.getName(), callLocalMethod(factoryMethod, resultBase));
			statements.add(decoratingStatement);

			return variable(local.getName(), local.getType(), statements);
		});
	}

	private Computation tryDeserializeUnmodifiable(SerializedList value, Deserializer generator) {
		DeserializerContext context = generator.getContext();
		Type componentType = value.getComponentType();
		String factoryMethod = "unmodifiableList";
		TypeManager types = context.getTypes();
		types.staticImport(Collections.class, factoryMethod);

		if (types.isHidden(componentType)) {
			componentType = wildcard();
		}
		Type resultType = parameterized(List.class, null, componentType);
		return context.forVariable(value, resultType, local -> {

			Computation computation = createOrdinaryList(value, generator, context);
			List<String> statements = new LinkedList<>(computation.getStatements());
			String resultBase = computation.getValue();

			String decoratingStatement = assignLocalVariableStatement(types.getVariableTypeName(resultType), local.getName(), callLocalMethod(factoryMethod, resultBase));
			statements.add(decoratingStatement);

			return variable(local.getName(), local.getType(), statements);
		});
	}

	private Computation tryDeserializeSynchronized(SerializedList value, Deserializer generator) {
		DeserializerContext context = generator.getContext();
		Type componentType = value.getComponentType();
		String factoryMethod = "synchronizedList";
		TypeManager types = context.getTypes();
		types.staticImport(Collections.class, factoryMethod);

		if (types.isHidden(componentType)) {
			componentType = wildcard();
		}
		Type resultType = parameterized(List.class, null, componentType);
		return context.forVariable(value, resultType, local -> {

			Computation computation = createOrdinaryList(value, generator, context);
			List<String> statements = new LinkedList<>(computation.getStatements());
			String resultBase = computation.getValue();

			String decoratingStatement = assignLocalVariableStatement(types.getVariableTypeName(resultType), local.getName(), callLocalMethod(factoryMethod, resultBase));
			statements.add(decoratingStatement);

			return variable(local.getName(), local.getType(), statements);
		});

	}

	private Computation tryDeserializeChecked(SerializedList value, Deserializer generator) {
		DeserializerContext context = generator.getContext();
		Type componentType = value.getComponentType();
		String factoryMethod = "checkedList";
		TypeManager types = context.getTypes();
		types.staticImport(Collections.class, factoryMethod);

		if (types.isHidden(componentType)) {
			throw new DeserializationException("cannot deserialize checked list with hidden element type: " + types.getVariableTypeName(componentType));
		}
		Type resultType = parameterized(List.class, null, componentType);
		return context.forVariable(value, resultType, local -> {

			Computation computation = createOrdinaryList(value, generator, context);
			List<String> statements = new LinkedList<>(computation.getStatements());
			String resultBase = computation.getValue();
			String checkedType = types.getRawClass(componentType);

			String decoratingStatement = assignLocalVariableStatement(types.getVariableTypeName(resultType), local.getName(), callLocalMethod(factoryMethod, resultBase, checkedType));
			statements.add(decoratingStatement);

			return variable(local.getName(), local.getType(), statements);
		});
	}

}
