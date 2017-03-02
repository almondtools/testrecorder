package net.amygdalum.testrecorder.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import net.amygdalum.testrecorder.util.TypesTest.NestedPackagePrivate;
import net.amygdalum.testrecorder.util.TypesTest.NestedProtected;
import net.amygdalum.testrecorder.util.TypesTest.NestedPublic;

public class TypesTest {

	@Test
	public void testIsHiddenTrueForPrivate() throws Exception {
		assertThat(Types.isHidden(NestedPrivate.class, "any"), is(true));
	}

	@Test
	public void testIsHiddenFalseForNestedPackagePrivate() throws Exception {
		assertThat(Types.isHidden(NestedPackagePrivate.class, "net.amygdalum.testrecorder.util"), is(false));
	}

	@Test
	public void testIsHiddenFalseForNestedProtected() throws Exception {
		assertThat(Types.isHidden(NestedProtected.class, "net.amygdalum.testrecorder.util"), is(false));
	}

	@Test
	public void testIsHiddenFalseForPackagePrivateInSamePackage() throws Exception {
		assertThat(Types.isHidden(TypesPackagePrivate.class, "net.amygdalum.testrecorder.util"), is(false));
		assertThat(Types.isHidden(TypesPackagePrivate.class, "other"), is(true));
	}

	@Test
	public void testIsHiddenFalseForNestedPublic() throws Exception {
		assertThat(Types.isHidden(NestedPublic.class, "any"), is(false));
	}

	@Test
	public void testIsHiddenFalseForPublic() throws Exception {
		assertThat(Types.isHidden(TypesPublic.class, "net.amygdalum.testrecorder.util"), is(false));
		assertThat(Types.isHidden(TypesPublic.class, "other"), is(false));
	}

	public static class NestedPublic {
	}

	private static class NestedPrivate {
	}

	static class NestedPackagePrivate {
	}

	protected static class NestedProtected {
	}
}

class TypesPackagePrivate {
	//NestedPrivate privateAccessIsNotAllowedFromPackage;
	NestedPublic publicAccessIsAllowedFromPackage;
	NestedPackagePrivate packagePrivateAccessIsAllowedFromPackage;
	NestedProtected protectedAccessIsAllowedFromPackage;
	
}