package net.amygdalum.testrecorder.util;

import java.io.PrintStream;

public class Logger {

	private static Logger DEBUG = debugLogger();
	private static Logger INFO = infoLogger();
	private static Logger WARN = warnLogger();
	private static Logger ERROR = errorLogger();

	private PrintStream[] out;

	public Logger(PrintStream... out) {
		this.out = out;
	}

	private static Logger debugLogger() {
		return new Logger();
	}

	private static Logger infoLogger() {
		return new Logger(System.out);
	}

	private static Logger warnLogger() {
		return new Logger(System.out);
	}

	private static Logger errorLogger() {
		return new Logger(System.err);
	}

	public static void setDEBUG(Logger debug) {
		DEBUG = debug;
	}

	public static void resetDEBUG() {
		DEBUG = warnLogger();
	}

	public static void debug(Object... msgs) {
		for (Object msg : msgs) {
			DEBUG.log(msg);
		}
	}

	public static void setINFO(Logger info) {
		INFO = info;
	}

	public static void resetINFO() {
		INFO = warnLogger();
	}

	public static void info(Object... msgs) {
		for (Object msg : msgs) {
			INFO.log(msg);
		}
	}

	public static void setWARN(Logger warn) {
		WARN = warn;
	}

	public static void resetWARN() {
		WARN = warnLogger();
	}

	public static void warn(Object... msgs) {
		for (Object msg : msgs) {
			WARN.log(msg);
		}
	}

	public static void setERROR(Logger error) {
		ERROR = error;
	}

	public static void resetERROR() {
		ERROR = errorLogger();
	}

	public static void error(Object... msgs) {
		for (Object msg : msgs) {
			ERROR.log(msg);
		}
	}

	public void log(Object msg) {
		for (int i = 0; i < out.length; i++) {
			out[i].println(msg);
			if (msg instanceof Exception) {
				((Exception) msg).printStackTrace(out[i]);
			}
		}
	}

}
