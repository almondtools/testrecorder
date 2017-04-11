package net.amygdalum.testrecorder.deserializers.matcher;

import static net.amygdalum.testrecorder.values.SerializedNull.nullInstance;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.values.SerializedNull;

public class DefaultNullAdaptorTest {

	private DefaultNullAdaptor adaptor;

	@Before
	public void before() throws Exception {
		adaptor = new DefaultNullAdaptor();
	}
	
	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent(), nullValue());
	}

	@Test
	public void testMatchesAny() throws Exception {
		assertThat(adaptor.matches(Object.class),is(true));
		assertThat(adaptor.matches(new Object() {}.getClass()),is(true));
	}

	@Test
	public void testTryDeserialize() throws Exception {
		SerializedNull value = nullInstance(String.class);
		MatcherGenerators generator = new MatcherGenerators(getClass());
		
		Computation result = adaptor.tryDeserialize(value, generator);
		
		assertThat(result.getStatements(), empty());
		assertThat(result.getValue(), equalTo("nullValue(String.class)"));
	}

    @Test
    public void testTryDeserializeOnHidden() throws Exception {
        SerializedNull value = nullInstance(PrivateList.class);
        value.setResultType(List.class);
        
        MatcherGenerators generator = new MatcherGenerators(getClass());
        
        Computation result = adaptor.tryDeserialize(value, generator);
        
        assertThat(result.getStatements(), empty());
        assertThat(result.getValue(), equalTo("nullValue(java.util.List.class)"));
    }

    @Test
    public void testTryDeserializeOnReallyHidden() throws Exception {
        SerializedNull value = nullInstance(PrivateList.class);
        value.setResultType(PrivateList.class);
        
        MatcherGenerators generator = new MatcherGenerators(getClass());
        
        Computation result = adaptor.tryDeserialize(value, generator);
        
        assertThat(result.getStatements(), empty());
        assertThat(result.getValue(), equalTo("nullValue()"));
    }

    private static class PrivateList<T> extends ArrayList<T> {
        
    }
}
