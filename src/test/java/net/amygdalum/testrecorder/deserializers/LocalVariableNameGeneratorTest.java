package net.amygdalum.testrecorder.deserializers;

import static net.amygdalum.testrecorder.util.Types.array;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.wildcardExtends;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class LocalVariableNameGeneratorTest {

	private LocalVariableNameGenerator nameGenerator;

	@Before
	public void before() throws Exception {
		nameGenerator = new LocalVariableNameGenerator();
	}
	
	@Test
	public void testFetchName() {
		
		String testName = nameGenerator.fetchName(ClassicEnum.class);
		
		assertThat(testName, equalTo("classicEnum1"));
	}

	@Test
	public void testFetchNameForPrimitive() {
		
		String testName = nameGenerator.fetchName(int.class);
		
		assertThat(testName, equalTo("int1"));
	}

	@Test
	public void testFetchNameForArray() {
		
		String testName = nameGenerator.fetchName(ClassicEnum[].class);
		
		assertThat(testName, equalTo("classicEnumArray1"));
	}

	@Test
	public void testFetchNameForPrimitiveArray() {
		
		String testName = nameGenerator.fetchName(int[].class);
		
		assertThat(testName, equalTo("intArray1"));
	}

	@Test
	public void testFetchNameMultipleTimes() {
		nameGenerator.fetchName(ClassicEnum.class);
		
		String testName = nameGenerator.fetchName(ClassicEnum.class);
		
		assertThat(testName, equalTo("classicEnum2"));
	}

	@Test
	public void testFetchNameForNestedTypes() {
		
		String testName = nameGenerator.fetchName(NestedEnum.ENUM_VALUE.getClass());
		
		assertThat(testName, equalTo("nestedEnum1"));
	}

	@Test
	public void testFetchNameForAnonymousTypes() {
		
		String testName = nameGenerator.fetchName(SmartEnum.ENUM_VALUE.getClass());
		
		assertThat(testName, equalTo("smartEnum$1_1"));
	}

	@Test
	public void testFetchNameForGenericArrayType() {
		
		String testName = nameGenerator.fetchName(array(parameterized(List.class,null, String.class)));
		
		assertThat(testName, equalTo("listArray1"));
	}

	@Test
	public void testFetchNameForParameterizedType() {
		
		String testName = nameGenerator.fetchName(parameterized(List.class,null, Integer.class));
		
		assertThat(testName, equalTo("list1"));
	}
	
	@Test
	public void testFetchNameForBoundedType() {
		
		String testName = nameGenerator.fetchName(wildcardExtends(Collection.class));
		
		assertThat(testName, equalTo("extends_java_util_Collection1"));
	}
	
	@Test
	public void testFetchNameForCustomUnnamedType() {
		
		String testName = nameGenerator.fetchName(new Type() {
			@Override
			public String getTypeName() {
				return "";
			}
		});
		
		assertThat(testName, equalTo("_1"));
	}
	
	enum NestedEnum {
		ENUM_VALUE;
	}

}

enum ClassicEnum {
	ENUM_VALUE;
	
}

enum SmartEnum {
	ENUM_VALUE {

		@Override
		void overrideMe() {
			// Causes $1 class
		}
	};

	abstract void overrideMe();
}
