package net.amygdalum.testrecorder.deserializers.builder;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.TypeSelector.innerClasses;
import static net.amygdalum.testrecorder.TypeSelector.startingWith;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callLocalMethod;
import static net.amygdalum.testrecorder.util.Types.equalTypes;
import static net.amygdalum.testrecorder.util.Types.parameterized;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.amygdalum.testrecorder.DeserializationException;
import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.values.SerializedMap;

public class CollectionsMapAdaptor implements SetupGenerator<SerializedMap> {

	private DefaultMapAdaptor adaptor;

	@Override
	public Class<SerializedMap> getAdaptedClass() {
		return SerializedMap.class;
	}

	public CollectionsMapAdaptor() {
		this.adaptor = new DefaultMapAdaptor();
	}

	@Override
	public Class<? extends SetupGenerator<SerializedMap>> parent() {
		return DefaultMapAdaptor.class;
	}

	@Override
	public boolean matches(Type type) {
		return innerClasses(Collections.class)
			.filter(startingWith("Unmodifiable", "Synchronized", "Checked", "Empty", "Singleton"))
			.filter(element -> Map.class.isAssignableFrom(element))
			.anyMatch(element -> equalTypes(element, type));
	}

	@Override
	public Computation tryDeserialize(SerializedMap value, SetupGenerators generator) {
		TypeManager types = generator.getTypes();
		types.registerImport(Map.class);

		String name = types.getSimpleName(value.getType());
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
			throw new DeserializationException(value.toString());
		}
	}

	private Computation createOrdinaryMap(SerializedMap value, SetupGenerators generator) {
		SerializedMap baseValue = new SerializedMap(parameterized(LinkedHashMap.class, null, value.getMapKeyType(), value.getMapValueType()));
		baseValue.putAll(value);
		return adaptor.tryDeserialize(baseValue, generator);
	}

	private Computation tryDeserializeEmpty(SerializedMap value, SetupGenerators generator) {
		String factoryMethod = "emptyMap";
		TypeManager types = generator.getTypes();
		types.staticImport(Collections.class, factoryMethod);

		Type resultType = parameterized(Map.class, null, value.getMapKeyType(), value.getMapValueType());
		return generator.forVariable(value, resultType, local -> {

			String decoratingStatement = assignLocalVariableStatement(types.getBestName(resultType), local.getName(), callLocalMethod(factoryMethod));

			return new Computation(local.getName(), value.getResultType(), asList(decoratingStatement));
		});
	}

	private Computation tryDeserializeSingleton(SerializedMap value, SetupGenerators generator) {
		String factoryMethod = "singletonMap";
		TypeManager types = generator.getTypes();
		types.registerImport(Map.class);
		types.staticImport(Collections.class, factoryMethod);

		Type resultType = parameterized(Map.class, null, value.getMapKeyType(), value.getMapValueType());
		return generator.forVariable(value, resultType, local -> {

			Entry<SerializedValue, SerializedValue> entry = value.entrySet().iterator().next();
			List<String> statements = new LinkedList<>();

			Computation keyComputation = entry.getKey().accept(generator);
			statements.addAll(keyComputation.getStatements());
			String resultKey = keyComputation.getValue();

			Computation valueComputation = entry.getValue().accept(generator);
			statements.addAll(valueComputation.getStatements());
			String resultValue = valueComputation.getValue();

			String decoratingStatement = assignLocalVariableStatement(types.getBestName(resultType), local.getName(), callLocalMethod(factoryMethod, resultKey, resultValue));
			statements.add(decoratingStatement);

			return new Computation(local.getName(), value.getResultType(), statements);
		});

	}

	private Computation tryDeserializeUnmodifiable(SerializedMap value, SetupGenerators generator) {
		String factoryMethod = "unmodifiableMap";
		TypeManager types = generator.getTypes();
		types.staticImport(Collections.class, factoryMethod);

		Type resultType = parameterized(Map.class, null, value.getMapKeyType(), value.getMapValueType());
		return generator.forVariable(value, resultType, local -> {

			Computation computation = createOrdinaryMap(value, generator);
			List<String> statements = new LinkedList<>(computation.getStatements());
			String resultBase = computation.getValue();

			String decoratingStatement = assignLocalVariableStatement(types.getBestName(resultType), local.getName(), callLocalMethod(factoryMethod, resultBase));
			statements.add(decoratingStatement);

			return new Computation(local.getName(), value.getResultType(), statements);
		});

	}

	private Computation tryDeserializeSynchronized(SerializedMap value, SetupGenerators generator) {
		String factoryMethod = "synchronizedMap";
		TypeManager types = generator.getTypes();
		types.staticImport(Collections.class, factoryMethod);

		Type resultType = parameterized(Map.class, null, value.getMapKeyType(), value.getMapValueType());
		return generator.forVariable(value, resultType, local -> {

			Computation computation = createOrdinaryMap(value, generator);
			List<String> statements = new LinkedList<>(computation.getStatements());
			String resultBase = computation.getValue();

			String decoratingStatement = assignLocalVariableStatement(types.getBestName(resultType), local.getName(), callLocalMethod(factoryMethod, resultBase));
			statements.add(decoratingStatement);

			return new Computation(local.getName(), value.getResultType(), statements);
		});

	}

	private Computation tryDeserializeChecked(SerializedMap value, SetupGenerators generator) {
		String factoryMethod = "checkedMap";
		TypeManager types = generator.getTypes();
		types.staticImport(Collections.class, factoryMethod);

		Type resultType = parameterized(Map.class, null, value.getMapKeyType(), value.getMapValueType());
		return generator.forVariable(value, resultType, local -> {

			Computation computation = createOrdinaryMap(value, generator);
			List<String> statements = new LinkedList<>(computation.getStatements());
			String resultBase = computation.getValue();
			String checkedKeyType = types.getRawTypeName(value.getMapKeyType());
			String checkedValueType = types.getRawTypeName(value.getMapValueType());

			String decoratingStatement = assignLocalVariableStatement(types.getBestName(resultType), local.getName(), callLocalMethod(factoryMethod, resultBase, checkedKeyType, checkedValueType));
			statements.add(decoratingStatement);

			return new Computation(local.getName(), value.getResultType(), statements);
		});
	}

}
