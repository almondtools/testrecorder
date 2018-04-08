package net.amygdalum.testrecorder;

public class Example {
	public void noResultNoArgs() {
	}

	public boolean primitiveResultNoArgs() {
		return true;
	}

	public ResultObject objectResultNoArgs() {
		return null;
	}

	public void noResultPrimitiveArg(int i) {
	}

	public void noResultArrayArg(char[] c) {
	}

	public void noResultObjectArrayArg(String[] s) {
	}

	public void noResultObjectArg(ArgumentObject o) {
	}

	public ResultObject objectResultMixedArgs(double d, ArgumentObject o) {
		return null;
	}

	public static long staticPrimitiveResultMixedArgs(ArgumentObject o, char c) {
		return 1l;
	}

}