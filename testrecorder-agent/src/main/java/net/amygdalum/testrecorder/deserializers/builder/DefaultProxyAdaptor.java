package net.amygdalum.testrecorder.deserializers.builder;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.deserializers.Templates.arrayLiteral;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callLocalMethod;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethod;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethodChainExpression;
import static net.amygdalum.testrecorder.util.Types.baseType;

import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.values.SerializedImmutable;
import net.amygdalum.testrecorder.values.SerializedProxy;

public class DefaultProxyAdaptor extends DefaultSetupGenerator<SerializedProxy> implements SetupGenerator<SerializedProxy> {

	@Override
	public Class<SerializedProxy> getAdaptedClass() {
		return SerializedProxy.class;
	}

	@Override
	public boolean matches(Type type) {
		Class<?> clazz = baseType(type);
		return Proxy.isProxyClass(clazz)
			|| clazz == Proxy.class;
	}

	@Override
	public Computation tryDeserialize(SerializedProxy value, SetupGenerators generator, DeserializerContext context) {
		TypeManager types = context.getTypes();
		types.registerImport(Proxy.class);

		Type resultType = types.mostSpecialOf(value.getUsedTypes()).orElse(Object.class);
		types.registerType(resultType);

		return context.forVariable(value, Object.class, local -> {
			List<String> statements = new ArrayList<>();
			
			String classLoader = callMethodChainExpression("this", asList(callLocalMethod("getClass"), callLocalMethod("getClassLoader")));
			
			List<String> interfaceList = new ArrayList<>();
			for (SerializedImmutable<Class<?>> interfaceClass : value.getInterfaces()) {
				Computation interfaceComputation = interfaceClass.accept(generator, context);
				statements.addAll(interfaceComputation.getStatements());
				interfaceList.add(interfaceComputation.getValue());
			}
			String interfaces = arrayLiteral(types.getVariableTypeName(Class[].class), interfaceList);

			Computation invocationHandler = value.getInvocationHandler().accept(generator, context);
			statements.addAll(invocationHandler.getStatements());
			String handler = invocationHandler.getValue();
			
			statements.add(assignLocalVariableStatement(types.getVariableTypeName(local.getType()), local.getName(), callMethod("Proxy", "newProxyInstance", classLoader, interfaces, handler)));
			return Computation.variable(local.getName(), local.getType(), statements);
		});
	}

}
