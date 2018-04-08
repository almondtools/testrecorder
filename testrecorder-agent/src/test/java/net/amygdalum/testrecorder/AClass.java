package net.amygdalum.testrecorder;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Condition;
import org.assertj.core.description.Description;
import org.assertj.core.description.TextDescription;

import net.amygdalum.testrecorder.profile.Classes;

public class AClass extends Condition<Classes> {

	private List<String> matches;
	private List<String> nonMatches;

	public AClass() {
		this.matches = new ArrayList<>();
		this.nonMatches = new ArrayList<>();
	}
	
	@Override
	public Description description() {
		return new TextDescription("matches %s, not matches %s", matches, nonMatches);
	}
	
	@Override
	public boolean matches(Classes classes) {
		for (String match : matches) {
			if (!classes.matches(match)) {
				return false;
			}
		}
		for (String nonMatch : nonMatches) {
			if (classes.matches(nonMatch)) {
				return false;
			}
		}
		return true;
	}

	public static AClass matching(String name) {
		return new AClass().andMatching(name);
	}

	public static AClass notMatching(String name) {
		return new AClass().andNotMatching(name);
	}

	public AClass andNotMatching(String name) {
		this.nonMatches.add(name);
		return this;
	}

	public AClass andMatching(String name) {
		this.matches.add(name);
		return this;
	}

}