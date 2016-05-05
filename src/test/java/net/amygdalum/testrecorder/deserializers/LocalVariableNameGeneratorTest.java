package net.amygdalum.testrecorder.deserializers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class LocalVariableNameGeneratorTest {

	@Test
	public void generatesNameForTypes() {
		LocalVariableNameGenerator nameGenerator = new LocalVariableNameGenerator();
		String testName = nameGenerator.fetchName(SimpleEnum.class);
		assertThat(testName, is(equalTo("simpleEnum1")));
	}

	@Test
	public void generatesNameForAnonymousTypes() {
		LocalVariableNameGenerator nameGenerator = new LocalVariableNameGenerator();
		String testName = nameGenerator.fetchName(SimpleEnum.enum1.getClass());
		assertThat(testName, is(equalTo("simpleEnum1")));

		testName = nameGenerator.fetchName(SmartEnum.enum1.getClass());
		assertThat(testName, is(equalTo("smartEnum$1_1")));
	}

}

enum SimpleEnum {
	enum1;
}

enum SmartEnum {
	enum1 {

		@Override
		void overrideMe() {
			// Causes $1 class
		}
	};

	abstract void overrideMe();
}
