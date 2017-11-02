package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.Recorded;

public class SharedState {

	private State state;
	
	public SharedState() {
		state = new State();
	}
	
	public static SharedState create(State state) {
		SharedState sharedState = new SharedState();
		sharedState.state = state;
		return sharedState;
	}
	
	@Recorded
	public String combine(SharedState other) {
		return state.next() + ":" + other.state.next();
	}
	
}