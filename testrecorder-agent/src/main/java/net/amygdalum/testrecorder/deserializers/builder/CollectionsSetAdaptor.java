package net.amygdalum.testrecorder.deserializers.builder;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callLocalMethod;
import static net.amygdalum.testrecorder.types.Computation.variable;
import static net.amygdalum.testrecorder.util.TypeFilters.startingWith;
import static net.amygdalum.testrecorder.util.Types.equalTypes;
import static net.amygdalum.testrecorder.util.Types.innerClasses;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.wildcard;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializationException;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.values.SerializedSet;

public class CollectionsSetAdaptor implements SetupGenerator<SerializedSet> {

	private DefaultSetAdaptor adaptor;

	public CollectionsSetAdaptor() {
		this.adaptor = new DefaultSetAdaptor();
	}

	@Override
	public Class<SerializedSet> getAdaptedClass() {
		return SerializedSet.class;
	}

	@Override
	public Class<? extends SetupGenerator<SerializedSet>> parent() {
		return DefaultSetAdaptor.class;
	}

	@Override
	public boolean matches(Type type) {
		return innerClasses(Collections.class).stream()
			.filter(startingWith("Unmodifiable", "Synchronized", "Checked", "Empty", "Singleton"))
			.filter(element -> Set.class.isAssignableFrom(element))
			.anyMatch(element -> equalTypes(element, type));
	}

	@Override
	public Computation tryDeserialize(SerializedSet value, SetupGenerators generator, DeserializerContext context) {
		TypeManager types = context.getTypes();
		types.registerImport(Set.class);
		types.registerType(value.getComponentType());

		String name = types.getRawTypeName(value.getType());
		if (name.contains("Empty")) {
			return tryDeserializeEmpty(value, generator, context);
		} else if (name.contains("Singleton")) {
			return tryDeserializeSingleton(value, generator, context);
		} else if (name.contains("Unmodifiable")) {
			return tryDeserializeUnmodifiable(value, generator, context);
		} else if (name.contains("Synchronized")) {
			return tryDeserializeSynchronized(value, generator, context);
		} else if (name.contains("Checked")) {
			return tryDeserializeChecked(value, generator, context);
		} else {
			throw new DeserializationException("failed deserializing: " + value);
		}
	}

	private Computation createOrdinarySet(SerializedSet value, SetupGenerators generator, DeserializerContext context) {
		SerializedSet baseValue = new SerializedSet(parameterized(LinkedHashSet.class, null, value.getComponentType()));
		baseValue.addAll(value);
		return adaptor.tryDeserialize(baseValue, generator, context);
	}

	private Computation tryDeserializeEmpty(SerializedSet value, SetupGenerators generator, DeserializerContext context) {
        Type componentType = value.getComponentType();
		String factoryMethod = "emptySet";
		TypeManager types = context.getTypes();
		types.staticImport(Collections.class, factoryMethod);

		if (types.isHidden(componentType)) {
		    componentType = wildcard();
		}
        Type resultType = parameterized(Set.class, null, componentType);
		return context.forVariable(value, local -> {

			String decoratingStatement = assignLocalVariableStatement(types.getVariableTypeName(resultType), local.getName(), callLocalMethod(factoryMethod));

			return variable(local.getName(), resultType, asList(decoratingStatement));
		});
	}

	private Computation tryDeserializeSingleton(SerializedSet value, SetupGenerators generator, DeserializerContext context) {
        Type componentType = value.getComponentType();
		String factoryMethod = "singleton";
		TypeManager types = context.getTypes();
		types.registerImport(Set.class);
		types.staticImport(Collections.class, factoryMethod);

        if (types.isHidden(componentType)) {
            componentType = wildcard();
        }
        Type resultType = parameterized(Set.class, null, componentType);
		return context.forVariable(value, local -> {

			Computation computation = value.iterator().next().accept(generator, context);
			List<String> statements = new LinkedList<>(computation.getStatements());
			String resultBase = computation.getValue();

			String decoratingStatement = assignLocalVariableStatement(types.getVariableTypeName(resultType), local.getName(), callLocalMethod(factoryMethod, resultBase));
			statements.add(decoratingStatement);

			return variable(local.getName(), resultType, statements);
		});
	}

	private Computation tryDeserializeUnmodifiable(SerializedSet value, SetupGenerators generator, DeserializerContext context) {
        Type componentType = value.getComponentType();
		String factoryMethod = "unmodifiableSet";

		TypeManager types = context.getTypes();
		types.staticImport(Collections.class, factoryMethod);

        if (types.isHidden(componentType)) {
            componentType = wildcard();
        }
        Type resultType = parameterized(Set.class, null, componentType);
		return context.forVariable(value, local -> {

			Computation computation = createOrdinarySet(value, generator, context);
			List<String> statements = new LinkedList<>(computation.getStatements());
			String resultBase = computation.getValue();

			String decoratingStatement = assignLocalVariableStatement(types.getVariableTypeName(resultType), local.getName(), callLocalMethod(factoryMethod, resultBase));
			statements.add(decoratingStatement);

			return variable(local.getName(), resultType, statements);
		});
	}

	private Computation tryDeserializeSynchronized(SerializedSet value, SetupGenerators generator, DeserializerContext context) {
        Type componentType = value.getComponentType();
		String factoryMethod = "synchronizedSet";
		TypeManager types = context.getTypes();
		types.staticImport(Collections.class, factoryMethod);

        if (types.isHidden(componentType)) {
            componentType = wildcard();
        }
        Type resultType = parameterized(Set.class, null, componentType);
		return context.forVariable(value, local -> {

			Computation computation = createOrdinarySet(value, generator, context);
			List<String> statements = new LinkedList<>(computation.getStatements());
			String resultBase = computation.getValue();

			String decoratingStatement = assignLocalVariableStatement(types.getVariableTypeName(resultType), local.getName(), callLocalMethod(factoryMethod, resultBase));
			statements.add(decoratingStatement);

			return variable(local.getName(), resultType, statements);
		});
	}

	private Computation tryDeserializeChecked(SerializedSet value, SetupGenerators generator, DeserializerContext context) {
        Type componentType = value.getComponentType();
		String factoryMethod = "checkedSet";
		TypeManager types = context.getTypes();
		types.staticImport(Collections.class, factoryMethod);

        if (types.isHidden(componentType)) {
            throw new DeserializationException("cannot deserialize checked set with hidden element type: " + types.getVariableTypeName(componentType));
        }
        Type resultType = parameterized(Set.class, null, componentType);
		return context.forVariable(value, local -> {

			Computation computation = createOrdinarySet(value, generator, context);
			List<String> statements = new LinkedList<>(computation.getStatements());
			String resultBase = computation.getValue();
			String checkedType = types.getRawClass(value.getComponentType());

			String decoratingStatement = assignLocalVariableStatement(types.getVariableTypeName(resultType), local.getName(), callLocalMethod(factoryMethod, resultBase, checkedType));
			statements.add(decoratingStatement);

			return variable(local.getName(), resultType, statements);
		});
	}

}
