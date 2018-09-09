package net.amygdalum.testrecorder;

import net.amygdalum.testrecorder.types.ContextSnapshot;

public interface SnapshotConsumer {

	void accept(ContextSnapshot snapshot);

}
