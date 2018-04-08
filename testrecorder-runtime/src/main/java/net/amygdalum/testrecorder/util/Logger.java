package net.amygdalum.testrecorder.util;

import java.io.PrintStream;

public class Logger {

	private static Logger INFO = new Logger(System.out);
	private static Logger WARN = new Logger(System.out);
	private static Logger ERROR = new Logger(System.err);

	private PrintStream out;

	public Logger(PrintStream out) {
		this.out = out;
	}

	public static void setINFO(Logger info) {
		INFO = info;
	}

	public static void resetINFO() {
		INFO = new Logger(System.out);
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
		WARN = new Logger(System.out);
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
		ERROR = new Logger(System.err);
	}

	public static void error(Object... msgs) {
		for (Object msg : msgs) {
			ERROR.log(msg);
		}
	}

	public void log(Object msg) {
		out.println(msg);
		if (msg instanceof Exception) {
			((Exception) msg).printStackTrace(out);
		}
	}

}
