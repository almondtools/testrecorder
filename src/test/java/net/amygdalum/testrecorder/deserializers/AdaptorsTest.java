package net.amygdalum.testrecorder.deserializers;

import static net.amygdalum.xrayinterface.XRayInterface.xray;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializationException;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.values.SerializedObject;

public class AdaptorsTest {

	private AgentConfiguration config;
	private Adaptors<TestComputationValueVisitor> adaptors;
	private OpenAdaptors openadaptors;

	@BeforeEach
	public void before() throws Exception {
		config = new AgentConfiguration();
		adaptors = new Adaptors<>(config);
		openadaptors = xray(adaptors).to(OpenAdaptors.class);
	}

	@Test
	public void testAddSingleAdaptor() throws Exception {
		MyAdaptor1 a1 = new MyAdaptor1(null, true, Computation.variable("a1", Object.class));

		adaptors.add(a1);

		assertThat(openadaptors.getAdaptors().get(SerializedObject.class)).containsExactly(a1);
	}

	@Test
	public void testAddOrphanAdaptor() throws Exception {
		MyAdaptor1 a1 = new MyAdaptor1(null, true, Computation.variable("a1", Object.class));
		MyAdaptor2 a2 = new MyAdaptor2(null, true, Computation.variable("a2", Object.class));

		adaptors.add(a1);
		adaptors.add(a2);

		assertThat(openadaptors.getAdaptors().get(SerializedObject.class)).containsExactly(a1, a2);
	}

	@Test
	public void testAddChildAdaptor() throws Exception {
		MyAdaptor1 a1 = new MyAdaptor1(null, true, Computation.variable("a1", Object.class));
		MyAdaptor2 a2 = new MyAdaptor2(MyAdaptor1.class, true, Computation.variable("a2", Object.class));

		adaptors.add(a1);
		adaptors.add(a2);

		assertThat(openadaptors.getAdaptors().get(SerializedObject.class)).containsExactly(a2, a1);
	}

	@Test
	public void testAddParentAdaptor() throws Exception {
		MyAdaptor1 a1 = new MyAdaptor1(MyAdaptor1.class, true, Computation.variable("a1", Object.class));
		MyAdaptor2 a2 = new MyAdaptor2(MyAdaptor1.class, true, Computation.variable("a2", Object.class));

		adaptors.add(a2);
		adaptors.add(a1);

		assertThat(openadaptors.getAdaptors().get(SerializedObject.class)).containsExactly(a2, a1);
	}

	@Test
	public void testAddMultipleAdaptor123() throws Exception {
		MyAdaptor1 a1 = new MyAdaptor1(null, true, Computation.variable("a1", Object.class));
		MyAdaptor2 a2 = new MyAdaptor2(MyAdaptor1.class, true, Computation.variable("a2", Object.class));
		MyAdaptor3 a3 = new MyAdaptor3(MyAdaptor2.class, true, Computation.variable("a3", Object.class));

		adaptors.add(a1);
		adaptors.add(a2);
		adaptors.add(a3);

		assertThat(openadaptors.getAdaptors().get(SerializedObject.class)).containsExactly(a3, a2, a1);
	}

	@Test
	public void testAddMultipleAdaptor132() throws Exception {
		MyAdaptor1 a1 = new MyAdaptor1(null, true, Computation.variable("a1", Object.class));
		MyAdaptor2 a2 = new MyAdaptor2(MyAdaptor1.class, true, Computation.variable("a2", Object.class));
		MyAdaptor3 a3 = new MyAdaptor3(MyAdaptor2.class, true, Computation.variable("a3", Object.class));

		adaptors.add(a1);
		adaptors.add(a3);
		adaptors.add(a2);

		assertThat(openadaptors.getAdaptors().get(SerializedObject.class)).containsExactly(a3, a2, a1);
	}

	@Test
	public void testAddMultipleAdaptor321() throws Exception {
		MyAdaptor1 a1 = new MyAdaptor1(null, true, Computation.variable("a1", Object.class));
		MyAdaptor2 a2 = new MyAdaptor2(MyAdaptor1.class, true, Computation.variable("a2", Object.class));
		MyAdaptor3 a3 = new MyAdaptor3(MyAdaptor2.class, true, Computation.variable("a3", Object.class));

		adaptors.add(a3);
		adaptors.add(a2);
		adaptors.add(a1);

		assertThat(openadaptors.getAdaptors().get(SerializedObject.class)).containsExactly(a3, a2, a1);
	}

	@Test
	public void testAddMultipleAdaptor4321() throws Exception {
		MyAdaptor1 a1 = new MyAdaptor1(null, true, Computation.variable("a1", Object.class));
		MyAdaptor2 a2 = new MyAdaptor2(MyAdaptor1.class, true, Computation.variable("a2", Object.class));
		MyAdaptor3 a3 = new MyAdaptor3(MyAdaptor2.class, true, Computation.variable("a3", Object.class));
		MyAdaptor4 a4 = new MyAdaptor4(MyAdaptor3.class, true, Computation.variable("a3", Object.class));

		adaptors.add(a4);
		adaptors.add(a3);
		adaptors.add(a2);
		adaptors.add(a1);

		assertThat(openadaptors.getAdaptors().get(SerializedObject.class)).containsExactly(a4, a3, a2, a1);
	}

	@Test
	public void testAddMultipleAdaptorNoTotalOrder() throws Exception {
		MyAdaptor1 a1 = new MyAdaptor1(null, true, Computation.variable("a1", Object.class));
		MyAdaptor2 a2 = new MyAdaptor2(MyAdaptor1.class, true, Computation.variable("a2", Object.class));
		MyAdaptor4 a4 = new MyAdaptor4(MyAdaptor3.class, true, Computation.variable("a3", Object.class));
		
		adaptors.add(a1);
		adaptors.add(a2);
		adaptors.add(a4);
		
		assertThat(openadaptors.getAdaptors().get(SerializedObject.class)).containsExactly(a4, a2, a1);
	}
	
	private abstract class MyAbstractAdaptor implements Adaptor<SerializedObject, TestComputationValueVisitor> {

		private Class<? extends Adaptor<SerializedObject, TestComputationValueVisitor>> parent;
		private boolean matches;
		private Computation computation;

		public MyAbstractAdaptor(Class<? extends Adaptor<SerializedObject, TestComputationValueVisitor>> parent, boolean matches, Computation computation) {
			super();
			this.parent = parent;
			this.matches = matches;
			this.computation = computation;
		}

		@Override
		public Class<? extends Adaptor<SerializedObject, TestComputationValueVisitor>> parent() {
			return parent;
		}

		@Override
		public Class<? extends SerializedValue> getAdaptedClass() {
			return SerializedObject.class;
		}

		@Override
		public boolean matches(Type type) {
			return matches;
		}

		@Override
		public Computation tryDeserialize(SerializedObject value, TestComputationValueVisitor generator, DeserializerContext context) throws DeserializationException {
			return computation;
		}

	}

	private class MyAdaptor1 extends MyAbstractAdaptor {

		public MyAdaptor1(Class<? extends Adaptor<SerializedObject, TestComputationValueVisitor>> parent, boolean matches, Computation computation) {
			super(parent, matches, computation);
		}

	}

	private class MyAdaptor2 extends MyAbstractAdaptor {

		public MyAdaptor2(Class<? extends Adaptor<SerializedObject, TestComputationValueVisitor>> parent, boolean matches, Computation computation) {
			super(parent, matches, computation);
		}

	}

	private class MyAdaptor3 extends MyAbstractAdaptor {

		public MyAdaptor3(Class<? extends Adaptor<SerializedObject, TestComputationValueVisitor>> parent, boolean matches, Computation computation) {
			super(parent, matches, computation);
		}

	}

	private class MyAdaptor4 extends MyAbstractAdaptor {

		public MyAdaptor4(Class<? extends Adaptor<SerializedObject, TestComputationValueVisitor>> parent, boolean matches, Computation computation) {
			super(parent, matches, computation);
		}

	}
	interface OpenAdaptors {
		Map<Class<? extends SerializedValue>, List<Adaptor<?, TestComputationValueVisitor>>> getAdaptors();
	}
}
