package net.amygdalum.testrecorder.deserializers.matcher;

import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.values.SerializedMap;

public class DefaultMapAdaptorTest {

	private DefaultMapAdaptor adaptor;
	private DeserializerContext context;

	@BeforeEach
	public void before() throws Exception {
		adaptor = new DefaultMapAdaptor();
		context = new DefaultDeserializerContext();
	}
	
	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent()).isNull();
	}

	@Test
	public void testMatchesAnyArray() throws Exception {
		assertThat(adaptor.matches(Object.class)).isTrue();
		assertThat(adaptor.matches(new Object(){}.getClass())).isTrue();
	}

	@Test
	public void testTryDeserializeMap() throws Exception {
		SerializedMap value = new SerializedMap(parameterized(LinkedHashMap.class, null, Integer.class, Integer.class)).withResult(parameterized(Map.class, null, Integer.class, Integer.class));
		value.put(literal(8), literal(15));
		value.put(literal(47), literal(11));
		MatcherGenerators generator = new MatcherGenerators();
		Computation result = adaptor.tryDeserialize(value, generator, context);
		
		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("containsEntries(Integer.class, Integer.class).entry(8, 15).entry(47, 11)");
	}

	@Test
	public void testTryDeserializeEmptyMap() throws Exception {
		SerializedMap value = new SerializedMap(BigInteger[].class);
		MatcherGenerators generator = new MatcherGenerators();
		
		Computation result = adaptor.tryDeserialize(value, generator, context);
		
		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("noEntries(Object.class, Object.class)");
	}


}
