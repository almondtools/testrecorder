package net.amygdalum.testrecorder;

import java.util.HashSet;
import java.util.Set;

public final class Recorder {

	private static final Set<String> RECORDER_CLASSES = computeRecorderClassNames(
		SnapshotInstrumentor.class,
		SnapshotManager.class
		);
	
	private Recorder() {
	}

	private static Set<String> computeRecorderClassNames(Class<?>...classes) {
		Set<String> classNames = new HashSet<>();
		for (Class<?> clazz : classes) {
			classNames.add(clazz.getName());
			for (Class<?> memberClazz : clazz.getDeclaredClasses()) {
				classNames.add(memberClazz.getName());
			}
		}
		return classNames;
	}

	public static boolean isRecording() {
		StackTraceElement[] stackTrace = new Throwable().getStackTrace();
		for (StackTraceElement stackTraceElement : stackTrace) {
			if (RECORDER_CLASSES.contains(stackTraceElement.getClassName())) {
				return true;
			}
		}
		return false;
	}

}
