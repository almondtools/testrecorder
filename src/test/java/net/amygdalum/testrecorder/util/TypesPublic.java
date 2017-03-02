package net.amygdalum.testrecorder.util;

import net.amygdalum.testrecorder.util.TypesTest.NestedPackagePrivate;
import net.amygdalum.testrecorder.util.TypesTest.NestedProtected;
import net.amygdalum.testrecorder.util.TypesTest.NestedPublic;

public class TypesPublic {
	
	//NestedPrivate privateAccessIsNotAllowedFromPackage;
	NestedPublic publicAccessIsAllowedFromPackage;
	NestedPackagePrivate packagePrivateAccessIsAllowedFromPackage;
	NestedProtected protectedAccessIsAllowedFromPackage;
	
}