package net.amygdalum.testrecorder.visitors.matcher;

import static net.amygdalum.testrecorder.visitors.Templates.asLiteral;
import static net.amygdalum.testrecorder.visitors.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.visitors.Templates.recursiveMatcher;
import static net.amygdalum.testrecorder.visitors.TypeManager.getBase;
import static net.amygdalum.testrecorder.visitors.TypeManager.parameterized;
import static net.amygdalum.testrecorder.visitors.TypeManager.wildcard;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import org.hamcrest.Matcher;

import net.amygdalum.testrecorder.SerializedCollectionVisitor;
import net.amygdalum.testrecorder.SerializedImmutableVisitor;
import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.SerializedValueVisitor;
import net.amygdalum.testrecorder.util.GenericMatcher;
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
import net.amygdalum.testrecorder.visitors.TypeManager;

public class ObjectToMatcherCode implements SerializedValueVisitor<Computation>, SerializedCollectionVisitor<Computation>, SerializedImmutableVisitor<Computation> {

	public static final Adaptors<ObjectToMatcherCode> DEFAULT = new Adaptors<ObjectToMatcherCode>()
		.add(SerializedLiteral.class, new DefaultLiteralAdaptor())
		.add(SerializedNull.class, new DefaultNullAdaptor())
		.add(SerializedBigInteger.class, new DefaultBigIntegerAdaptor())
		.add(SerializedBigDecimal.class, new DefaultBigDecimalAdaptor())
		.add(SerializedObject.class, new DefaultObjectAdaptor())
		.add(SerializedArray.class, new DefaultArrayAdaptor())
		.add(SerializedList.class, new DefaultListAdaptor())
		.add(SerializedSet.class, new DefaultSetAdaptor())
		.add(SerializedMap.class, new DefaultMapAdaptor());

	private TypeManager types;
	private Adaptors<ObjectToMatcherCode> adaptors;

	private Set<SerializedValue> computed;

	public ObjectToMatcherCode() {
		this(new LocalVariableNameGenerator(), new TypeManager(), DEFAULT);
	}

	public ObjectToMatcherCode(LocalVariableNameGenerator locals, TypeManager types) {
		this(locals, types, DEFAULT);
	}

	public ObjectToMatcherCode(LocalVariableNameGenerator locals, TypeManager types, Adaptors<ObjectToMatcherCode> adaptors) {
		this.types = types;
		this.adaptors = adaptors;
		this.computed = new HashSet<>();
	}

	public boolean isSimpleValue(SerializedValue element) {
		return element instanceof SerializedNull
			|| element instanceof SerializedLiteral;
	}

	public Computation simpleValue(SerializedValue element) {
		if (element instanceof SerializedNull) {
			return new Computation("null");
		} else if (element instanceof SerializedLiteral) {
			return new Computation(asLiteral(((SerializedLiteral) element).getValue()));
		} else {
			return element.accept(this);
		}
	}

	@Override
	public Computation visitField(SerializedField field) {
		SerializedValue fieldValue = field.getValue();
		if (isSimpleValue(fieldValue)) {
			types.registerImport(getBase(field.getType()));
			Computation value = simpleValue(fieldValue);

			String assignField = assignLocalVariableStatement(types.getRawName(field.getType()), field.getName(), value.getValue());
			return new Computation(assignField, value.getStatements());
		} else {
			types.registerImport(Matcher.class);
			Computation value = fieldValue.accept(this);

			String genericType = types.getSimpleName(value.getType());

			String assignField = assignLocalVariableStatement(genericType, field.getName(), value.getValue());
			return new Computation(assignField, value.getStatements());
		}
	}

	@Override
	public Computation visitObject(SerializedObject value) {
		if (!computed.add(value)) {
			types.staticImport(GenericMatcher.class, "recursive");
			Type resultType = value.getType().equals(value.getValueType()) ? parameterized(Matcher.class, null, value.getType()) : parameterized(Matcher.class, null, wildcard());
			return new Computation(recursiveMatcher(types.getRawTypeName(value.getValueType())), resultType);
		}
		return adaptors.tryDeserialize(value, types, this);
	}

	@Override
	public Computation visitList(SerializedList value) {
		if (!computed.add(value)) {
			types.staticImport(GenericMatcher.class, "recursive");
			types.registerImport(value.getValueType());
			return new Computation(recursiveMatcher(types.getRawTypeName(value.getValueType())), parameterized(Matcher.class, null, wildcard()));
		}
		return adaptors.tryDeserialize(value, types, this);
	}

	@Override
	public Computation visitSet(SerializedSet value) {
		if (!computed.add(value)) {
			types.staticImport(GenericMatcher.class, "recursive");
			types.registerImport(value.getValueType());
			return new Computation(recursiveMatcher(types.getRawTypeName(value.getValueType())), parameterized(Matcher.class, null, wildcard()));
		}
		return adaptors.tryDeserialize(value, types, this);
	}

	@Override
	public Computation visitMap(SerializedMap value) {
		if (!computed.add(value)) {
			types.staticImport(GenericMatcher.class, "recursive");
			types.registerImport(value.getValueType());
			return new Computation(recursiveMatcher(types.getRawTypeName(value.getValueType())), parameterized(Matcher.class, null, wildcard()));
		}
		return adaptors.tryDeserialize(value, types, this);
	}

	@Override
	public Computation visitArray(SerializedArray value) {
		if (!computed.add(value)) {
			types.staticImport(GenericMatcher.class, "recursive");
			types.registerImport(value.getValueType());
			return new Computation(recursiveMatcher(types.getRawTypeName(value.getValueType())), parameterized(Matcher.class, null, wildcard()));
		}
		return adaptors.tryDeserialize(value, types, this);
	}

	@Override
	public Computation visitLiteral(SerializedLiteral value) {
		return adaptors.tryDeserialize(value, types, this);
	}

	@Override
	public Computation visitNull(SerializedNull value) {
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
	public Computation visitUnknown(SerializedValue value) {
		return Computation.NULL;
	}

	public static class Factory implements SerializedValueVisitorFactory {

		@Override
		public SerializedValueVisitor<Computation> create(LocalVariableNameGenerator locals, TypeManager types) {
			return new ObjectToMatcherCode(locals, types);
		}

		@Override
		public Type resultType(Type type) {
			return parameterized(Matcher.class, null, type);
		}
	}

}
