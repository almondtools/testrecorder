package net.amygdalum.testrecorder;

public interface SnapshotConsumer {

	void accept(ContextSnapshot snapshot);

}
