package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.FieldSignature;
import net.amygdalum.testrecorder.types.SerializedField;
import net.amygdalum.testrecorder.util.testobjects.Bean;
import net.amygdalum.testrecorder.util.testobjects.Simple;

public class SetterParamTest {

	private SetterParam setterParam;

	@BeforeEach
	public void before() throws Exception {
		Method setter = Bean.class.getDeclaredMethod("setAttribute", String.class);
		FieldSignature field = new FieldSignature(Simple.class, String.class, "attribute");
		setterParam = new SetterParam(setter, String.class, new SerializedField(field, literal("value")), "value");
	}

	@Test
	public void testSetterParam() throws Exception {
		assertThat(setterParam.getField().getName()).isEqualTo("attribute");
		assertThat(setterParam.getName()).isEqualTo("setAttribute");
		assertThat(setterParam.getValue()).isEqualTo("value");
		assertThat(setterParam.computeSerializedValue()).isEqualTo(literal("value"));
	}

	@Test
	public void testApply() throws Exception {
		Bean object = new Bean();
		setterParam.apply(object);

		assertThat(object.getAttribute()).isEqualTo("value");
	}

	@Test
	public void testToString() throws Exception {
		assertThat(setterParam.toString()).isEqualTo("public void net.amygdalum.testrecorder.util.testobjects.Bean.setAttribute(java.lang.String)=value=> attribute");
	}

}
