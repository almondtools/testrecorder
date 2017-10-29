package net.amygdalum.testrecorder.util.testobjects;

public class Bounded<V extends Super> {

	public V v;
	public Bounded<? extends V> vx;
	
	public Bounded() {
	}
	
}