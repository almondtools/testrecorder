package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethod;
import static net.amygdalum.testrecorder.deserializers.Templates.cast;
import static net.amygdalum.testrecorder.util.Types.isHidden;
import static net.amygdalum.testrecorder.util.Types.wrapHidden;

import java.lang.reflect.Type;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import net.amygdalum.testrecorder.Deserializer;
import net.amygdalum.testrecorder.SerializedImmutableType;
import net.amygdalum.testrecorder.SerializedReferenceType;
import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.SerializedValueType;
import net.amygdalum.testrecorder.deserializers.Adaptors;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DeserializerFactory;
import net.amygdalum.testrecorder.deserializers.LocalVariableNameGenerator;
import net.amygdalum.testrecorder.deserializers.TypeManager;
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

	private Map<SerializedValue, String> computed;

	public ObjectToSetupCode() {
		this(new LocalVariableNameGenerator(), new TypeManager(), DEFAULT);
	}

	public ObjectToSetupCode(LocalVariableNameGenerator locals, TypeManager types) {
		this(locals, types, DEFAULT);
	}

	public ObjectToSetupCode(LocalVariableNameGenerator locals, TypeManager types, Adaptors<ObjectToSetupCode> adaptors) {
		this.adaptors = adaptors;
		this.types = types;
		this.locals = locals;
		this.computed = new IdentityHashMap<>();
	}

	public TypeManager getTypes() {
		return types;
	}

	public String localVariable(SerializedValue value, Type type) {
		String name = locals.fetchName(type);
		computed.put(value, name);
		return name;
	}

	@Override
	public Computation visitField(SerializedField field) {
		Type type = field.getType();
		Type resultType = wrapHidden(type);
		types.registerType(type);
		types.registerType(resultType);

		Computation valueTemplate = field.getValue().accept(this);

		List<String> statements = valueTemplate.getStatements();

		String value = valueTemplate.getValue();
		if (isHidden(field.getValue().getResultType()) && !isHidden(type)) {
			String unwrapped = callMethod(value, "value");
			value = cast(types.getSimpleName(type), unwrapped);
		} 
		String assignField = assignLocalVariableStatement(types.getSimpleName(resultType), field.getName(), value);
		return new Computation(assignField, statements);
	}

	@Override
	public Computation visitReferenceType(SerializedReferenceType value) {
		if (computed.containsKey(value)) {
			return new Computation(computed.get(value), true);
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
