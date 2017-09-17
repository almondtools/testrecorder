package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.Recorded;
import net.amygdalum.testrecorder.SerializationProfile.Input;

public class DelegatedInput {

	private SingleInput input;

	public DelegatedInput(SingleInput input) {
		this.input = input;
	}

	@Recorded
	public String combine(DelegatedInput delegated) {
		return this.input.currentTimeMillis() + ":" + delegated.input.currentTimeMillis();
	}
	
    public static class SingleInput {
    	@Input
        public long currentTimeMillis() {
            return System.currentTimeMillis();
        }

    }
}