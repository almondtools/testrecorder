package com.almondtools.testrecorder;

import static com.almondtools.testrecorder.visitors.Templates.assignLocalVariableStatement;
import static java.util.stream.Collectors.joining;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.almondtools.testrecorder.profile.DefaultSerializationProfile;
import com.almondtools.testrecorder.visitors.Computation;
import com.almondtools.testrecorder.visitors.LocalVariableNameGenerator;
import com.almondtools.testrecorder.visitors.ObjectToSetupCode;
import com.almondtools.testrecorder.visitors.SerializedValueVisitorFactory;
import com.almondtools.testrecorder.visitors.TypeManager;

public class CodeSerializer {

	private SerializerFacade facade;
	private TypeManager types;
	private SerializedValueVisitorFactory serializers;

	public CodeSerializer() {
		this(new ConfigurableSerializerFacade(new DefaultSerializationProfile()), new ObjectToSetupCode.Factory());
	}
	
	public CodeSerializer(SerializedValueVisitorFactory serializers) {
		this(new ConfigurableSerializerFacade(new DefaultSerializationProfile()), serializers);
	}
	
	public CodeSerializer(SerializerFacade facade, SerializedValueVisitorFactory serializers) {
		this.types = new TypeManager();
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
			this.type = types.getSimpleName(value.getType());
			this.locals = new LocalVariableNameGenerator();
			this.statements = new ArrayList<>();
		}

		public String generateCode() {
			SerializedValueVisitor<Computation> serializer = serializers.create(locals, types);
			
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
