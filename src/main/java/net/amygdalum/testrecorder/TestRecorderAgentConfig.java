package net.amygdalum.testrecorder;

import java.util.List;

public interface TestRecorderAgentConfig extends SerializationProfile {

	public static final Runnable NONE = new Runnable() {
		
		@Override
		public void run() {
		}
	}; 
	
	SnapshotConsumer getSnapshotConsumer();

	long getTimeoutInMillis();

	List<String> getPackages();
	
	Class<? extends Runnable> getInitializer();

}
