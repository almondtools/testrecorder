package net.amygdalum.testrecorder.scenarios;

import java.util.ArrayList;

import net.amygdalum.testrecorder.profile.Recorded;

public class CustomList<T> extends ArrayList<T> {

	@Recorded
	@Override
	public boolean add(T e) {
		return super.add(e);
	}
	
}
