package net.amygdalum.testrecorder.profile;

import net.amygdalum.testrecorder.extensionpoint.ExtensionPoint;
import net.amygdalum.testrecorder.extensionpoint.ExtensionStrategy;
import net.amygdalum.testrecorder.types.ContextSnapshot;

@ExtensionPoint(strategy=ExtensionStrategy.OVERRIDING)
public interface SnapshotConsumer {

	void accept(ContextSnapshot snapshot);

}
