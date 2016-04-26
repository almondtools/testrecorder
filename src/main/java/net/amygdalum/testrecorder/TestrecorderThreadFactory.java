package net.amygdalum.testrecorder;

import java.util.concurrent.ThreadFactory;

public class TestrecorderThreadFactory implements ThreadFactory {

	public static final ThreadGroup RECORDING = new ThreadGroup("RECORDING");
	
	private String name;
	private boolean daemon;

	public TestrecorderThreadFactory(boolean daemon, String name) {
		this.daemon = daemon;
		this.name = name;
	}

	@Override
	public Thread newThread(Runnable runnable) {
		Thread thread = new Thread(RECORDING, runnable, name, 0);
		thread.setDaemon(daemon);
		return thread;
	}

}
