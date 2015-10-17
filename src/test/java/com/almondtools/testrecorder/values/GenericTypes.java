package com.almondtools.testrecorder.values;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GenericTypes<T, L extends List<T>, S extends Set<T>, M extends Map<T,T>> {

	public L listOfBounded;
	public List<String> listOfString;
	public ArrayList<String> arrayListOfString;
	public List<Set<String>> listOfSetOfString;
	
	public S setOfBounded;
	public Set<String> setOfString;
	public HashSet<String> hashSetOfString;
	public Set<List<String>> setOfListOfString;

	public static Type listOfString() {
		try {
			return GenericTypes.class.getDeclaredField("listOfString").getGenericType();
		} catch (ReflectiveOperationException e) {
			return List.class;
		}
	}
	
	public static Type listOfSetOfString() {
		try {
			return GenericTypes.class.getDeclaredField("listOfSetOfString").getGenericType();
		} catch (ReflectiveOperationException e) {
			return List.class;
		}
	}
	
	public static Type arrayListOfString() {
		try {
			return GenericTypes.class.getDeclaredField("arrayListOfString").getGenericType();
		} catch (ReflectiveOperationException e) {
			return List.class;
		}
	}
	
	public static Type listOfBounded() {
		try {
			return GenericTypes.class.getDeclaredField("listOfBounded").getGenericType();
		} catch (ReflectiveOperationException e) {
			return List.class;
		}
	}

	public static Type setOfString() {
		try {
			return GenericTypes.class.getDeclaredField("setOfString").getGenericType();
		} catch (ReflectiveOperationException e) {
			return List.class;
		}
	}
	
	public static Type setOfListOfString() {
		try {
			return GenericTypes.class.getDeclaredField("setOfListOfString").getGenericType();
		} catch (ReflectiveOperationException e) {
			return List.class;
		}
	}
	
	public static Type hashSetOfString() {
		try {
			return GenericTypes.class.getDeclaredField("hashSetOfString").getGenericType();
		} catch (ReflectiveOperationException e) {
			return List.class;
		}
	}
	
	public static Type setOfBounded() {
		try {
			return GenericTypes.class.getDeclaredField("setOfBounded").getGenericType();
		} catch (ReflectiveOperationException e) {
			return List.class;
		}
	}
}