package net.amygdalum.testrecorder.util.testobjects;

import java.util.Collection;

public class GenericMethods<T>{

	public GenericMethods() {
	}
	
	public T methodT() {
		return null;
	}
	
	public static <S> S free(S value) {
		return value;
	}
	
	public static <S> S bound(S a, S b) {
		return a;
	}
	
	public static <S> void freeNested(GenericMethods<S> value) {
	}
	
	public static <S> void boundNested(GenericMethods<S> a, S b) {
	}
	
	public static <S extends Collection<?>> S freeLimited(S value) {
		return value;
	}
	
	public static <S extends Collection<?>> S boundLimited(S a, S b) {
		return a;
	}
	
}