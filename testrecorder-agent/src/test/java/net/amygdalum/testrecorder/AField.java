package net.amygdalum.testrecorder;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Condition;
import org.assertj.core.description.Description;
import org.assertj.core.description.TextDescription;

import net.amygdalum.testrecorder.profile.Fields;

public class AField extends Condition<Fields> {

	private List<Field> matches;
	private List<Field> nonMatches;

	public AField() {
		this.matches = new ArrayList<>();
		this.nonMatches = new ArrayList<>();
	}

	@Override
	public Description description() {
		return new TextDescription("matches %s, not matches %s", matches, nonMatches);
	}

	@Override
	public boolean matches(Fields fields) {
		for (Field match : matches) {
			if (!fields.matches(match.className, match.fieldName, match.fieldDescriptor)) {
				return false;
			}
		}
		for (Field nonMatch : nonMatches) {
			if (fields.matches(nonMatch.className, nonMatch.fieldName, nonMatch.fieldDescriptor)) {
				return false;
			}
		}
		return true;
	}

	public static AField matching(String className, String fieldName, String fieldDescriptor) {
		return new AField().andMatching(className, fieldName, fieldDescriptor);
	}

	public static AField notMatching(String className, String fieldName, String fieldDescriptor) {
		return new AField().andNotMatching(className, fieldName, fieldDescriptor);
	}

	public AField andNotMatching(String className, String fieldName, String fieldDescriptor) {
		this.nonMatches.add(new Field(className, fieldName, fieldDescriptor));
		return this;
	}

	public AField andMatching(String className, String fieldName, String fieldDescriptor) {
		this.matches.add(new Field(className, fieldName, fieldDescriptor));
		return this;
	}

	private class Field {
		public String className;
		public String fieldName;
		public String fieldDescriptor;

		public Field(String className, String fieldName, String fieldDescriptor) {
			this.className = className;
			this.fieldName = fieldName;
			this.fieldDescriptor = fieldDescriptor;
		}

		@Override
		public String toString() {
			return className + '.' + fieldName + ':' + fieldDescriptor;
		}
	}
}