package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.TypeSelector.in;
import static net.amygdalum.testrecorder.TypeSelector.innerClasses;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callLocalMethod;
import static net.amygdalum.testrecorder.util.Types.array;
import static net.amygdalum.testrecorder.util.Types.equalTypes;
import static net.amygdalum.testrecorder.util.Types.parameterized;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.deserializers.Adaptor;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.values.SerializedArray;
import net.amygdalum.testrecorder.values.SerializedList;

public class ArraysListAdaptor implements Adaptor<SerializedList, ObjectToSetupCode> {
	
	private DefaultArrayAdaptor adaptor;
	
	public ArraysListAdaptor() {
		this.adaptor = new DefaultArrayAdaptor();
	}

	@Override
	public Class<? extends Adaptor<SerializedList, ObjectToSetupCode>> parent() {
		return DefaultListAdaptor.class;
	}

	@Override
	public boolean matches(Type type) {
		return innerClasses(Arrays.class)
			.filter(in("ArrayList"))
			.filter(element -> List.class.isAssignableFrom(element))
			.anyMatch(element -> equalTypes(element, type));
	}

	@Override
	public Computation tryDeserialize(SerializedList value, ObjectToSetupCode generator) {
		TypeManager types = generator.getTypes();
		types.staticImport(Arrays.class, "asList");

		Type type = array(value.getComponentType());
		SerializedArray baseValue = new SerializedArray(type);
		for (SerializedValue element : value) {
			baseValue.add(element);
		}

		Type resultType = parameterized(List.class, null, value.getComponentType());
		String resultList = generator.localVariable(value, resultType);
		
		Computation computation = adaptor.tryDeserialize(baseValue, generator);
		List<String> statements = new LinkedList<>(computation.getStatements());
		String resultArray = computation.getValue();
		
		String asListStatement = assignLocalVariableStatement(types.getBestName(resultType), resultList, callLocalMethod("asList", resultArray));
		statements.add(asListStatement);
		
		generator.finishVariable(value);
		
		return new Computation(resultList, statements);
	}

}
