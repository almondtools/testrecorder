package net.amygdalum.testrecorder.dynamiccompile;

import static java.util.Collections.emptyList;

import java.util.List;

public class DynamicClassCompilerException extends Exception {

	private List<String> detailMessages;

	public DynamicClassCompilerException(String msg) {
		super(msg);
		this.detailMessages = emptyList();
	}

	public DynamicClassCompilerException(String msg, List<String> detailMessages) {
		super(msg);
		this.detailMessages = detailMessages;
	}
	
	public List<String> getDetailMessages() {
		return detailMessages;
	}

}
