package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethod;
import static net.amygdalum.testrecorder.deserializers.Templates.cast;
import static net.amygdalum.testrecorder.util.Types.boxingEquivalentTypes;
import static net.amygdalum.testrecorder.util.Types.assignableTypes;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import net.amygdalum.testrecorder.DeserializationException;
import net.amygdalum.testrecorder.Deserializer;
import net.amygdalum.testrecorder.SerializedImmutableType;
import net.amygdalum.testrecorder.SerializedReferenceType;
import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.SerializedValueType;
import net.amygdalum.testrecorder.deserializers.Adaptors;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DeserializerFactory;
import net.amygdalum.testrecorder.deserializers.LocalVariable;
import net.amygdalum.testrecorder.deserializers.LocalVariableNameGenerator;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.deserializers.LocalVariableDefinition;
import net.amygdalum.testrecorder.util.GenericObject;
import net.amygdalum.testrecorder.values.SerializedArray;
import net.amygdalum.testrecorder.values.SerializedEnum;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedImmutable;
import net.amygdalum.testrecorder.values.SerializedList;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedMap;
import net.amygdalum.testrecorder.values.SerializedNull;
import net.amygdalum.testrecorder.values.SerializedObject;
import net.amygdalum.testrecorder.values.SerializedSet;

public class ObjectToSetupCode implements Deserializer<Computation> {

	public static final Adaptors<ObjectToSetupCode> DEFAULT = new Adaptors<ObjectToSetupCode>()
		.add(SerializedLiteral.class, new DefaultLiteralAdaptor())
		.add(SerializedNull.class, new DefaultNullAdaptor())
		.add(SerializedImmutable.class, new DefaultClassAdaptor())
		.add(SerializedImmutable.class, new DefaultBigIntegerAdaptor())
		.add(SerializedImmutable.class, new DefaultBigDecimalAdaptor())
		.add(SerializedEnum.class, new DefaultEnumAdaptor())
		.add(SerializedObject.class, new BeanObjectAdaptor())
		.add(SerializedObject.class, new DefaultObjectAdaptor())
		.add(SerializedArray.class, new DefaultArrayAdaptor())
		.add(SerializedList.class, new ArraysListAdaptor())
		.add(SerializedList.class, new CollectionsListAdaptor())
		.add(SerializedList.class, new DefaultListAdaptor())
		.add(SerializedSet.class, new CollectionsSetAdaptor())
		.add(SerializedSet.class, new DefaultSetAdaptor())
		.add(SerializedMap.class, new CollectionsMapAdaptor())
		.add(SerializedMap.class, new DefaultMapAdaptor());

	private TypeManager types;
	private Adaptors<ObjectToSetupCode> adaptors;
	private LocalVariableNameGenerator locals;

	private Map<SerializedValue, LocalVariable> defined;

	public ObjectToSetupCode(Class<?> clazz) {
		this(new LocalVariableNameGenerator(), new TypeManager(clazz.getPackage().getName()), DEFAULT);
	}

	public ObjectToSetupCode(LocalVariableNameGenerator locals, TypeManager types) {
		this(locals, types, DEFAULT);
	}

	public ObjectToSetupCode(LocalVariableNameGenerator locals, TypeManager types, Adaptors<ObjectToSetupCode> adaptors) {
		this.adaptors = adaptors;
		this.types = types;
		this.locals = locals;
		this.defined = new IdentityHashMap<>();
	}

	public String adapt(String value, Type resultType, Type type) {
		if (types.isHidden(type) && !types.isHidden(resultType)) {
			return cast(types.getSimpleName(resultType), callMethod(value, "value"));
		} else if (!assignableTypes(resultType, type) && !boxingEquivalentTypes(resultType, type)) {
			return cast(types.getSimpleName(resultType), value);
		} else {
			return value;
		}
	}

	public TypeManager getTypes() {
		return types;
	}

	public Computation forVariable(SerializedValue value, Type type, LocalVariableDefinition computation) {
		LocalVariable local = localVariable(value, type);
		try {
			Computation definition = computation.define(local);
			finishVariable(value);
			return definition;
		} catch (DeserializationException e) {
			resetVariable(value);
			throw e;
		}
	}

	public String temporaryLocal() {
		return locals.fetchName("temp");
	}

	private LocalVariable localVariable(SerializedValue value, Type type) {
		String name = locals.fetchName(type);
		LocalVariable definition = new LocalVariable(name);
		defined.put(value, definition);
		return definition;
	}


	private void finishVariable(SerializedValue value) {
		defined.computeIfPresent(value, (val, def) -> def.finish());
	}

	private void resetVariable(SerializedValue value) {
		defined.remove(value);
	}

	@Override
	public Computation visitField(SerializedField field) {
		Type fieldType = field.getType();
		Type resultType = field.getValue().getResultType();
		Type fieldResultType = types.wrapHidden(fieldType);
		types.registerTypes(fieldType, resultType, fieldResultType);

		Computation valueTemplate = field.getValue().accept(this);

		List<String> statements = valueTemplate.getStatements();

		String value = adapt(valueTemplate.getValue(), fieldType, resultType);

		String assignField = assignLocalVariableStatement(types.getSimpleName(fieldResultType), field.getName(), value);
		return new Computation(assignField, null, statements);
	}

	@Override
	public Computation visitReferenceType(SerializedReferenceType value) {
		if (defined.containsKey(value)) {
			LocalVariable definition = defined.get(value);
			String name = definition.getName();
			if (definition.isDefined()) {
				return new Computation(name, definition.getType(), true);
			} else {
				List<String> statements = new ArrayList<>();
				String forwardExpression = callMethod(types.getBestName(GenericObject.class), "forward", types.getRawTypeName(value.getType()));
				statements.add(assignLocalVariableStatement(types.getBestName(types.wrapHidden(value.getType())), name, forwardExpression));
				definition.define(value.getResultType());
				return new Computation(name, definition.getType(), true, statements);
			}
		}
		return adaptors.tryDeserialize(value, types, this);
	}

	@Override
	public Computation visitValueType(SerializedValueType value) {
		return adaptors.tryDeserialize(value, types, this);
	}

	@Override
	public Computation visitImmutableType(SerializedImmutableType value) {
		return adaptors.tryDeserialize(value, types, this);
	}

	public static class Factory implements DeserializerFactory {

		@Override
		public ObjectToSetupCode create(LocalVariableNameGenerator locals, TypeManager types) {
			return new ObjectToSetupCode(locals, types);
		}

		@Override
		public Type resultType(Type type) {
			return type;
		}
	}

}
