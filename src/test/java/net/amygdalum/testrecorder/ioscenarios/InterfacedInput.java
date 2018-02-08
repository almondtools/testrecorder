package net.amygdalum.testrecorder.ioscenarios;

import net.amygdalum.testrecorder.profile.SerializationProfile.Input;

public interface InterfacedInput {

	@Input
	long input();

}