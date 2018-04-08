package net.amygdalum.testrecorder.codeserializer;

import static java.util.stream.Collectors.joining;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.amygdalum.testrecorder.ConfigurableSerializerFacade;
import net.amygdalum.testrecorder.DefaultPerformanceProfile;
import net.amygdalum.testrecorder.DefaultSerializationProfile;
import net.amygdalum.testrecorder.DefaultSnapshotConsumer;
import net.amygdalum.testrecorder.SnapshotConsumer;
import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.deserializers.DeserializerTypeManager;
import net.amygdalum.testrecorder.deserializers.builder.SetupGenerators;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.profile.PerformanceProfile;
import net.amygdalum.testrecorder.profile.SerializationProfile;
import net.amygdalum.testrecorder.serializers.SerializerFacade;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.Deserializer;
import net.amygdalum.testrecorder.types.LocalVariableNameGenerator;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.TypeManager;

public class CodeSerializer {

	private AgentConfiguration config;
	private SerializerFacade facade;
	private TypeManager types;
	private Deserializer<Computation> deserializer;

	public CodeSerializer() {
		this("");
	}

	public CodeSerializer(String pkg) {
		this(pkg, config -> new ConfigurableSerializerFacade(config), config -> new SetupGenerators(config));
	}

	public CodeSerializer(String pkg, Function<AgentConfiguration, SerializerFacade> facade, Function<AgentConfiguration, Deserializer<Computation>> deserializer) {
		this.types = new DeserializerTypeManager(pkg);
		this.config = new AgentConfiguration()
			.withDefaultValue(SerializationProfile.class, DefaultSerializationProfile::new)
			.withDefaultValue(PerformanceProfile.class, DefaultPerformanceProfile::new)
			.withDefaultValue(SnapshotConsumer.class, DefaultSnapshotConsumer::new);
		this.facade = facade.apply(config);
		this.deserializer = deserializer.apply(config);
	}

	public TypeManager getTypes() {
		return types;
	}

	public String serialize(Object value) {
		return serialize(value.getClass(), value);
	}

	public String serialize(Type type, Object value) {
		SerializedValue serializedValue = facade.serialize(type, value);

		return new Generator(serializedValue).generateCode();
	}

	private class Generator {

		private LocalVariableNameGenerator locals;

		private SerializedValue value;

		private List<String> statements;

		public Generator(SerializedValue value) {
			this.value = value;
			this.locals = new LocalVariableNameGenerator();
			this.statements = new ArrayList<>();
		}

		public String generateCode() {
			Computation serialized = value.accept(deserializer, new DefaultDeserializerContext(types, locals));

			statements.addAll(serialized.getStatements());
			if (!serialized.isStored()) {
				String name = locals.fetchName(value.getClass());
				String type = types.getVariableTypeName(serialized.getType());
				statements.add(assignLocalVariableStatement(type, name, serialized.getValue()));
			}

			return statements.stream()
				.collect(joining("\n"));
		}

	}

}
