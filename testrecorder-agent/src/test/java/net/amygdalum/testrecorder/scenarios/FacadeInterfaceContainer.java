package net.amygdalum.testrecorder.scenarios;

import java.io.IOException;

import net.amygdalum.testrecorder.profile.Facade;
import net.amygdalum.testrecorder.profile.Recorded;

public class FacadeInterfaceContainer {

	@Facade
	private FacadeInterfaceExample facade;
	
	public FacadeInterfaceContainer(String... values) throws IOException {
		this.facade = new FacadeImplementationExample(values);
	}

	@Recorded
	public String readFromFacade() throws IOException {
		return facade.read();
	}
	
	@Recorded
	public void writeToFacade(String value) throws IOException {
		facade.write(value);
	}
	
}