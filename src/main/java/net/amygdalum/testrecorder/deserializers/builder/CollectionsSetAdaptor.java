package net.amygdalum.testrecorder.deserializers.builder;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.TypeFilters.startingWith;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callLocalMethod;
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

import net.amygdalum.testrecorder.DeserializationException;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DeserializerContext;
import net.amygdalum.testrecorder.deserializers.TypeManager;
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
		TypeManager types = generator.getTypes();
		types.registerImport(Set.class);
		types.registerType(value.getComponentType());

		String name = types.getRawName(value.getType());
		if (name.contains("Empty")) {
			return tryDeserializeEmpty(value, generator);
		} else if (name.contains("Singleton")) {
			return tryDeserializeSingleton(value, generator);
		} else if (name.contains("Unmodifiable")) {
			return tryDeserializeUnmodifiable(value, generator, context);
		} else if (name.contains("Synchronized")) {
			return tryDeserializeSynchronized(value, generator, context);
		} else if (name.contains("Checked")) {
			return tryDeserializeChecked(value, generator, context);
		} else {
			throw new DeserializationException(value.toString());
		}
	}

	private Computation createOrdinarySet(SerializedSet value, SetupGenerators generator, DeserializerContext context) {
		SerializedSet baseValue = new SerializedSet(parameterized(LinkedHashSet.class, null, value.getComponentType()));
		baseValue.addAll(value);
		return adaptor.tryDeserialize(baseValue, generator, context);
	}

	private Computation tryDeserializeEmpty(SerializedSet value, SetupGenerators generator) {
        Type componentType = value.getComponentType();
		String factoryMethod = "emptySet";
		TypeManager types = generator.getTypes();
		types.staticImport(Collections.class, factoryMethod);

		if (types.isHidden(componentType)) {
		    componentType = wildcard();
		}
        Type resultType = parameterized(Set.class, null, componentType);
		return generator.forVariable(value, resultType, local -> {

			String decoratingStatement = assignLocalVariableStatement(types.getBestName(resultType), local.getName(), callLocalMethod(factoryMethod));

			return new Computation(local.getName(), resultType, asList(decoratingStatement));
		});
	}

	private Computation tryDeserializeSingleton(SerializedSet value, SetupGenerators generator) {
        Type componentType = value.getComponentType();
		String factoryMethod = "singleton";
		TypeManager types = generator.getTypes();
		types.registerImport(Set.class);
		types.staticImport(Collections.class, factoryMethod);

        if (types.isHidden(componentType)) {
            componentType = wildcard();
        }
        Type resultType = parameterized(Set.class, null, componentType);
		return generator.forVariable(value, resultType, local -> {

			Computation computation = value.iterator().next().accept(generator);
			List<String> statements = new LinkedList<>(computation.getStatements());
			String resultBase = computation.getValue();

			String decoratingStatement = assignLocalVariableStatement(types.getBestName(resultType), local.getName(), callLocalMethod(factoryMethod, resultBase));
			statements.add(decoratingStatement);

			return new Computation(local.getName(), resultType, statements);
		});
	}

	private Computation tryDeserializeUnmodifiable(SerializedSet value, SetupGenerators generator, DeserializerContext context) {
        Type componentType = value.getComponentType();
		String factoryMethod = "unmodifiableSet";

		TypeManager types = generator.getTypes();
		types.staticImport(Collections.class, factoryMethod);

        if (types.isHidden(componentType)) {
            componentType = wildcard();
        }
        Type resultType = parameterized(Set.class, null, componentType);
		return generator.forVariable(value, resultType, local -> {

			Computation computation = createOrdinarySet(value, generator, context);
			List<String> statements = new LinkedList<>(computation.getStatements());
			String resultBase = computation.getValue();

			String decoratingStatement = assignLocalVariableStatement(types.getBestName(resultType), local.getName(), callLocalMethod(factoryMethod, resultBase));
			statements.add(decoratingStatement);

			return new Computation(local.getName(), resultType, statements);
		});
	}

	private Computation tryDeserializeSynchronized(SerializedSet value, SetupGenerators generator, DeserializerContext context) {
        Type componentType = value.getComponentType();
		String factoryMethod = "synchronizedSet";
		TypeManager types = generator.getTypes();
		types.staticImport(Collections.class, factoryMethod);

        if (types.isHidden(componentType)) {
            componentType = wildcard();
        }
        Type resultType = parameterized(Set.class, null, componentType);
		return generator.forVariable(value, resultType, local -> {

			Computation computation = createOrdinarySet(value, generator, context);
			List<String> statements = new LinkedList<>(computation.getStatements());
			String resultBase = computation.getValue();

			String decoratingStatement = assignLocalVariableStatement(types.getBestName(resultType), local.getName(), callLocalMethod(factoryMethod, resultBase));
			statements.add(decoratingStatement);

			return new Computation(local.getName(), resultType, statements);
		});
	}

	private Computation tryDeserializeChecked(SerializedSet value, SetupGenerators generator, DeserializerContext context) {
        Type componentType = value.getComponentType();
		String factoryMethod = "checkedSet";
		TypeManager types = generator.getTypes();
		types.staticImport(Collections.class, factoryMethod);

        if (types.isHidden(componentType)) {
            throw new DeserializationException("cannot deserialize checked set with hidden element type: " + types.getBestName(componentType));
        }
        Type resultType = parameterized(Set.class, null, componentType);
		return generator.forVariable(value, resultType, local -> {

			Computation computation = createOrdinarySet(value, generator, context);
			List<String> statements = new LinkedList<>(computation.getStatements());
			String resultBase = computation.getValue();
			String checkedType = types.getRawTypeName(value.getComponentType());

			String decoratingStatement = assignLocalVariableStatement(types.getBestName(resultType), local.getName(), callLocalMethod(factoryMethod, resultBase, checkedType));
			statements.add(decoratingStatement);

			return new Computation(local.getName(), resultType, statements);
		});
	}

}
