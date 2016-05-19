package net.amygdalum.testrecorder.deserializers.matcher;

import static net.amygdalum.testrecorder.deserializers.Templates.asLiteral;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.nullMatcher;
import static net.amygdalum.testrecorder.deserializers.Templates.recursiveMatcher;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.wildcard;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

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
import net.amygdalum.testrecorder.util.GenericMatcher;
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

public class ObjectToMatcherCode implements Deserializer<Computation> {

	public static final Adaptors<ObjectToMatcherCode> DEFAULT = new Adaptors<ObjectToMatcherCode>()
		.add(SerializedLiteral.class, new DefaultLiteralAdaptor())
		.add(SerializedNull.class, new DefaultNullAdaptor())
		.add(SerializedImmutable.class, new DefaultClassAdaptor())
		.add(SerializedImmutable.class, new DefaultBigIntegerAdaptor())
		.add(SerializedImmutable.class, new DefaultBigDecimalAdaptor())
		.add(SerializedEnum.class, new DefaultEnumAdaptor())
		.add(SerializedObject.class, new DefaultObjectAdaptor())
		.add(SerializedArray.class, new DefaultArrayAdaptor())
		.add(SerializedList.class, new DefaultListAdaptor())
		.add(SerializedSet.class, new DefaultSetAdaptor())
		.add(SerializedMap.class, new DefaultMapAdaptor());

	private TypeManager types;
	private Adaptors<ObjectToMatcherCode> adaptors;

	private Set<SerializedValue> computed;

	public ObjectToMatcherCode(Class<?> clazz) {
		this(new LocalVariableNameGenerator(), new TypeManager(clazz.getPackage().getName()), DEFAULT);
	}

	public ObjectToMatcherCode(LocalVariableNameGenerator locals, TypeManager types) {
		this(locals, types, DEFAULT);
	}

	public ObjectToMatcherCode(LocalVariableNameGenerator locals, TypeManager types, Adaptors<ObjectToMatcherCode> adaptors) {
		this.types = types;
		this.adaptors = adaptors;
		this.computed = new HashSet<>();
	}
	
	public TypeManager getTypes() {
		return types;
	}

	public boolean isSimpleValue(SerializedValue element) {
		return element instanceof SerializedNull
			|| element instanceof SerializedLiteral;
	}

	public Computation simpleMatcher(SerializedValue element) {
		if (element instanceof SerializedNull) {
			types.staticImport(Matchers.class, "nullValue");
			return new Computation(nullMatcher(""), element.getResultType());
		} else if (element instanceof SerializedLiteral) {
			return new Computation(asLiteral(((SerializedLiteral) element).getValue()), element.getResultType());
		} else {
			return element.accept(this);
		}
	}

	public Computation simpleValue(SerializedValue element) {
		if (element instanceof SerializedNull) {
			return new Computation("null", element.getResultType());
		} else if (element instanceof SerializedLiteral) {
			return new Computation(asLiteral(((SerializedLiteral) element).getValue()), element.getResultType());
		} else {
			return element.accept(this);
		}
	}

	@Override
	public Computation visitField(SerializedField field) {
		SerializedValue fieldValue = field.getValue();
		if (isSimpleValue(fieldValue)) {
			types.registerImport(baseType(field.getType()));
			Computation value = simpleValue(fieldValue);

			String assignField = assignLocalVariableStatement(types.getRawName(field.getType()), field.getName(), value.getValue());
			return new Computation(assignField, null, value.getStatements());
		} else {
			types.registerImport(Matcher.class);
			Computation value = fieldValue.accept(this);

			String genericType = types.getSimpleName(value.getType());

			String assignField = assignLocalVariableStatement(genericType, field.getName(), value.getValue());
			return new Computation(assignField, null, value.getStatements());
		}
	}
	
	@Override
	public Computation visitReferenceType(SerializedReferenceType value) {
		if (!computed.add(value)) {
			types.staticImport(GenericMatcher.class, "recursive");
			Type resultType = value.getResultType().equals(value.getType()) ? parameterized(Matcher.class, null, value.getResultType()) : parameterized(Matcher.class, null, wildcard());
			return new Computation(recursiveMatcher(types.getRawTypeName(value.getType())), resultType);
		}
		return adaptors.tryDeserialize(value, types, this);
	}
	
	@Override
	public Computation visitImmutableType(SerializedImmutableType value) {
		return adaptors.tryDeserialize(value, types, this);
	}
	
	@Override
	public Computation visitValueType(SerializedValueType value) {
		return adaptors.tryDeserialize(value, types, this);
	}

	public static class Factory implements DeserializerFactory {

		@Override
		public Deserializer<Computation> create(LocalVariableNameGenerator locals, TypeManager types) {
			return new ObjectToMatcherCode(locals, types);
		}

		@Override
		public Type resultType(Type type) {
			return parameterized(Matcher.class, null, type);
		}
	}

}
