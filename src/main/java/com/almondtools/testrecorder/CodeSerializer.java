package com.almondtools.testrecorder;

import static com.almondtools.testrecorder.TypeHelper.getSimpleName;
import static java.util.stream.Collectors.joining;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.almondtools.testrecorder.profile.DefaultSerializationProfile;
import com.almondtools.testrecorder.visitors.Computation;
import com.almondtools.testrecorder.visitors.ImportManager;
import com.almondtools.testrecorder.visitors.LocalVariableNameGenerator;
import com.almondtools.testrecorder.visitors.ObjectToSetupCode;
import com.almondtools.testrecorder.visitors.SerializedValueVisitorFactory;
import com.almondtools.testrecorder.visitors.Templates;

public class CodeSerializer {

	private SerializerFacade facade;
	private ImportManager imports;
	private SerializedValueVisitorFactory serializers;

	public CodeSerializer() {
		this.imports = new ImportManager();
		this.facade = new ConfigurableSerializerFacade(new DefaultSerializationProfile());
		this.serializers = new ObjectToSetupCode.Factory();
	}
	
	public void setFacade(SerializerFacade facade) {
		this.facade = facade;
	}
	
	public void setSerializers(SerializedValueVisitorFactory serializers) {
		this.serializers = serializers;
	}
	
	public String serialize(Object value) {
		return (serialize(value.getClass(), value));
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
			this.type = getSimpleName(value.getType());
			this.locals = new LocalVariableNameGenerator();
			this.statements = new ArrayList<>();
		}

		public String generateCode() {
			SerializedValueVisitor<Computation> serializer = serializers.create(locals, imports);
			
			Computation serialized = value.accept(serializer);

			statements.addAll(serialized.getStatements());
			if (!serialized.isStored()) {
				String name = locals.fetchName(value.getClass());
				statements.add(Templates.assignStatement(type, name, serialized.getValue()));
			}

			return statements.stream()
				.collect(joining("\n"));
		}

	}

}
