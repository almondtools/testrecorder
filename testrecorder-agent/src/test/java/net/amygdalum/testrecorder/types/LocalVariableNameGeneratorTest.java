package net.amygdalum.testrecorder.types;

import static net.amygdalum.testrecorder.util.Types.array;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.wildcardExtends;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LocalVariableNameGeneratorTest {

	private LocalVariableNameGenerator nameGenerator;

	@BeforeEach
	void before() throws Exception {
		nameGenerator = new LocalVariableNameGenerator();
	}

	@Test
	void testFetchName() {
		String testName = nameGenerator.fetchName(ClassicEnum.class);

		assertThat(testName).isEqualTo("classicEnum1");
	}

	@Test
	void testFetchNameForPrimitive() {
		String testName = nameGenerator.fetchName(int.class);

		assertThat(testName).isEqualTo("int1");
	}

	@Test
	void testFetchNameForArray() {
		String testName = nameGenerator.fetchName(ClassicEnum[].class);

		assertThat(testName).isEqualTo("classicEnumArray1");
	}

	@Test
	void testFetchNameForPrimitiveArray() {

		String testName = nameGenerator.fetchName(int[].class);

		assertThat(testName).isEqualTo("intArray1");
	}

	@Test
	void testFetchNameMultipleTimes() {
		nameGenerator.fetchName(ClassicEnum.class);

		String testName = nameGenerator.fetchName(ClassicEnum.class);

		assertThat(testName).isEqualTo("classicEnum2");
	}

	@Test
	void testFetchNameForNestedTypes() {
		String testName = nameGenerator.fetchName(NestedEnum.ENUM_VALUE.getClass());

		assertThat(testName).isEqualTo("nestedEnum1");
	}

	@Test
	void testFetchNameForAnonymousTypes() {

		String testName = nameGenerator.fetchName(SmartEnum.ENUM_VALUE.getClass());

		assertThat(testName).isEqualTo("smartEnum$1_1");
	}

	@Test
	void testFetchNameForGenericArrayType() {

		String testName = nameGenerator.fetchName(array(parameterized(List.class, null, String.class)));

		assertThat(testName).isEqualTo("listArray1");
	}

	@Test
	void testFetchNameForParameterizedType() {
		String testName = nameGenerator.fetchName(parameterized(List.class, null, Integer.class));

		assertThat(testName).isEqualTo("list1");
	}

	@Test
	void testFetchNameForBoundedType() {
		String testName = nameGenerator.fetchName(wildcardExtends(Collection.class));

		assertThat(testName).isEqualTo("extends_java_util_Collection1");
	}

	@Test
	void testFetchNameForCustomUnnamedType() {
		String testName = nameGenerator.fetchName(new Type() {
			@Override
			public String getTypeName() {
				return "";
			}
		});

		assertThat(testName).isEqualTo("_1");
	}

	@Test
	void testNameString() {
		assertThat(nameGenerator.fetchName("var")).isEqualTo("var1");
	}

	@Test
	void testNameStringWithDigitSuffix() {
		assertThat(nameGenerator.fetchName("var1")).isEqualTo("var1_1");
	}

	@Test
	void testNameStringEmpty() {
		assertThat(nameGenerator.fetchName("")).isEqualTo("_1");
	}

	@Test
	void testNameWithMoreThan10Uses() {
		IntStream.range(0, 10)
			.mapToObj(i -> nameGenerator.fetchName("var"))
			.toArray(String[]::new);

		String name = nameGenerator.fetchName("var");

		assertThat(name).isEqualTo("var11");
	}

	@Test
	void testFreeNameWithMoreThan10Uses() {
		IntStream.range(0, 10)
			.mapToObj(i -> nameGenerator.fetchName("var"))
			.toArray(String[]::new);

		nameGenerator.freeName("var9");
		nameGenerator.freeName("var10");
		String name = nameGenerator.fetchName("var");

		assertThat(name).isEqualTo("var9");
	}

	@Test
	void testFreeNameFreeNames() {
		nameGenerator.fetchName("var");

		nameGenerator.freeName("var2");

		assertThat(nameGenerator.fetchName("var")).isEqualTo("var2");
	}

	@Test
	void testFreeNameUnknownNames() {
		nameGenerator.freeName("8");
		nameGenerator.freeName("10");

		String name = nameGenerator.fetchName("");

		assertThat(name).isEqualTo("_1");
	}

	@Test
	void testFreeNameMultipleTimes() {
		IntStream.range(0, 10)
			.mapToObj(i -> nameGenerator.fetchName("var"))
			.toArray(String[]::new);

		nameGenerator.freeName("var3");
		nameGenerator.freeName("var9");
		nameGenerator.freeName("var10");
		String name = nameGenerator.fetchName("var");

		assertThat(name).isEqualTo("var3");
	}

	@Test
	void testFetchNameRecycling() {
		nameGenerator.fetchName("var");
		nameGenerator.fetchName("var");
		
		nameGenerator.freeName("var1");
		nameGenerator.fetchName("var");

		String name = nameGenerator.fetchName("var");
		
		assertThat(name).isEqualTo("var3");
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
