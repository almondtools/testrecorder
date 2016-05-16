package net.amygdalum.testrecorder;

import static java.util.stream.Collectors.joining;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DeserializerFactory;
import net.amygdalum.testrecorder.deserializers.LocalVariableNameGenerator;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.deserializers.builder.ObjectToSetupCode;
import net.amygdalum.testrecorder.profile.DefaultSerializationProfile;

public class CodeSerializer {

	private SerializerFacade facade;
	private TypeManager types;
	private DeserializerFactory serializers;

	public CodeSerializer() {
		this("");
	}
	
	public CodeSerializer(String pkg) {
		this(pkg, new ConfigurableSerializerFacade(new DefaultSerializationProfile()), new ObjectToSetupCode.Factory());
	}
	
	public CodeSerializer(String pkg, DeserializerFactory serializers) {
		this(pkg, new ConfigurableSerializerFacade(new DefaultSerializationProfile()), serializers);
	}
	
	public CodeSerializer(String pkg, SerializerFacade facade, DeserializerFactory serializers) {
		this.types = new TypeManager(pkg);
		this.facade = facade;
		this.serializers = serializers;
	}
	
	public String serialize(Object value) {
		return serialize(serializers.resultType(value.getClass()), value);
	}
	
	public String serialize(Type type, Object value) {
		SerializedValue serializedValue = facade.serialize(type, value);

		return new Generator(serializedValue).generateCode();
	}

	private class Generator {

		private LocalVariableNameGenerator locals;

		private SerializedValue value;

		private String type;
		private List<String> statements;

		public Generator(SerializedValue value) {
			this.value = value;
			this.type = types.getSimpleName(value.getResultType());
			this.locals = new LocalVariableNameGenerator();
			this.statements = new ArrayList<>();
		}

		public String generateCode() {
			Deserializer<Computation> serializer = serializers.create(locals, types);
			
			Computation serialized = value.accept(serializer);

			statements.addAll(serialized.getStatements());
			if (!serialized.isStored()) {
				String name = locals.fetchName(value.getClass());
				statements.add(assignLocalVariableStatement(type, name, serialized.getValue()));
			}

			return statements.stream()
				.collect(joining("\n"));
		}

	}

}
