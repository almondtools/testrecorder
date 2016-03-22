package net.amygdalum.testrecorder.visitors.builder;

import static net.amygdalum.testrecorder.visitors.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.visitors.Templates.cast;
import static net.amygdalum.testrecorder.visitors.TypeManager.isHidden;

import java.lang.reflect.Type;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import net.amygdalum.testrecorder.SerializedCollectionVisitor;
import net.amygdalum.testrecorder.SerializedImmutableVisitor;
import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.SerializedValueVisitor;
import net.amygdalum.testrecorder.values.SerializedArray;
import net.amygdalum.testrecorder.values.SerializedBigDecimal;
import net.amygdalum.testrecorder.values.SerializedBigInteger;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedList;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedMap;
import net.amygdalum.testrecorder.values.SerializedNull;
import net.amygdalum.testrecorder.values.SerializedObject;
import net.amygdalum.testrecorder.values.SerializedSet;
import net.amygdalum.testrecorder.visitors.Adaptors;
import net.amygdalum.testrecorder.visitors.Computation;
import net.amygdalum.testrecorder.visitors.LocalVariableNameGenerator;
import net.amygdalum.testrecorder.visitors.SerializedValueVisitorFactory;
import net.amygdalum.testrecorder.visitors.Templates;
import net.amygdalum.testrecorder.visitors.TypeManager;

public class ObjectToSetupCode implements SerializedValueVisitor<Computation>, SerializedCollectionVisitor<Computation>, SerializedImmutableVisitor<Computation> {

	public static final Adaptors<ObjectToSetupCode> DEFAULT = new Adaptors<ObjectToSetupCode>()
		.add(SerializedLiteral.class, new DefaultLiteralAdaptor())
		.add(SerializedNull.class, new DefaultNullAdaptor())
		.add(SerializedBigInteger.class, new DefaultBigIntegerAdaptor())
		.add(SerializedBigDecimal.class, new DefaultBigDecimalAdaptor())
		.add(SerializedObject.class, new BeanObjectAdaptor())
		.add(SerializedObject.class, new DefaultObjectAdaptor())
		.add(SerializedArray.class, new DefaultArrayAdaptor())
		.add(SerializedList.class, new DefaultListAdaptor())
		.add(SerializedSet.class, new DefaultSetAdaptor())
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

	public String localVariable(SerializedValue value, Type type) {
		String name = locals.fetchName(type);
		computed.put(value, name);
		return name;
	}

	@Override
	public Computation visitField(SerializedField field) {
		types.registerType(field.getType());

		Computation valueTemplate = field.getValue().accept(this);

		List<String> statements = valueTemplate.getStatements();

		if (isHidden(field.getValue().getValueType()) && !isHidden(field.getType())) {
			String unwrapped = Templates.callMethod(valueTemplate.getValue(), "value");
			String casted = cast(types.getSimpleName(field.getType()), unwrapped);

			String assignField = assignLocalVariableStatement(types.getSimpleName(field.getType()), field.getName(), casted);
			return new Computation(assignField, statements);
		} else {
			String assignField = assignLocalVariableStatement(types.getSimpleName(field.getType()), field.getName(), valueTemplate.getValue());
			return new Computation(assignField, statements);
		}
	}

	@Override
	public Computation visitObject(SerializedObject value) {
		if (computed.containsKey(value)) {
			return new Computation(computed.get(value), true);
		}
		return adaptors.tryDeserialize(value, types, this);
	}

	@Override
	public Computation visitList(SerializedList value) {
		if (computed.containsKey(value)) {
			return new Computation(computed.get(value), true);
		}
		return adaptors.tryDeserialize(value, types, this);
	}

	@Override
	public Computation visitSet(SerializedSet value) {
		if (computed.containsKey(value)) {
			return new Computation(computed.get(value), true);
		}
		return adaptors.tryDeserialize(value, types, this);
	}

	@Override
	public Computation visitMap(SerializedMap value) {
		if (computed.containsKey(value)) {
			return new Computation(computed.get(value), true);
		}
		return adaptors.tryDeserialize(value, types, this);
	}

	@Override
	public Computation visitArray(SerializedArray value) {
		if (computed.containsKey(value)) {
			return new Computation(computed.get(value), true);
		}
		return adaptors.tryDeserialize(value, types, this);
	}

	@Override
	public Computation visitLiteral(SerializedLiteral value) {
		return adaptors.tryDeserialize(value, types, this);
	}

	@Override
	public Computation visitBigDecimal(SerializedBigDecimal value) {
		return adaptors.tryDeserialize(value, types, this);
	}

	@Override
	public Computation visitBigInteger(SerializedBigInteger value) {
		return adaptors.tryDeserialize(value, types, this);
	}

	@Override
	public Computation visitNull(SerializedNull value) {
		return adaptors.tryDeserialize(value, types, this);
	}

	@Override
	public Computation visitUnknown(SerializedValue value) {
		return Computation.NULL;
	}

	public static class Factory implements SerializedValueVisitorFactory {

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
