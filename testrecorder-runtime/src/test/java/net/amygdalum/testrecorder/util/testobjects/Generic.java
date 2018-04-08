package net.amygdalum.testrecorder.util.testobjects;

public class Generic<V extends Super> implements GenericInterface<V>{

	public V v;
	public Generic<? extends V> vx;
	public V[] vs;
	public Generic<?> starx;
	
	public Generic() {
	}
	
}