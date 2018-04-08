package net.amygdalum.testrecorder.deserializers.matcher;

import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.recursiveMatcher;
import static net.amygdalum.testrecorder.types.Computation.expression;
import static net.amygdalum.testrecorder.util.Literals.asLiteral;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.wildcard;

import java.lang.reflect.Type;

import org.hamcrest.Matcher;

import net.amygdalum.testrecorder.deserializers.Adaptors;
import net.amygdalum.testrecorder.hints.SkipChecks;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.runtime.GenericMatcher;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.Deserializer;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedFieldType;
import net.amygdalum.testrecorder.types.SerializedImmutableType;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.SerializedValueType;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedNull;

public class MatcherGenerators implements Deserializer<Computation> {

	private Adaptors<MatcherGenerators> adaptors;

	public MatcherGenerators(AgentConfiguration config) {
		this.adaptors = new Adaptors<MatcherGenerators>(config).load(MatcherGenerator.class);
	}

	public boolean isSimpleValue(SerializedValue element) {
		return element instanceof SerializedNull
			|| element instanceof SerializedLiteral;
	}

	public Computation simpleMatcher(SerializedValue element, DeserializerContext context) {
		TypeManager types = context.getTypes();
		Type usedType = types.mostSpecialOf(element.getUsedTypes()).orElse(Object.class);
		if (element instanceof SerializedNull) {
			return expression("null", usedType);
		} else if (element instanceof SerializedLiteral) {
			return expression(asLiteral(((SerializedLiteral) element).getValue()), usedType);
		} else {
			return element.accept(this, context);
		}
	}

	@Override
	public Computation visitField(SerializedFieldType field, DeserializerContext context) {
		TypeManager types = context.getTypes();
		SerializedValue fieldValue = field.getValue();
		DeserializerContext ctx = context.newWithHints(field.getAnnotations());
		if (ctx.getHint(SkipChecks.class).isPresent()) {
			return null;
		} else if (isSimpleValue(fieldValue)) {
			types.registerImport(baseType(field.getType()));
			Computation value = simpleMatcher(fieldValue, ctx);

			String assignField = assignLocalVariableStatement(types.getRawTypeName(field.getType()), field.getName(), value.getValue());
			return expression(assignField, null, value.getStatements());
		} else {
			types.registerImport(Matcher.class);
			Computation value = fieldValue.accept(this, ctx);

			String genericType = types.getVariableTypeName(parameterized(Matcher.class, null, wildcard()));

			String assignField = assignLocalVariableStatement(genericType, field.getName(), value.getValue());
			return expression(assignField, null, value.getStatements());
		}
	}

	@Override
	public Computation visitReferenceType(SerializedReferenceType value, DeserializerContext context) {
		TypeManager types = context.getTypes();
		Type usedType = types.mostSpecialOf(value.getUsedTypes()).orElse(Object.class);
		if (context.getHint(SkipChecks.class).isPresent()) {
			return null;
		} else if (context.isComputed(value)) {
			types.staticImport(GenericMatcher.class, "recursive");
			Type resultType = usedType.equals(value.getType()) ? parameterized(Matcher.class, null, usedType) : parameterized(Matcher.class, null, wildcard());
			if (!types.isHidden(value.getType())) {
				return expression(recursiveMatcher(types.getRawClass(value.getType())), resultType);
			} else if (!types.isHidden(usedType)) {
				return expression(recursiveMatcher(types.getRawClass(usedType)), resultType);
			} else {
				return expression(recursiveMatcher(types.getRawClass(Object.class)), parameterized(Matcher.class, null, wildcard()));
			}
		}
		return adaptors.tryDeserialize(value, types, this, context);
	}

	@Override
	public Computation visitImmutableType(SerializedImmutableType value, DeserializerContext context) {
		TypeManager types = context.getTypes();
		if (context.getHint(SkipChecks.class).isPresent()) {
			return null;
		}
		return adaptors.tryDeserialize(value, types, this, context);
	}

	@Override
	public Computation visitValueType(SerializedValueType value, DeserializerContext context) {
		TypeManager types = context.getTypes();
		if (context.getHint(SkipChecks.class).isPresent()) {
			return null;
		}
		return adaptors.tryDeserialize(value, types, this, context);
	}

}
