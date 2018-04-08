package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.profile.Recorded;

public class SharedState implements State {

	private State state;
	
	public SharedState() {
		state = new StringState();
	}
	
	public static SharedState create(State state) {
		SharedState sharedState = new SharedState();
		sharedState.state = state;
		return sharedState;
	}

	@Override
	public String next() {
		return state.next();
	}
	
	@Recorded
	public String combine(SharedState other) {
		return state.next() + ":" + other.state.next();
	}
	
}