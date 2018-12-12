package net.amygdalum.testrecorder.util;

import java.util.HashSet;
import java.util.Set;

public final class Recorder {

	private static final Set<String> recorderClasses = new HashSet<>();
	
	private Recorder() {
	}

	public static void registerClass(Class<?> clazz) {
		recorderClasses.add(clazz.getName());
		for (Class<?> memberClazz : clazz.getDeclaredClasses()) {
			recorderClasses.add(memberClazz.getName());
		}
	}

	public static boolean isRecording() {
		StackTraceElement[] stackTrace = new Throwable().getStackTrace();
		for (StackTraceElement stackTraceElement : stackTrace) {
			if (recorderClasses.contains(stackTraceElement.getClassName())) {
				return true;
			}
		}
		return false;
	}

}
