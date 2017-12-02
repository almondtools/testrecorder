package net.amygdalum.testrecorder.asm;

import org.objectweb.asm.Type;

public class Local {
	public int index;
	public Type type;

	public Local(int index, Type type) {
		this.index = index;
		this.type = type;
	}
	
	
}