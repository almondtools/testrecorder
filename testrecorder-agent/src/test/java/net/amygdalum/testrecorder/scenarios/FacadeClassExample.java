package net.amygdalum.testrecorder.scenarios;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;

import net.amygdalum.testrecorder.profile.Facade;
import net.amygdalum.testrecorder.profile.Input;
import net.amygdalum.testrecorder.profile.Output;

@Facade
public class FacadeClassExample {

	private BufferedWriter out;
	private BufferedReader in;
	
	public FacadeClassExample(String ... values) throws IOException {
		PipedWriter out = new PipedWriter();
		PipedReader in = new PipedReader(out);
		this.out = new BufferedWriter(out); 
		this.in = new BufferedReader(in);
		for (String value : values) {
			this.out.write(value);
			this.out.newLine();
		}
		this.out.flush();
	}
	
	@Input
	public String read() throws IOException {
		return in.readLine();
	}

	@Output
	public void write(String value) throws IOException {
		out.write(value);
		out.newLine();
		out.flush();
	}

}
