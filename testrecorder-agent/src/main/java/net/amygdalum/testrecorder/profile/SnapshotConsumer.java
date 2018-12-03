package net.amygdalum.testrecorder.profile;

import net.amygdalum.testrecorder.ExtensionPoint;
import net.amygdalum.testrecorder.ExtensionStrategy;
import net.amygdalum.testrecorder.types.ContextSnapshot;

@ExtensionPoint(strategy=ExtensionStrategy.OVERRIDING)
public interface SnapshotConsumer {

	void accept(ContextSnapshot snapshot);

}
