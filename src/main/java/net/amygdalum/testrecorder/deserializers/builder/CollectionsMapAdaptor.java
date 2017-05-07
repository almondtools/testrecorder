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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.amygdalum.testrecorder.DeserializationException;
import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DeserializerContext;
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
		return innerClasses(Collections.class).stream()
			.filter(startingWith("Unmodifiable", "Synchronized", "Checked", "Empty", "Singleton"))
			.filter(element -> Map.class.isAssignableFrom(element))
			.anyMatch(element -> equalTypes(element, type));
	}

	@Override
	public Computation tryDeserialize(SerializedMap value, SetupGenerators generator, DeserializerContext context) {
		TypeManager types = generator.getTypes();
		types.registerImport(Map.class);
        types.registerTypes(value.getMapKeyType(), value.getMapValueType());

		String name = types.getRawTypeName(value.getType());
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

	private Computation createOrdinaryMap(SerializedMap value, SetupGenerators generator, DeserializerContext context) {
		SerializedMap baseValue = new SerializedMap(parameterized(LinkedHashMap.class, null, value.getMapKeyType(), value.getMapValueType()));
		baseValue.putAll(value);
		return adaptor.tryDeserialize(baseValue, generator, context);
	}

	private Computation tryDeserializeEmpty(SerializedMap value, SetupGenerators generator) {
        Type mapKeyType = value.getMapKeyType();
        Type mapValueType = value.getMapValueType();

        String factoryMethod = "emptyMap";
		TypeManager types = generator.getTypes();
		types.staticImport(Collections.class, factoryMethod);
		
		if (types.isHidden(mapKeyType)) {
		    mapKeyType = wildcard();
		}
		if (types.isHidden(mapValueType)) {
		    mapValueType = wildcard();
		}
        Type resultType = parameterized(Map.class, null, mapKeyType, mapValueType);
		return generator.forVariable(value, resultType, local -> {

			String decoratingStatement = assignLocalVariableStatement(types.getVariableTypeName(resultType), local.getName(), callLocalMethod(factoryMethod));

			return new Computation(local.getName(), resultType, asList(decoratingStatement));
		});
	}

	private Computation tryDeserializeSingleton(SerializedMap value, SetupGenerators generator) {
        Type mapKeyType = value.getMapKeyType();
        Type mapValueType = value.getMapValueType();
		String factoryMethod = "singletonMap";
		TypeManager types = generator.getTypes();
		types.registerImport(Map.class);
		types.staticImport(Collections.class, factoryMethod);

        if (types.isHidden(mapKeyType)) {
            mapKeyType = wildcard();
        }
        if (types.isHidden(mapValueType)) {
            mapValueType = wildcard();
        }
        Type resultType = parameterized(Map.class, null, mapKeyType, mapValueType);
		return generator.forVariable(value, resultType, local -> {

			Entry<SerializedValue, SerializedValue> entry = value.entrySet().iterator().next();
			List<String> statements = new LinkedList<>();

			Computation keyComputation = entry.getKey().accept(generator);
			statements.addAll(keyComputation.getStatements());
			String resultKey = keyComputation.getValue();

			Computation valueComputation = entry.getValue().accept(generator);
			statements.addAll(valueComputation.getStatements());
			String resultValue = valueComputation.getValue();

			String decoratingStatement = assignLocalVariableStatement(types.getVariableTypeName(resultType), local.getName(), callLocalMethod(factoryMethod, resultKey, resultValue));
			statements.add(decoratingStatement);

			return new Computation(local.getName(), resultType, statements);
		});

	}

	private Computation tryDeserializeUnmodifiable(SerializedMap value, SetupGenerators generator, DeserializerContext context) {
        Type mapKeyType = value.getMapKeyType();
        Type mapValueType = value.getMapValueType();
		String factoryMethod = "unmodifiableMap";
		TypeManager types = generator.getTypes();
		types.staticImport(Collections.class, factoryMethod);

        if (types.isHidden(mapKeyType)) {
            mapKeyType = wildcard();
        }
        if (types.isHidden(mapValueType)) {
            mapValueType = wildcard();
        }
		Type resultType = parameterized(Map.class, null, mapKeyType, mapValueType);
		return generator.forVariable(value, resultType, local -> {

			Computation computation = createOrdinaryMap(value, generator, context);
			List<String> statements = new LinkedList<>(computation.getStatements());
			String resultBase = computation.getValue();

			String decoratingStatement = assignLocalVariableStatement(types.getVariableTypeName(resultType), local.getName(), callLocalMethod(factoryMethod, resultBase));
			statements.add(decoratingStatement);

			return new Computation(local.getName(), resultType, statements);
		});

	}

	private Computation tryDeserializeSynchronized(SerializedMap value, SetupGenerators generator, DeserializerContext context) {
        Type mapKeyType = value.getMapKeyType();
        Type mapValueType = value.getMapValueType();
		String factoryMethod = "synchronizedMap";
		TypeManager types = generator.getTypes();
		types.staticImport(Collections.class, factoryMethod);

        if (types.isHidden(mapKeyType)) {
            mapKeyType = wildcard();
        }
        if (types.isHidden(mapValueType)) {
            mapValueType = wildcard();
        }
		Type resultType = parameterized(Map.class, null, mapKeyType, mapValueType);
		return generator.forVariable(value, resultType, local -> {

			Computation computation = createOrdinaryMap(value, generator, context);
			List<String> statements = new LinkedList<>(computation.getStatements());
			String resultBase = computation.getValue();

			String decoratingStatement = assignLocalVariableStatement(types.getVariableTypeName(resultType), local.getName(), callLocalMethod(factoryMethod, resultBase));
			statements.add(decoratingStatement);

			return new Computation(local.getName(), resultType, statements);
		});

	}

	private Computation tryDeserializeChecked(SerializedMap value, SetupGenerators generator, DeserializerContext context) {
        Type mapKeyType = value.getMapKeyType();
        Type mapValueType = value.getMapValueType();
		String factoryMethod = "checkedMap";
		TypeManager types = generator.getTypes();
		types.staticImport(Collections.class, factoryMethod);

        if (types.isHidden(mapKeyType)) {
            throw new DeserializationException("cannot deserialize checked map with hidden key type: " + types.getVariableTypeName(mapKeyType));
        }
        if (types.isHidden(mapValueType)) {
            throw new DeserializationException("cannot deserialize checked map with hidden value type: " + types.getVariableTypeName(mapValueType));
        }
		Type resultType = parameterized(Map.class, null, mapKeyType, mapValueType);
		return generator.forVariable(value, resultType, local -> {

			Computation computation = createOrdinaryMap(value, generator, context);
			List<String> statements = new LinkedList<>(computation.getStatements());
			String resultBase = computation.getValue();
			String checkedKeyType = types.getRawClass(mapKeyType);
			String checkedValueType = types.getRawClass(mapValueType);

			String decoratingStatement = assignLocalVariableStatement(types.getVariableTypeName(resultType), local.getName(), callLocalMethod(factoryMethod, resultBase, checkedKeyType, checkedValueType));
			statements.add(decoratingStatement);

			return new Computation(local.getName(), resultType, statements);
		});
	}

}
