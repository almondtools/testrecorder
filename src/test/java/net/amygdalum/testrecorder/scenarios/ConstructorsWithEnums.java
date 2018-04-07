package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.profile.Recorded;

public class ConstructorsWithEnums {

	private InnerEnum value;
	private ChainedEnum chained;

	public ConstructorsWithEnums(String value) {
		this.value = InnerEnum.valueOf(value);
		this.chained = ChainedEnum.valueOf(value);
	}

	public ConstructorsWithEnums(InnerEnum value) {
		this.value = value;
	}

	public ConstructorsWithEnums(ChainedEnum chained) {
		this.chained = chained;
		this.value = chained.value;
	}

	public InnerEnum getValue() {
		return value;
	}

	public ChainedEnum getChained() {
		return chained;
	}

	@Recorded
	public static String toString(ConstructorsWithEnums value) {
		return value.toString();
	}

	@Override
	public String toString() {
		return (value == null ? "null" : value.toString()) + ":" + (chained == null ? "null" : chained.toString());
	}

	public static enum InnerEnum {
		FIRST, SECOND
	}

	public static enum ChainedEnum {
		FIRST(InnerEnum.FIRST), SECOND(InnerEnum.SECOND);

		private InnerEnum value;

		private ChainedEnum(InnerEnum value) {
			this.value = value;
		}
	}

}