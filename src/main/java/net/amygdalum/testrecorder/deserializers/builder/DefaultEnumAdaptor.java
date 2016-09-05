package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.deserializers.Templates.asLiteral;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethod;
import static net.amygdalum.testrecorder.deserializers.Templates.fieldAccess;
import static net.amygdalum.testrecorder.util.Types.baseType;

import java.lang.reflect.Type;

import net.amygdalum.testrecorder.Wrapped;
import net.amygdalum.testrecorder.deserializers.Adaptor;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DefaultAdaptor;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.values.SerializedEnum;

public class DefaultEnumAdaptor extends DefaultAdaptor<SerializedEnum, ObjectToSetupCode> implements Adaptor<SerializedEnum, ObjectToSetupCode> {

	@Override
	public boolean matches(Type type) {
		return baseType(type).isEnum();
	}

	@Override
	public Computation tryDeserialize(SerializedEnum value, ObjectToSetupCode generator) {
		TypeManager types = generator.getTypes();
		types.registerType(value.getType());

		if (types.isHidden(value.getType())) {
			String typeName = types.getBestSignature(value.getType());
			String typeArgument = asLiteral(typeName);
			String wrapped = callMethod(types.getRawName(Wrapped.class), "enumType", typeArgument, asLiteral(value.getName()));
			String unwrapped = callMethod(wrapped, "value");
			return new Computation(unwrapped, value.getResultType());
		} else {
			String typeName = types.getBestName(value.getType());
			String name = value.getName();

			String enumConstant = fieldAccess(typeName, name);
			return new Computation(enumConstant, value.getResultType());
		}
	}

}
