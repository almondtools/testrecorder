package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.profile.Recorded;

public class ConstructorsWithNestedEnums {

	private InnerEnum value;
	private ChainedEnum chained;
	private RecursiveEnum recursive;

	private ConstructorsWithNestedEnums(String value) {
		this.value = InnerEnum.valueOf(value);
		this.chained = ChainedEnum.valueOf(value);
	}

	private ConstructorsWithNestedEnums(InnerEnum value) {
		this.value = value;
	}

	public ConstructorsWithNestedEnums(ChainedEnum chained) {
		this.chained = chained;
		this.value = chained.value;
	}

	public ConstructorsWithNestedEnums(RecursiveEnum recursive) {
		this.recursive = recursive;
	}

	public static ConstructorsWithNestedEnums of(String value) {
		return new ConstructorsWithNestedEnums(value);
	}

	public static ConstructorsWithNestedEnums of(InnerEnum value) {
		return new ConstructorsWithNestedEnums(value);
	}
	
	public static ConstructorsWithNestedEnums of(ChainedEnum value) {
		return new ConstructorsWithNestedEnums(value);
	}
	
	public static ConstructorsWithNestedEnums of(RecursiveEnum value) {
		return new ConstructorsWithNestedEnums(value);
	}
	
	public InnerEnum getValue() {
		return value;
	}

	public ChainedEnum getChained() {
		return chained;
	}
	
	public RecursiveEnum getRecursive() {
		return recursive;
	}
	

	@Recorded
	public static String toString(ConstructorsWithNestedEnums value) {
		return value.toString();
	}

	@Override
	public String toString() {
		return (value == null ? "null" : value.toString()) + ":" + (chained == null ? "null" : chained.toString()) + (recursive != null ? ":" + recursive : "");
	}

	public enum InnerEnum {
		FIRST, SECOND
	}

	public enum ChainedEnum {
		FIRST(InnerEnum.FIRST), SECOND(InnerEnum.SECOND);

		private InnerEnum value;

		private ChainedEnum(InnerEnum value) {
			this.value = value;
		}
	}
	
	public enum RecursiveEnum {
		FIRST(2),
		SECOND(3) {

			@Override
			public boolean includes(RecursiveEnum value) {
				return value == FIRST;
			}
		},
		THIRD(5) {

			@Override
			public boolean includes(RecursiveEnum value) {
				return value == FIRST || value == SECOND;
			}
		},
		OTHER(2);

		public final int value;

		private RecursiveEnum(int value) {
			this.value = value;
		}

		public boolean includes(RecursiveEnum value) {
			return false;
		}
	}	

}