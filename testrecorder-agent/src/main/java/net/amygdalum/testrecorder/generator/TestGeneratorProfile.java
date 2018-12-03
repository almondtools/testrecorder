package net.amygdalum.testrecorder.generator;

import static net.amygdalum.testrecorder.ExtensionStrategy.OVERRIDING;

import java.util.List;

import net.amygdalum.testrecorder.ExtensionPoint;
import net.amygdalum.testrecorder.deserializers.CustomAnnotation;

@ExtensionPoint(strategy=OVERRIDING)
public interface TestGeneratorProfile {

	List<CustomAnnotation> annotations();
	
}
