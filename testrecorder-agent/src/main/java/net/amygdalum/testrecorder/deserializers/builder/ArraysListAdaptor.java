package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callLocalMethod;
import static net.amygdalum.testrecorder.types.Computation.variable;
import static net.amygdalum.testrecorder.util.TypeFilters.in;
import static net.amygdalum.testrecorder.util.Types.array;
import static net.amygdalum.testrecorder.util.Types.equalTypes;
import static net.amygdalum.testrecorder.util.Types.innerClasses;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.wildcard;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.values.SerializedArray;
import net.amygdalum.testrecorder.values.SerializedList;

public class ArraysListAdaptor implements SetupGenerator<SerializedList> {

    private DefaultArrayAdaptor adaptor;

    public ArraysListAdaptor() {
        this.adaptor = new DefaultArrayAdaptor();
    }

    @Override
    public Class<SerializedList> getAdaptedClass() {
        return SerializedList.class;
    }

    @Override
    public Class<? extends SetupGenerator<SerializedList>> parent() {
        return DefaultListAdaptor.class;
    }

    @Override
    public boolean matches(Type type) {
        return innerClasses(Arrays.class).stream()
            .filter(in("ArrayList"))
            .filter(element -> List.class.isAssignableFrom(element))
            .anyMatch(element -> equalTypes(element, type));
    }

    @Override
    public Computation tryDeserialize(SerializedList value, SetupGenerators generator, DeserializerContext context) {
        Type componentType = value.getComponentType();

        TypeManager types = context.getTypes();
        types.staticImport(Arrays.class, "asList");
        types.registerType(componentType);

        Type type = array(componentType);
        SerializedArray baseValue = new SerializedArray(type);
        for (SerializedValue element : value) {
            baseValue.add(element);
        }

        Type resultType = types.isHidden(componentType)
            ? parameterized(List.class, null, wildcard())
            : parameterized(List.class, null, componentType);
        return context.forVariable(value, local -> {

            Computation computation = adaptor.tryDeserialize(baseValue, generator, context);
            List<String> statements = new LinkedList<>(computation.getStatements());
            String resultArray = computation.getValue();

            String asListStatement = assignLocalVariableStatement(types.getVariableTypeName(resultType), local.getName(), callLocalMethod("asList", resultArray));
            statements.add(asListStatement);

            return variable(local.getName(), resultType, statements);
        });
    }

}
