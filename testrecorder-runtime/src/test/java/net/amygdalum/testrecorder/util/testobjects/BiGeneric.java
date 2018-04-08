package net.amygdalum.testrecorder.util.testobjects;

public class BiGeneric<K, V extends Super> implements GenericInterface<K>, NonGenericInterface {

	public K k;
	public V v;
	public BiGeneric<K, V> vx;
	public V[] vs;
	public K[] ks;
	public BiGeneric<? extends K, V> kstarv;
	public BiGeneric<? super K, V> ksupv;
	public BiGeneric<K, ? extends V> kvstar;
	public BiGeneric<K, ? super V> kvsup;
	public BiGeneric<?, V> starv;
	public BiGeneric<K, ?> kstar;

	public BiGeneric() {
	}

}