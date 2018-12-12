package net.amygdalum.testrecorder.generator;

import static net.amygdalum.testrecorder.extensionpoint.ExtensionStrategy.OVERRIDING;

import java.util.List;

import net.amygdalum.testrecorder.deserializers.CustomAnnotation;
import net.amygdalum.testrecorder.extensionpoint.ExtensionPoint;

@ExtensionPoint(strategy=OVERRIDING)
public interface TestGeneratorProfile {

	List<CustomAnnotation> annotations();
	
}
