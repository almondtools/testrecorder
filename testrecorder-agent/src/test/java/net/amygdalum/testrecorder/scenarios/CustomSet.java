package net.amygdalum.testrecorder.scenarios;

import java.util.HashSet;

import net.amygdalum.testrecorder.profile.Recorded;

public class CustomSet<T> extends HashSet<T> {

	@Recorded
	@Override
	public boolean add(T e) {
		return super.add(e);
	}
	
}
