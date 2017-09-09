package net.amygdalum.testrecorder.profile;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import net.amygdalum.testrecorder.Classes;
import net.amygdalum.testrecorder.util.testobjects.Simple;

public class ClassesByPackageTest {

	@Test
	public void testMatchesReflectiveClass() throws Exception {
		assertThat(Classes.byPackage("net.amygdalum.testrecorder.util.testobjects").matches(Simple.class), is(true)); 
		assertThat(Classes.byPackage("net.amygdalum.testrecorder.util").matches(Simple.class), is(true)); 
		assertThat(Classes.byPackage("net.amygdalum.testrecorder.util.scenarios").matches(Simple.class), is(false)); 
	}

	@Test
	public void testMatchesClassDescriptor() throws Exception {
		assertThat(Classes.byPackage("net.amygdalum.testrecorder.util.testobjects").matches("net/amygdalum/testrecorder/util/testobjects/Simple"), is(true)); 
		assertThat(Classes.byPackage("net.amygdalum.testrecorder.util").matches("net/amygdalum/testrecorder/util/testobjects/Simple"), is(true)); 
		assertThat(Classes.byPackage("net.amygdalum.testrecorder.util.scenarios").matches("net/amygdalum/testrecorder/util/testobjects/Simple"), is(false));
	}

}
