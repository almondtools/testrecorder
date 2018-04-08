package net.amygdalum.testrecorder.runtime;

public final class Throwables {
	
	private Throwables() {
	}

	public static Throwable capture(WithResult<?> code) {
		return capture(code, Throwable.class);
	}
	
	public static <T extends Throwable> T capture(WithResult<?> code, Class<T> clazz) {
		try {
			code.run();
			return null;
		} catch (Throwable exception) {
			if (clazz.isInstance(exception)) {
				return clazz.cast(exception);
			} else {
				return null;
			}
		}
	}
	
	public static Throwable capture(WithoutResult code) {
		return capture(code, Throwable.class);
	}
	
	public static <T extends Throwable> T capture(WithoutResult code, Class<T> clazz) {
		try {
			code.run();
			return null;
		} catch (Throwable exception) {
			if (clazz.isInstance(exception)) {
				return clazz.cast(exception);
			} else {
				return null;
			}
		}
	}
	
	
	public interface WithResult<T> {
		T run() throws Throwable;
	}
	
	public interface WithoutResult {
		void run() throws Throwable;
	}
}
