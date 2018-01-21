package net.amygdalum.testrecorder;

public class TestTestRecorderAgentConfig extends DefaultTestRecorderAgentConfig implements TestRecorderAgentConfig {

	private SnapshotConsumer consumer;

	public TestTestRecorderAgentConfig(SnapshotConsumer consumer) {
		this.consumer = consumer;
	}
	
	@Override
	public SnapshotConsumer getSnapshotConsumer() {
		return consumer;
	}
}
