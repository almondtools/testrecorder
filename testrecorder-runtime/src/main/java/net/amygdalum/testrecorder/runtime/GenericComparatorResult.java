package net.amygdalum.testrecorder.runtime;

public enum GenericComparatorResult {

	MATCH(true), MISMATCH(false), NOT_APPLYING(false, true);
	
	private boolean applying;
	private boolean result;

	private GenericComparatorResult(boolean result) {
		this(true, result);
	}
	
	private GenericComparatorResult(boolean applying, boolean result) {
		this.applying = applying;
		this.result = result;
	}
	
	public boolean getResult() {
		return result;
	}
	
	public boolean isApplying() {
		return applying;
	}
	
}
