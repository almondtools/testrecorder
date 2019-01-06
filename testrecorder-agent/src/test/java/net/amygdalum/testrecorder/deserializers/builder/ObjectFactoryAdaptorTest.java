package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.TestAgentConfiguration.defaultConfig;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static net.amygdalum.testrecorder.values.SerializedNull.nullInstance;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.annotation.Annotation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.Adaptors;
import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.deserializers.Deserializer;
import net.amygdalum.testrecorder.hints.Factory;
import net.amygdalum.testrecorder.hints.Name;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializationException;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.FieldSignature;
import net.amygdalum.testrecorder.types.SerializedField;
import net.amygdalum.testrecorder.values.SerializedObject;

public class ObjectFactoryAdaptorTest {

	private AgentConfiguration config;
	private ObjectFactoryAdaptor adaptor;
	private DeserializerContext context;

	@BeforeEach
	public void before() throws Exception {
		config = defaultConfig();
		adaptor = new ObjectFactoryAdaptor();
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
	public void testTryDeserializeWithFactorable() throws Exception {
		SerializedObject value = new SerializedObject(Factorable.class);
		value.addField(new SerializedField(new FieldSignature(Factorable.class, int.class, "a"), literal(1)));
		value.addField(new SerializedField(new FieldSignature(Factorable.class, String.class, "b"), literal("2")));

		context.getTypes().registerImport(Factorable.class);
		Deserializer generator = generator();

		Computation deserialized = adaptor.tryDeserialize(value, generator);
		assertThat(deserialized.getStatements()).containsExactly("Factorable factorable1 = MyFactory.build(1, \"2\");");
		assertThat(deserialized.getValue()).isEqualTo("factorable1");
	}

	@Test
	public void testTryDeserializeWithFactorablePartialState1() throws Exception {
		SerializedObject value = new SerializedObject(Factorable.class);
		value.addField(new SerializedField(new FieldSignature(Factorable.class, int.class, "a"), literal(0)));
		value.addField(new SerializedField(new FieldSignature(Factorable.class, String.class, "b"), literal("2")));

		context.getTypes().registerImport(Factorable.class);
		Deserializer generator = generator();

		Computation deserialized = adaptor.tryDeserialize(value, generator);
		assertThat(deserialized.getStatements()).containsExactly("Factorable factorable1 = MyFactory.build(\"2\");");
		assertThat(deserialized.getValue()).isEqualTo("factorable1");
	}

	@Test
	public void testTryDeserializeWithFactorablePartialState2() throws Exception {
		SerializedObject value = new SerializedObject(Factorable.class);
		value.addField(new SerializedField(new FieldSignature(Factorable.class, int.class, "a"), literal(1)));
		value.addField(new SerializedField(new FieldSignature(Factorable.class, String.class, "b"), nullInstance()));

		context.getTypes().registerImport(Factorable.class);
		Deserializer generator = generator();

		Computation deserialized = adaptor.tryDeserialize(value, generator);
		assertThat(deserialized.getStatements()).containsExactly("Factorable factorable1 = MyFactory.build(1);");
		assertThat(deserialized.getValue()).isEqualTo("factorable1");
	}

	@Test
	public void testTryDeserializeWithFactorableNullState() throws Exception {
		SerializedObject value = new SerializedObject(Factorable.class);
		value.addField(new SerializedField(new FieldSignature(Factorable.class, int.class, "a"), literal(0)));
		value.addField(new SerializedField(new FieldSignature(Factorable.class, String.class, "b"), nullInstance()));

		context.getTypes().registerImport(Factorable.class);
		Deserializer generator = generator();

		Computation deserialized = adaptor.tryDeserialize(value, generator);
		assertThat(deserialized.getStatements()).containsExactly("Factorable factorable1 = MyFactory.build();");
		assertThat(deserialized.getValue()).isEqualTo("factorable1");
	}

	@Test
	public void testTryDeserializeWithNonFactorable() throws Exception {
		SerializedObject value = new SerializedObject(NotFactorable.class);
		value.addField(new SerializedField(new FieldSignature(NotFactorable.class, int.class, "a"), literal(1)));
		value.addField(new SerializedField(new FieldSignature(NotFactorable.class, String.class, "b"), literal("2")));

		context.getTypes().registerImport(NotFactorable.class);
		Deserializer generator = generator();

		assertThrows(DeserializationException.class, () -> adaptor.tryDeserialize(value, generator));
	}

	@Test
	public void testTryDeserializeWithNonFactorableNeedingDisambiguation() throws Exception {
		SerializedObject value = new SerializedObject(NotFactorableNeedingDisambiguation.class);
		value.addField(new SerializedField(new FieldSignature(NotFactorableNeedingDisambiguation.class, int.class, "a"), literal(1)));
		value.addField(new SerializedField(new FieldSignature(NotFactorableNeedingDisambiguation.class, int.class, "b"), literal(2)));
		
		context.getTypes().registerImport(NotFactorableNeedingDisambiguation.class);
		Deserializer generator = generator();
		
		assertThrows(DeserializationException.class, () -> adaptor.tryDeserialize(value, generator));
	}
	
	@Test
	public void testTryDeserializeWithFactorableDisambiguated() throws Exception {
		SerializedObject value = new SerializedObject(FactorableDisambiguated.class);
		value.addField(new SerializedField(new FieldSignature(FactorableDisambiguated.class, int.class, "a"), literal(1)));
		value.addField(new SerializedField(new FieldSignature(FactorableDisambiguated.class, int.class, "b"), literal(2)));
		
		context.getTypes().registerImport(FactorableDisambiguated.class);
		Deserializer generator = generator();

		Computation deserialized = adaptor.tryDeserialize(value, generator);
		assertThat(deserialized.getStatements()).containsExactly("FactorableDisambiguated factorableDisambiguated1 = FactorableDisambiguated.build(1, 2);");
		assertThat(deserialized.getValue()).isEqualTo("factorableDisambiguated1");
	}
	
	@Test
	public void testTryDeserializeWithExternalFactorable() throws Exception {
		SerializedObject value = new SerializedObject(FactorableWithExternalHint.class);
		value.addField(new SerializedField(new FieldSignature(FactorableWithExternalHint.class, int.class, "a"), literal(1)));
		value.addField(new SerializedField(new FieldSignature(FactorableWithExternalHint.class, String.class, "b"), literal("2")));

		context.getTypes().registerImport(FactorableWithExternalHint.class);
		context.addHint(FactorableWithExternalHint.class, new Factory() {
			
			@Override
			public Class<? extends Annotation> annotationType() {
				return Factory.class;
			}
			
			@Override
			public String method() {
				return "build";
			}
			
			@Override
			public Class<?> clazz() {
				return ExternalFactory.class;
			}
		});
		Deserializer generator = generator();

		Computation deserialized = adaptor.tryDeserialize(value, generator);
		assertThat(deserialized.getStatements()).containsExactly("FactorableWithExternalHint factorableWithExternalHint1 = ExternalFactory.build(1, \"2\");");
		assertThat(deserialized.getValue()).isEqualTo("factorableWithExternalHint1");
	}

	private Deserializer generator() {
		return new SetupGenerators(new Adaptors().load(config.loadConfigurations(SetupGenerator.class))).newGenerator(context);
	}

	@Factory(clazz = MyFactory.class, method = "build")
	public static class Factorable {
		private int a;
		private String b;

		@Override
		public String toString() {
			return a + ":" + b;
		}
	}

	public static class MyFactory {

		public static Factorable build(int a, String b) {
			Factorable factorable = new Factorable();
			factorable.a = a;
			factorable.b = b;
			return factorable;
		}

		public static Factorable build(String b) {
			Factorable factorable = new Factorable();
			factorable.b = b;
			return factorable;
		}

		public static Factorable build(int a) {
			Factorable factorable = new Factorable();
			factorable.a = a;
			return factorable;
		}

		public static Factorable build() {
			Factorable factorable = new Factorable();
			return factorable;
		}
	}

	@Factory(clazz = NotFactorableNeedingDisambiguation.class, method = "build")
	public static class NotFactorableNeedingDisambiguation {
		private int a;
		private int b;

		@Override
		public String toString() {
			return a + ":" + b;
		}
		
		public static NotFactorableNeedingDisambiguation build(int a, int b) {
			NotFactorableNeedingDisambiguation factorable = new NotFactorableNeedingDisambiguation();
			factorable.a = a;
			factorable.b = b;
			return factorable;
		}

	}

	@Factory(clazz = FactorableDisambiguated.class, method = "build")
	public static class FactorableDisambiguated {
		private int a;
		private int b;
		
		@Override
		public String toString() {
			return a + ":" + b;
		}
		
		public static FactorableDisambiguated build(@Name("a")int a, @Name("b")int b) {
			FactorableDisambiguated factorable = new FactorableDisambiguated();
			factorable.a = a;
			factorable.b = b;
			return factorable;
		}

	}
	
	public static class NotFactorable {
		private int a;
		private String b;
		
		@Override
		public String toString() {
			return a + ":" + b;
		}
	}
	
	public static class FactorableWithExternalHint {
		private int a;
		private String b;

		@Override
		public String toString() {
			return a + ":" + b;
		}
	}

	public static class ExternalFactory {
		
		public static FactorableWithExternalHint build(int a, String b) {
			FactorableWithExternalHint factorable = new FactorableWithExternalHint();
			factorable.a = a;
			factorable.b = b;
			return factorable;
		}

	}

}
