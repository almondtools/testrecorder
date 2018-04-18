package net.amygdalum.testrecorder.scenarios;

import java.io.IOException;

import net.amygdalum.testrecorder.profile.Input;
import net.amygdalum.testrecorder.profile.Output;

public interface FacadeInterfaceExample {

	@Input
	String read() throws IOException;

	@Output
	void write(String value) throws IOException;

}
