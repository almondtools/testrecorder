package net.amygdalum.testrecorder.types;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class TestDeSerializer {

	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	
	public TestDeSerializer() throws IOException {
		PipedOutputStream out = new PipedOutputStream();
		PipedInputStream in = new PipedInputStream(out);
		this.out = new ObjectOutputStream(out);
		this.in = new ObjectInputStream(in);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T deSerialize(T object) throws IOException, ClassNotFoundException {
		out.writeObject(object);
		return (T) in.readObject();
	}

}
