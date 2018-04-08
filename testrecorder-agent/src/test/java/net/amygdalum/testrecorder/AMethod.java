package net.amygdalum.testrecorder;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Condition;
import org.assertj.core.description.Description;
import org.assertj.core.description.TextDescription;

import net.amygdalum.testrecorder.profile.Methods;

public class AMethod extends Condition<Methods> {

	private List<Method> matches;
	private List<Method> nonMatches;

	public AMethod() {
		this.matches = new ArrayList<>();
		this.nonMatches = new ArrayList<>();
	}

	@Override
	public Description description() {
		return new TextDescription("matches %s, not matches %s", matches, nonMatches);
	}

	@Override
	public boolean matches(Methods methods) {
		for (Method match : matches) {
			if (!methods.matches(match.className, match.methodName, match.methodDescriptor)) {
				return false;
			}
		}
		for (Method nonMatch : nonMatches) {
			if (methods.matches(nonMatch.className, nonMatch.methodName, nonMatch.methodDescriptor)) {
				return false;
			}
		}
		return true;
	}

	public static AMethod matching(String className, String methodName, String methodDescriptor) {
		return new AMethod().andMatching(className, methodName, methodDescriptor);
	}

	public static AMethod notMatching(String className, String methodName, String methodDescriptor) {
		return new AMethod().andNotMatching(className, methodName, methodDescriptor);
	}

	public AMethod andNotMatching(String className, String methodName, String methodDescriptor) {
		this.nonMatches.add(new Method(className, methodName, methodDescriptor));
		return this;
	}

	public AMethod andMatching(String className, String methodName, String methodDescriptor) {
		this.matches.add(new Method(className, methodName, methodDescriptor));
		return this;
	}

	private class Method {
		public String className;
		public String methodName;
		public String methodDescriptor;

		public Method(String className, String methodName, String methodDescriptor) {
			this.className = className;
			this.methodName = methodName;
			this.methodDescriptor = methodDescriptor;
		}

		@Override
		public String toString() {
			return className + '.' + methodName + ':' + methodDescriptor;
		}
	}
}