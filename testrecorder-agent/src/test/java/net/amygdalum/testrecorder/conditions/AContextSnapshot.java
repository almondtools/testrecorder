package net.amygdalum.testrecorder.conditions;

import org.assertj.core.api.Condition;
import org.assertj.core.description.Description;
import org.assertj.core.description.TextDescription;

import net.amygdalum.extensions.assertj.conditions.AString;
import net.amygdalum.extensions.assertj.conditions.CompoundDescription;
import net.amygdalum.testrecorder.ContextSnapshot;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedValue;

public class AContextSnapshot extends Condition<ContextSnapshot> {

	private Condition<String> keyCondition;
	private Condition<SerializedValue> setupThisObjectCondition;

	public AContextSnapshot() {
	}

	public static AContextSnapshot withKey(String keyCondition) {
		return new AContextSnapshot().andKey(keyCondition);
	}

	public AContextSnapshot andKey(String keyCondition) {
		return andKey(AString.matching(keyCondition));
	}

	public AContextSnapshot andKey(Condition<String> keyCondition) {
		this.keyCondition = keyCondition;
		return this;
	}

	public Condition<ContextSnapshot> andSetupThis(SerializedReferenceType serializedThisObjectCondition) {
		return andSetupThis(ASerializedValue.structurallyEqualTo(serializedThisObjectCondition));
	}

	public Condition<ContextSnapshot> andSetupThis(Condition<SerializedValue> setupThisObjectCondition) {
		this.setupThisObjectCondition = setupThisObjectCondition;
		return this;
	}

	@Override
	public Description description() {
		CompoundDescription description = new CompoundDescription(new TextDescription("matches ContextSnapshot"));
		if (keyCondition != null) {
			description.addComponent("key", keyCondition.description());
		}
		if (setupThisObjectCondition != null) {
			description.addComponent("serializedThisObject", setupThisObjectCondition.description());
		}
		return description;
	}

	@Override
	public boolean matches(ContextSnapshot contextSnapshot) {
		if (keyCondition != null && !keyCondition.matches(contextSnapshot.getKey())) {
			return false;
		}
		if (setupThisObjectCondition != null && !setupThisObjectCondition.matches(contextSnapshot.getSetupThis())) {
			return false;
		}
		return true;
	}

}
