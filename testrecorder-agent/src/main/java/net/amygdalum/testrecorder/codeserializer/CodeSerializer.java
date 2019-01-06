package net.amygdalum.testrecorder.codeserializer;

import static java.util.stream.Collectors.joining;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.profile.ConfigurationLoader.defaultClassLoader;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.amygdalum.testrecorder.ConfigurableSerializerFacade;
import net.amygdalum.testrecorder.DefaultPerformanceProfile;
import net.amygdalum.testrecorder.DefaultSerializationProfile;
import net.amygdalum.testrecorder.DefaultSnapshotConsumer;
import net.amygdalum.testrecorder.deserializers.CustomAnnotation;
import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.deserializers.Deserializer;
import net.amygdalum.testrecorder.deserializers.DeserializerFactory;
import net.amygdalum.testrecorder.deserializers.DeserializerTypeManager;
import net.amygdalum.testrecorder.deserializers.builder.SetupGenerators;
import net.amygdalum.testrecorder.generator.DefaultTestGeneratorProfile;
import net.amygdalum.testrecorder.generator.TestGeneratorProfile;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.profile.ClassPathConfigurationLoader;
import net.amygdalum.testrecorder.profile.DefaultPathConfigurationLoader;
import net.amygdalum.testrecorder.profile.PerformanceProfile;
import net.amygdalum.testrecorder.profile.SerializationProfile;
import net.amygdalum.testrecorder.profile.SnapshotConsumer;
import net.amygdalum.testrecorder.serializers.SerializerFacade;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.LocalVariableNameGenerator;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.types.TypeManager;

public class CodeSerializer {

	private TestGeneratorProfile generatorProfile;
	private SerializerFacade facade;
	private SerializerSession session;
	private TypeManager types;
	private DeserializerFactory deserializers;

	public CodeSerializer() {
		this("", ConfigurableSerializerFacade::new, SetupGenerators::new);
	}

	public CodeSerializer(String pkg) {
		this(pkg, defaultClassLoader(CodeSerializer.class), ConfigurableSerializerFacade::new, SetupGenerators::new);
	}

	public CodeSerializer(String pkg, Function<AgentConfiguration, SerializerFacade> facade, Function<AgentConfiguration, DeserializerFactory> deserializers) {
		this(pkg, defaultClassLoader(CodeSerializer.class), facade, deserializers);
	}

	public CodeSerializer(String pkg, ClassLoader loader, Function<AgentConfiguration, SerializerFacade> facade, Function<AgentConfiguration, DeserializerFactory> deserializers) {
		this(pkg, new AgentConfiguration(new ClassPathConfigurationLoader(loader), new DefaultPathConfigurationLoader(loader))
			.withDefaultValue(SerializationProfile.class, DefaultSerializationProfile::new)
			.withDefaultValue(PerformanceProfile.class, DefaultPerformanceProfile::new)
			.withDefaultValue(SnapshotConsumer.class, DefaultSnapshotConsumer::new), facade, deserializers);
	}

	public CodeSerializer(String pkg, AgentConfiguration config, Function<AgentConfiguration, SerializerFacade> facade, Function<AgentConfiguration, DeserializerFactory> deserializers) {
		this.generatorProfile = config.loadOptionalConfiguration(TestGeneratorProfile.class)
			.orElseGet(DefaultTestGeneratorProfile::new);
		this.facade = facade.apply(config);
		this.session = this.facade.newSession();
		this.deserializers = deserializers.apply(config);
		this.types = new DeserializerTypeManager(pkg);
	}

	public TypeManager getTypes() {
		return types;
	}

	public String serialize(Object value) {
		return serialize(value.getClass(), value);
	}

	public String serialize(Type type, Object value) {
		SerializedValue serializedValue = facade.serialize(type, value, session);

		return new Generator(serializedValue).generateCode();
	}

	private class Generator {

		private LocalVariableNameGenerator locals;
		private SerializedValue value;
		private List<String> statements;
		private Deserializer deserializer;

		Generator(SerializedValue value) {
			this.value = value;
			this.locals = new LocalVariableNameGenerator();
			this.statements = new ArrayList<>();

			this.deserializer = deserializers.newGenerator(newContext(locals));
		}

		private DefaultDeserializerContext newContext(LocalVariableNameGenerator locals) {
			DefaultDeserializerContext context = new DefaultDeserializerContext(types, locals);
			for (CustomAnnotation annotation : generatorProfile.annotations()) {
				context.addHint(annotation.getTarget(), annotation.getAnnotation());
			}
			return context;
		}

		public String generateCode() {
			Computation serialized = value.accept(deserializer);

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
