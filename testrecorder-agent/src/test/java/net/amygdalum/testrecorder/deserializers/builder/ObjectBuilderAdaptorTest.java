package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.TestAgentConfiguration.defaultConfig;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.Adaptors;
import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.deserializers.Deserializer;
import net.amygdalum.testrecorder.hints.Builder;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializationException;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.FieldSignature;
import net.amygdalum.testrecorder.types.SerializedField;
import net.amygdalum.testrecorder.values.SerializedObject;

public class ObjectBuilderAdaptorTest {

	private AgentConfiguration config;
	private ObjectBuilderAdaptor adaptor;
	private DeserializerContext context;

	@BeforeEach
	public void before() throws Exception {
		config = defaultConfig();
		adaptor = new ObjectBuilderAdaptor();
		context = new DefaultDeserializerContext();
	}

	@Test
	public void testParentIsDefaultObject() throws Exception {
		assertThat(adaptor.parent()).isSameAs(DefaultObjectAdaptor.class);
	}

	@Test
	public void testMatchesAnyObject() throws Exception {
		assertThat(adaptor.matches(Object.class)).isTrue();
		assertThat(adaptor.matches(new Object() {
		}.getClass())).isTrue();
	}

	@Test
	public void testTryDeserializeWithBuildable() throws Exception {
		SerializedObject value = new SerializedObject(Buildable.class);
		value.addField(new SerializedField(new FieldSignature(Buildable.class, int.class, "a"), literal(1)));
		value.addField(new SerializedField(new FieldSignature(Buildable.class, String.class, "b"), literal("2")));

		context.getTypes().registerImport(Buildable.class);
		Deserializer generator = generator();

		Computation deserialized = adaptor.tryDeserialize(value, generator);
		assertThat(deserialized.getStatements()).containsExactly("Buildable buildable1 = new MyBuilder().withA(1).withB(\"2\").build();");
		assertThat(deserialized.getValue()).isEqualTo("buildable1");

	}

	@Test
	public void testTryDeserializeWithNonBuildable() throws Exception {
		SerializedObject value = new SerializedObject(NotBuildable.class);
		value.addField(new SerializedField(new FieldSignature(Buildable.class, int.class, "a"), literal(1)));
		value.addField(new SerializedField(new FieldSignature(Buildable.class, String.class, "b"), literal("2")));

		context.getTypes().registerImport(NotBuildable.class);
		Deserializer generator = generator();

		assertThrows(DeserializationException.class, () -> adaptor.tryDeserialize(value, generator));
	}

	@Test
	public void testTryDeserializeWithBuilderMissingConstructor() throws Exception {
		SerializedObject value = new SerializedObject(BuildableButNoBuilderConstructor.class);
		value.addField(new SerializedField(new FieldSignature(Buildable.class, int.class, "a"), literal(1)));
		value.addField(new SerializedField(new FieldSignature(Buildable.class, String.class, "b"), literal("2")));

		context.getTypes().registerImport(BuildableButNoBuilderConstructor.class);
		Deserializer generator = generator();

		assertThrows(DeserializationException.class, () -> adaptor.tryDeserialize(value, generator));
	}

	@Test
	public void testTryDeserializeWithBuilderMissingWithMethod() throws Exception {
		SerializedObject value = new SerializedObject(BuildableButMissingWith.class);
		value.addField(new SerializedField(new FieldSignature(Buildable.class, int.class, "a"), literal(1)));
		value.addField(new SerializedField(new FieldSignature(Buildable.class, String.class, "b"), literal("2")));

		context.getTypes().registerImport(BuildableButMissingWith.class);
		Deserializer generator = generator();

		assertThrows(DeserializationException.class, () -> adaptor.tryDeserialize(value, generator));
	}

	@Test
	public void testTryDeserializeWithBuilderBrokenWithMethod() throws Exception {
		SerializedObject value = new SerializedObject(BuildableButBrokenWith.class);
		value.addField(new SerializedField(new FieldSignature(Buildable.class, int.class, "a"), literal(1)));
		value.addField(new SerializedField(new FieldSignature(Buildable.class, String.class, "b"), literal("2")));

		context.getTypes().registerImport(BuildableButBrokenWith.class);
		Deserializer generator = generator();

		assertThrows(DeserializationException.class, () -> adaptor.tryDeserialize(value, generator));
	}

	@Test
	public void testTryDeserializeWithBuilderMissingBuildMethod() throws Exception {
		SerializedObject value = new SerializedObject(BuildableButMissingBuild.class);
		value.addField(new SerializedField(new FieldSignature(Buildable.class, int.class, "a"), literal(1)));
		value.addField(new SerializedField(new FieldSignature(Buildable.class, String.class, "b"), literal("2")));

		context.getTypes().registerImport(BuildableButMissingBuild.class);
		Deserializer generator = generator();

		assertThrows(DeserializationException.class, () -> adaptor.tryDeserialize(value, generator));
	}

	@Test
	public void testTryDeserializeWithBuilderBrokenBuildMethod() throws Exception {
		SerializedObject value = new SerializedObject(BuildableButBrokenBuild.class);
		value.addField(new SerializedField(new FieldSignature(Buildable.class, int.class, "a"), literal(1)));
		value.addField(new SerializedField(new FieldSignature(Buildable.class, String.class, "b"), literal("2")));

		context.getTypes().registerImport(BuildableButBrokenBuild.class);
		Deserializer generator = generator();

		assertThrows(DeserializationException.class, () -> adaptor.tryDeserialize(value, generator));
	}

	private Deserializer generator() {
		return new SetupGenerators(new Adaptors().load(config.loadConfigurations(SetupGenerator.class))).newGenerator(context);
	}

	@Builder(builder = MyBuilder.class)
	public static class Buildable {
		private int a;
		private String b;

		@Override
		public String toString() {
			return a + ":" + b;
		}
	}

	public static class MyBuilder {
		private Buildable build;

		public MyBuilder() {
			this.build = new Buildable();
		}

		public MyBuilder withA(int a) {
			build.a = a;
			return this;
		}

		public MyBuilder withB(String b) {
			build.b = b;
			return this;
		}

		public Buildable build() {
			return build;
		}
	}

	public static class NotBuildable {
		private int a;
		private String b;

		@Override
		public String toString() {
			return a + ":" + b;
		}
	}

	@Builder(builder = BuildableButNoBuilderConstructor.Builder.class)
	public static class BuildableButNoBuilderConstructor {
		private int a;
		private String b;

		@Override
		public String toString() {
			return a + ":" + b;
		}

		public static class Builder {
			private BuildableButNoBuilderConstructor build;

			public Builder(BuildableButNoBuilderConstructor build) {
				this.build = build;
			}

			public Builder withA(int a) {
				build.a = a;
				return this;
			}

			public Builder withB(String b) {
				build.b = b;
				return this;
			}

			public BuildableButNoBuilderConstructor build() {
				return build;
			}
		}
	}

	@Builder(builder = BuildableButMissingWith.Builder.class)
	public static class BuildableButMissingWith {
		private int a;
		private String b;

		@Override
		public String toString() {
			return a + ":" + b;
		}

		public static class Builder {
			private BuildableButMissingWith build;

			public Builder() {
				this.build = new BuildableButMissingWith();
			}

			public Builder withB(String b) {
				build.b = b;
				return this;
			}

			public BuildableButMissingWith build() {
				return build;
			}
		}

	}

	@Builder(builder = BuildableButBrokenWith.Builder.class)
	public static class BuildableButBrokenWith {
		private int a;
		private String b;

		@Override
		public String toString() {
			return a + ":" + b;
		}

		public static class Builder {
			private BuildableButBrokenWith build;

			public Builder() {
				this.build = new BuildableButBrokenWith();
			}

			public BuildableButBrokenWith withA(int a) {
				build.a = a;
				return build;
			}

			public Builder withB(String b) {
				build.b = b;
				return this;
			}

			public BuildableButBrokenWith build() {
				return build;
			}
		}

	}

	@Builder(builder = BuildableButMissingBuild.Builder.class)
	public static class BuildableButMissingBuild {
		private int a;
		private String b;

		@Override
		public String toString() {
			return a + ":" + b;
		}

		public static class Builder {
			private BuildableButMissingBuild build;

			public Builder() {
				this.build = new BuildableButMissingBuild();
			}

			public Builder withA(int a) {
				build.a = a;
				return this;
			}

			public Builder withB(String b) {
				build.b = b;
				return this;
			}

		}

	}

	@Builder(builder = BuildableButBrokenBuild.Builder.class)
	public static class BuildableButBrokenBuild {
		private int a;
		private String b;

		@Override
		public String toString() {
			return a + ":" + b;
		}

		public static class Builder {
			private BuildableButBrokenBuild build;

			public Builder() {
				this.build = new BuildableButBrokenBuild();
			}

			public Builder withA(int a) {
				build.a = a;
				return this;
			}

			public Builder withB(String b) {
				build.b = b;
				return this;
			}

			public Object build() {
				return build;
			}
		}

	}

}
