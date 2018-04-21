package net.amygdalum.testrecorder.scenarios;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;

public class FacadeImplementationExample implements FacadeInterfaceExample {

	private BufferedWriter out;
	private BufferedReader in;
	
	public FacadeImplementationExample(String ... values) throws IOException {
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
	
	@Override
	public String read() throws IOException {
		return in.readLine();
	}

	@Override
	public void write(String value) throws IOException {
		out.write(value);
		out.newLine();
		out.flush();
	}
	
	@Override
	public int readInt() throws IOException {
		return Integer.parseInt(read());
	}
	
	@Override
	public void writeInt(int value) throws IOException {
		write(String.valueOf(value));
	}

}
