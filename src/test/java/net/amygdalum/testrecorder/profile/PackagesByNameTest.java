package net.amygdalum.testrecorder.profile;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.Packages;
import net.amygdalum.testrecorder.util.testobjects.Simple;

public class PackagesByNameTest {

	private Packages packagesByName;

	@Before
	public void before() {
		packagesByName = Packages.byName("net.amygdalum.testrecorder.util.testobjects");
	}
	
	@Test
	public void testMatchesReflectivePackage() throws Exception {
		assertThat(packagesByName.matches(Simple.class.getPackage()), is(true)); 
		assertThat(packagesByName.matches(PackagesByName.class.getPackage()), is(false)); 
	}

	@Test
	public void testMatchesPackageName() throws Exception {
		assertThat(packagesByName.matches("net.amygdalum.testrecorder.util.testobjects"), is(true)); 
		assertThat(packagesByName.matches("net.amygdalum.testrecorder.util"), is(false)); 
		assertThat(packagesByName.matches("net.amygdalum.testrecorder.util.testobjects.subpackage"), is(false)); 
	}

}
