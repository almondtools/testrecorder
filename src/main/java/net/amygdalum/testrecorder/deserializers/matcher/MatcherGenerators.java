package net.amygdalum.testrecorder.deserializers.matcher;

import static net.amygdalum.testrecorder.deserializers.Computation.expression;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.recursiveMatcher;
import static net.amygdalum.testrecorder.util.Literals.asLiteral;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.wildcard;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import org.hamcrest.Matcher;

import net.amygdalum.testrecorder.deserializers.Adaptors;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DeserializerFactory;
import net.amygdalum.testrecorder.deserializers.LocalVariableNameGenerator;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.hints.SkipChecks;
import net.amygdalum.testrecorder.testing.hamcrest.GenericMatcher;
import net.amygdalum.testrecorder.types.Deserializer;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedFieldType;
import net.amygdalum.testrecorder.types.SerializedImmutableType;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.SerializedValueType;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedNull;

public class MatcherGenerators implements Deserializer<Computation> {

    public static final Adaptors<MatcherGenerators> DEFAULT = new Adaptors<MatcherGenerators>()
        .load(MatcherGenerator.class);

    private LocalVariableNameGenerator locals;
    private TypeManager types;
    private Adaptors<MatcherGenerators> adaptors;

    private Set<SerializedValue> computed;

    public MatcherGenerators(Class<?> clazz) {
        this(new LocalVariableNameGenerator(), new TypeManager(clazz.getPackage().getName()), DEFAULT);
    }

    public MatcherGenerators(LocalVariableNameGenerator locals, TypeManager types) {
        this(locals, types, DEFAULT);
    }

    public MatcherGenerators(LocalVariableNameGenerator locals, TypeManager types, Adaptors<MatcherGenerators> adaptors) {
        this.locals = locals;
        this.types = types;
        this.adaptors = adaptors;
        this.computed = new HashSet<>();
    }

    public TypeManager getTypes() {
        return types;
    }

    public String temporaryLocal() {
        return locals.fetchName("temp");
    }

    public String newLocal(String name) {
        return locals.fetchName(name);
    }

    public boolean isSimpleValue(SerializedValue element) {
        return element instanceof SerializedNull
            || element instanceof SerializedLiteral;
    }

    public Computation simpleMatcher(SerializedValue element, DeserializerContext context) {
        if (element instanceof SerializedNull) {
            return expression("null", element.getResultType());
        } else if (element instanceof SerializedLiteral) {
            return expression(asLiteral(((SerializedLiteral) element).getValue()), element.getResultType());
        } else {
            return element.accept(this, context);
        }
    }

    @Override
    public Computation visitField(SerializedFieldType field, DeserializerContext context) {
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
        if (context.getHint(SkipChecks.class).isPresent()) {
            return null;
        } else if (!computed.add(value)) {
            types.staticImport(GenericMatcher.class, "recursive");
            Type resultType = value.getResultType().equals(value.getType()) ? parameterized(Matcher.class, null, value.getResultType()) : parameterized(Matcher.class, null, wildcard());
            if (!types.isHidden(value.getType())) {
                return expression(recursiveMatcher(types.getRawClass(value.getType())), resultType);
            } else if (!types.isHidden(value.getResultType())) {
                return expression(recursiveMatcher(types.getRawClass(value.getResultType())), resultType);
            } else {
                return expression(recursiveMatcher(types.getRawClass(Object.class)), parameterized(Matcher.class, null, wildcard()));
            }
        }
        return adaptors.tryDeserialize(value, types, this, context);
    }

    @Override
    public Computation visitImmutableType(SerializedImmutableType value, DeserializerContext context) {
        if (context.getHint(SkipChecks.class).isPresent()) {
            return null;
        }
        return adaptors.tryDeserialize(value, types, this, context);
    }

    @Override
    public Computation visitValueType(SerializedValueType value, DeserializerContext context) {
        if (context.getHint(SkipChecks.class).isPresent()) {
            return null;
        }
        return adaptors.tryDeserialize(value, types, this, context);
    }

    public static class Factory implements DeserializerFactory {

        @Override
        public Deserializer<Computation> create(LocalVariableNameGenerator locals, TypeManager types) {
            return new MatcherGenerators(locals, types);
        }
        
        @Override
        public Type resultType(Type type) {
            return parameterized(Matcher.class, null, type);
        }
    }

}
