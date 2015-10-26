package com.almondtools.testrecorder;

import static com.almondtools.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.almondtools.testrecorder.visitors.Computation;
import com.almondtools.testrecorder.visitors.ImportManager;
import com.almondtools.testrecorder.visitors.LocalVariableNameGenerator;
import com.almondtools.testrecorder.visitors.SerializedValueVisitorFactory;
import com.almondtools.testrecorder.visitors.TestComputationValueVisitor;

public class ValueGeneratorTest {

	private ValueGenerator valueGenerator;

	@Before
	public void before() throws Exception {
		valueGenerator = new ValueGenerator();
	}
	
	@Test
	public void testAccept() throws Exception {
		ValueSnapshot snapshot = new ValueSnapshot(MyClass.class, int.class, "intField");
		snapshot.setValue(literal(int.class, 42));
		
		valueGenerator.accept(snapshot);
		
		assertThat(valueGenerator.valuesFor(MyClass.class), contains(containsString("return 42")));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testMultiAccept() throws Exception {
		ValueSnapshot snapshot1 = new ValueSnapshot(MyClass.class, int.class, "intField");
		snapshot1.setValue(literal(int.class, 42));
		ValueSnapshot snapshot2 = new ValueSnapshot(MyClass.class, int.class, "intField");
		snapshot2.setValue(literal(int.class, 4711));
		
		valueGenerator.accept(snapshot1);
		valueGenerator.accept(snapshot2);
		
		assertThat(valueGenerator.valuesFor(MyClass.class), contains(containsString("return 42"), containsString("return 4711")));
	}
	
	@Test
	public void testSetSerializers() throws Exception {
		valueGenerator.setSerializers(new SerializedValueVisitorFactory() {
			
			@Override
			public SerializedValueVisitor<Computation> create(LocalVariableNameGenerator locals, ImportManager imports) {
				return new TestComputationValueVisitor();
			}
		});
		ValueSnapshot snapshot = new ValueSnapshot(MyClass.class, int.class, "intField");
		snapshot.setValue(literal(int.class, 42));
		
		valueGenerator.accept(snapshot);
		
		assertThat(valueGenerator.valuesFor(MyClass.class), contains(containsString("return (42)")));
	}

	@Test
	public void testValuesForNotFound() throws Exception {
		assertThat(valueGenerator.valuesFor(MyClass.class), empty());
	}

	@Test
	public void testComputeClassName() throws Exception {
		assertThat(valueGenerator.computeClassName(MyClass.class), equalTo("MyClassValues"));
	}

	@Test
	public void testRenderCode() throws Exception {
		ValueSnapshot snapshot1 = new ValueSnapshot(MyClass.class, int.class, "intField");
		snapshot1.setValue(literal(int.class, 42));
		ValueSnapshot snapshot2 = new ValueSnapshot(MyClass.class, String.class, "stringField");
		snapshot2.setValue(literal(String.class, "4711"));
		
		valueGenerator.accept(snapshot1);
		valueGenerator.accept(snapshot2);
		
		assertThat(valueGenerator.renderCode(MyClass.class), allOf(containsString("return 42"), containsString("return \"4711\"")));
		assertThat(valueGenerator.renderCode(MyClass.class), allOf(containsString("public int intField0"), containsString("public String stringField1")));
	}

	@Test
	public void testFromRecorded() throws Exception {
		assertThat(ValueGenerator.fromRecorded(new MyClass()), nullValue());
		assertThat(ValueGenerator.fromRecorded(null), nullValue());
	}

	@SuppressWarnings("unused")
	private static class MyClass {
	
		private int intField;
		private String stringField;
	}

}
