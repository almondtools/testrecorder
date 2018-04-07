package net.amygdalum.testrecorder.ioscenarios;

import net.amygdalum.testrecorder.profile.Recorded;
import net.amygdalum.testrecorder.profile.SerializationProfile.Output;

public class Outputs {

	public Outputs() {
	}
	
	@Recorded
	public void recorded() {
		print("Hello ");
		print("World");
	}

	public void notrecorded() {
		print("Hello ");
        print("World");
	}
	
	@Recorded
	public void primitivesRecorded() {
		printByte((byte) 1);
		printShort((short) 2);
		printInt(3);
		printLong(4l);
		printFloat(-1.2f);
		printDouble(1.1d);
		printBoolean(true);
		printChar('b');
	}

	@Recorded
	public void recordedWithConditionalReturn() {
		conditionalReturnOutput('a');
		conditionalReturnOutput(',');
		conditionalReturnOutput(' ');
		conditionalReturnOutput('b');
		conditionalReturnOutput('\n');
	}

	@Output
	public void print(String s) {
	}

	@Output
	public void printByte(byte value) {
	}

	@Output
	public void printShort(short value) {
	}

	@Output
	public void printInt(int value) {
	}
	
	@Output
	public void printLong(long value) {
	}
	
	@Output
	public void printFloat(float value) {
	}
	
	@Output
	public void printDouble(double value) {
	}
	
	@Output
	public void printBoolean(boolean value) {
	}
	
	@Output
	public void printChar(char value) {
	}
	
	@Output
	public boolean conditionalReturnOutput(char value) {
		if (Character.isWhitespace(value)) {
			return false;
		} else {
			return true;
		}
	}
	
}