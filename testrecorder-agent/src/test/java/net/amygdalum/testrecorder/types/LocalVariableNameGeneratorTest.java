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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class LocalVariableNameGeneratorTest {

	private LocalVariableNameGenerator nameGenerator;

	@BeforeEach
	void before() throws Exception {
		nameGenerator = new LocalVariableNameGenerator();
	}

	@Nested
	class testFetchName {

		@Nested
		class withType {
			@Test
			void forClass() {
				String testName = nameGenerator.fetchName(ClassicEnum.class);

				assertThat(testName).isEqualTo("classicEnum1");
			}

			@Test
			void forPrimitive() {
				String testName = nameGenerator.fetchName(int.class);

				assertThat(testName).isEqualTo("int1");
			}

			@Test
			void forArray() {
				String testName = nameGenerator.fetchName(ClassicEnum[].class);

				assertThat(testName).isEqualTo("classicEnumArray1");
			}

			@Test
			void forPrimitiveArray() {

				String testName = nameGenerator.fetchName(int[].class);

				assertThat(testName).isEqualTo("intArray1");
			}

			@Test
			void forNestedTypes() {
				String testName = nameGenerator.fetchName(NestedEnum.ENUM_VALUE.getClass());

				assertThat(testName).isEqualTo("nestedEnum1");
			}

			@Test
			void forAnonymousTypes() {

				String testName = nameGenerator.fetchName(SmartEnum.ENUM_VALUE.getClass());

				assertThat(testName).isEqualTo("smartEnum$1_1");
			}

			@Test
			void forGenericArrayType() {

				String testName = nameGenerator.fetchName(array(parameterized(List.class, null, String.class)));

				assertThat(testName).isEqualTo("listArray1");
			}

			@Test
			void forParameterizedType() {
				String testName = nameGenerator.fetchName(parameterized(List.class, null, Integer.class));

				assertThat(testName).isEqualTo("list1");
			}

			@Test
			void forBoundedType() {
				String testName = nameGenerator.fetchName(wildcardExtends(Collection.class));

				assertThat(testName).isEqualTo("extends_java_util_Collection1");
			}

			@Test
			void forCustomUnnamedType() {
				String testName = nameGenerator.fetchName(new Type() {
					@Override
					public String getTypeName() {
						return "";
					}
				});

				assertThat(testName).isEqualTo("_1");
			}

			@Test
			void multipleTimes() {
				nameGenerator.fetchName(ClassicEnum.class);

				String testName = nameGenerator.fetchName(ClassicEnum.class);

				assertThat(testName).isEqualTo("classicEnum2");
			}
		}

		@Nested
		class withName {

			@Test
			void onCommonString() {
				assertThat(nameGenerator.fetchName("var")).isEqualTo("var1");
			}

			@Test
			void onStringWithDigitSuffix() {
				assertThat(nameGenerator.fetchName("var1")).isEqualTo("var1_1");
			}

			@Test
			void onStringEmpty() {
				assertThat(nameGenerator.fetchName("")).isEqualTo("_1");
			}

			@Test
			void withMoreThan10Fetches() {
				IntStream.range(0, 10)
					.mapToObj(i -> nameGenerator.fetchName("var"))
					.toArray(String[]::new);

				String name = nameGenerator.fetchName("var");

				assertThat(name).isEqualTo("var11");
			}

		}

	}

	@Nested
	class testFreeName {
		@Test
		void withMoreThan10Uses() {
			IntStream.range(0, 10)
				.mapToObj(i -> nameGenerator.fetchName("var"))
				.toArray(String[]::new);

			nameGenerator.freeName("var9");
			nameGenerator.freeName("var10");
			String name = nameGenerator.fetchName("var");

			assertThat(name).isEqualTo("var9");
		}

		@Test
		void onFreeNames() {
			nameGenerator.fetchName("var");

			nameGenerator.freeName("var2");

			assertThat(nameGenerator.fetchName("var")).isEqualTo("var2");
		}

		@Test
		void onUnknownNames() {
			nameGenerator.freeName("8");
			nameGenerator.freeName("10");

			String name = nameGenerator.fetchName("");

			assertThat(name).isEqualTo("_1");
		}

		@Test
		void multipleTimes() {
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
		void recycling() {
			nameGenerator.fetchName("var");
			nameGenerator.fetchName("var");

			nameGenerator.freeName("var1");
			String recycled = nameGenerator.fetchName("var");

			String name = nameGenerator.fetchName("var");

			assertThat(recycled).isEqualTo("var1");
			assertThat(name).isEqualTo("var3");
		}
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
