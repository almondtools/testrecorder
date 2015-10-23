package com.almondtools.testrecorder.scenarios;

import static com.almondtools.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

import com.almondtools.testrecorder.DefaultConfig;
import com.almondtools.testrecorder.SnapshotInstrumentor;
import com.almondtools.testrecorder.ValueGenerator;

public class FieldsTest {

	private static SnapshotInstrumentor instrumentor;

	@BeforeClass
	public static void beforeClass() throws Exception {
		instrumentor = new SnapshotInstrumentor(new DefaultConfig());
		instrumentor.register("com.almondtools.testrecorder.scenarios.Fields");
	}
	
	@Test
	public void testAssertsInEachTest() throws Exception {
		Fields fields = new Fields();
		fields.all(4);

		assertThat(fields.getB(), equalTo((byte) 3));
		assertThat(fields.getS(), equalTo((short) 4));
		assertThat(fields.getI(), equalTo(9));
		assertThat(fields.getL(), equalTo(10l));
		assertThat(fields.getC(), equalTo('d'));
		assertThat(fields.isZ(), is(false));
		assertThat((double) fields.getF(), closeTo(4, 0.001));
		assertThat(fields.getD(), closeTo(27, 0.001));
		assertThat(fields.getO(), equalTo("s:3"));
	}

	@Test
	public void testCompilable() throws Exception {
		Fields fields = new Fields();
		fields.all(4);

		ValueGenerator valueGenerator = ValueGenerator.fromRecorded(fields);
		assertThat(valueGenerator.valuesFor(Fields.class), hasSize(9 * 4));
		assertThat(valueGenerator.renderCode(Fields.class), compiles());
	}
		
	@Test
	public void testB() throws Exception {
		Fields fields = new Fields();
		fields.b(4);

		ValueGenerator valueGenerator = ValueGenerator.fromRecorded(fields);
		assertThat(valueGenerator.valuesFor(Fields.class), hasSize(1));
		assertThat(valueGenerator.renderCode(Fields.class), containsString("return (byte) 4"));
	}
		
	@Test
	public void testS() throws Exception {
		Fields fields = new Fields();
		fields.s(4);

		ValueGenerator valueGenerator = ValueGenerator.fromRecorded(fields);
		assertThat(valueGenerator.valuesFor(Fields.class), hasSize(1));
		assertThat(valueGenerator.renderCode(Fields.class), containsString("return (short) 5"));
	}
		
	@Test
	public void testI() throws Exception {
		Fields fields = new Fields();
		fields.i(4);

		ValueGenerator valueGenerator = ValueGenerator.fromRecorded(fields);
		assertThat(valueGenerator.valuesFor(Fields.class), hasSize(1));
		assertThat(valueGenerator.renderCode(Fields.class), containsString("return 16"));
	}
		
	@Test
	public void testL() throws Exception {
		Fields fields = new Fields();
		fields.l(4);
		
		ValueGenerator valueGenerator = ValueGenerator.fromRecorded(fields);
		assertThat(valueGenerator.valuesFor(Fields.class), hasSize(1));
		assertThat(valueGenerator.renderCode(Fields.class), containsString("return 17"));
	}
	
	@Test
	public void testC() throws Exception {
		Fields fields = new Fields();
		fields.c(0);
		
		ValueGenerator valueGenerator = ValueGenerator.fromRecorded(fields);
		assertThat(valueGenerator.valuesFor(Fields.class), hasSize(1));
		assertThat(valueGenerator.renderCode(Fields.class), containsString("return 'a'"));
	}
	
	@Test
	public void testZ() throws Exception {
		Fields fields = new Fields();
		fields.z(0);
		
		ValueGenerator valueGenerator = ValueGenerator.fromRecorded(fields);
		assertThat(valueGenerator.valuesFor(Fields.class), hasSize(1));
		assertThat(valueGenerator.renderCode(Fields.class), containsString("return true"));
	}
	
	@Test
	public void testF() throws Exception {
		Fields fields = new Fields();
		fields.f(4);
		
		ValueGenerator valueGenerator = ValueGenerator.fromRecorded(fields);
		assertThat(valueGenerator.valuesFor(Fields.class), hasSize(1));
		assertThat(valueGenerator.renderCode(Fields.class), containsString("return 27.0f"));
	}
	
	@Test
	public void testD() throws Exception {
		Fields fields = new Fields();
		fields.d(4);
		
		ValueGenerator valueGenerator = ValueGenerator.fromRecorded(fields);
		assertThat(valueGenerator.valuesFor(Fields.class), hasSize(1));
		assertThat(valueGenerator.renderCode(Fields.class), containsString("return 256.0"));
	}
	
	@Test
	public void testO() throws Exception {
		Fields fields = new Fields();
		fields.o(4);
		
		ValueGenerator valueGenerator = ValueGenerator.fromRecorded(fields);
		assertThat(valueGenerator.valuesFor(Fields.class), hasSize(1));
		assertThat(valueGenerator.renderCode(Fields.class), containsString("return \"s:4\""));
	}
	
	@Test
	public void testMulti() throws Exception {
		Fields fields = new Fields();
		fields.b(1);
		fields.b(2);
		fields.b(3);
		
		ValueGenerator valueGenerator = ValueGenerator.fromRecorded(fields);
		assertThat(valueGenerator.valuesFor(Fields.class), hasSize(3));
		assertThat(valueGenerator.renderCode(Fields.class), containsString("return (byte) 1"));
		assertThat(valueGenerator.renderCode(Fields.class), containsString("return (byte) 2"));
		assertThat(valueGenerator.renderCode(Fields.class), containsString("return (byte) 3"));
	}
	
	@Test
	public void testMultiDifferent() throws Exception {
		Fields fields = new Fields();
		fields.b(1);
		fields.s(2);
		fields.b(3);
		
		ValueGenerator valueGenerator = ValueGenerator.fromRecorded(fields);
		assertThat(valueGenerator.valuesFor(Fields.class), hasSize(3));
		assertThat(valueGenerator.renderCode(Fields.class), containsString("return (byte) 1"));
		assertThat(valueGenerator.renderCode(Fields.class), containsString("return (short) 3"));
		assertThat(valueGenerator.renderCode(Fields.class), containsString("return (byte) 3"));
	}
	
}