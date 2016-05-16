package net.amygdalum.testrecorder.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TypesTest {

	@Test
	public void testIsHiddenFalseForPublic() throws Exception {
		assertThat(Types.isHidden(String.class, "any"), is(false));
		assertThat(Types.isHidden(NestedPublic.class, "any"), is(false));
	}

	@Test
	public void testIsHiddenTrueForPrivate() throws Exception {
		assertThat(Types.isHidden(NestedPrivate.class, "any"), is(true));
	}
	
	@Test
	public void testIsHiddenTrueForNestedPackagePrivate() throws Exception {
		assertThat(Types.isHidden(NestedPackagePrivate.class, "net.amygdalum.testrecorder.util"), is(true));
	}
	
	@Test
	public void testIsHiddenFalseForPackagePrivateInSamePackage() throws Exception {
		assertThat(Types.isHidden(PackagePrivate.class, "net.amygdalum.testrecorder.util"), is(false));
		assertThat(Types.isHidden(PackagePrivate.class, "other"), is(true));
	}

	public static class NestedPublic {
	}

	private static class NestedPrivate {
	}

	static class NestedPackagePrivate {
	}
}

class PackagePrivate {
	
}