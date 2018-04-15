package net.amygdalum.testrecorder.ioscenarios;

import net.amygdalum.testrecorder.profile.Input;
import net.amygdalum.testrecorder.profile.Recorded;

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