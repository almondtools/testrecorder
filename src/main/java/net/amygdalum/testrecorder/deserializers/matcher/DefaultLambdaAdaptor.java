package net.amygdalum.testrecorder.deserializers.matcher;

import static net.amygdalum.testrecorder.deserializers.Templates.lambdaMatcher;
import static net.amygdalum.testrecorder.util.Literals.asLiteral;

import java.lang.reflect.Type;

import org.hamcrest.Matcher;

import net.amygdalum.testrecorder.runtime.LambdaMatcher;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.values.LambdaSignature;
import net.amygdalum.testrecorder.values.SerializedLambdaObject;

public class DefaultLambdaAdaptor extends DefaultMatcherGenerator<SerializedLambdaObject> implements MatcherGenerator<SerializedLambdaObject> {
	
	@Override
	public Class<SerializedLambdaObject> getAdaptedClass() {
		return SerializedLambdaObject.class;
	}

	@Override
	public boolean matches(Type type) {
		return true;
	}

	@Override
	public Computation tryDeserialize(SerializedLambdaObject value, MatcherGenerators generator, DeserializerContext context) {
		TypeManager types = context.getTypes();
		types.registerImport(Matcher.class);
		types.staticImport(LambdaMatcher.class, "lambda");
		LambdaSignature signature = value.getSignature();
		return Computation.expression(lambdaMatcher(asLiteral(signature.getImplMethodName())), value.getType());
	}

}
