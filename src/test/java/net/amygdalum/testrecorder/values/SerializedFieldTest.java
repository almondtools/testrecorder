package net.amygdalum.testrecorder.values;

import static com.almondtools.conmatch.conventions.EqualityMatcher.satisfiesDefaultEquality;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import net.amygdalum.testrecorder.deserializers.TestValueVisitor;

public class SerializedFieldTest {

	@Test
	public void testGetName() throws Exception {
		assertThat(new SerializedField(null, "field", String.class, literal("stringvalue")).getName(), equalTo("field"));
	}

	@Test
	public void testGetType() throws Exception {
		assertThat(new SerializedField(null, "field", String.class, literal("stringvalue")).getType(), equalTo(String.class));
	}

	@Test
	public void testGetValue() throws Exception {
		assertThat(new SerializedField(null, "field", String.class, literal("stringvalue")).getValue(), equalTo(literal("stringvalue")));
	}

	@Test
	public void testAccept() throws Exception {
		assertThat(new SerializedField(null, "f", String.class, literal("sv"))
			.accept(new TestValueVisitor()), equalTo("field"));
	}

	@Test
	public void testToString() throws Exception {
		assertThat(new SerializedField(Object.class, "f", String.class, literal("sv")).toString(), equalTo("java.lang.String f: sv"));
	}

	@Test
	public void testGetDeclaringClass() throws Exception {
		assertThat(new SerializedField(Object.class, "f", String.class, literal("sv")).getDeclaringClass(), equalTo(Object.class));
	}

	@Test
	public void testEquals() throws Exception {
		assertThat(new SerializedField(Object.class, "f", String.class, literal("sv")), satisfiesDefaultEquality()
			.andEqualTo(new SerializedField(Object.class, "f", String.class, literal("sv")))
			.andNotEqualTo(new SerializedField(String.class, "f", String.class, literal("sv")))
			.andNotEqualTo(new SerializedField(Object.class, "nf", String.class, literal("sv")))
			.andNotEqualTo(new SerializedField(Object.class, "f", Object.class, literal("sv")))
			.andNotEqualTo(new SerializedField(Object.class, "f", String.class, literal("nsv"))));
	}

}
