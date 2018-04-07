package net.amygdalum.testrecorder.scenarios;

import static java.util.Arrays.asList;

import net.amygdalum.testrecorder.profile.Recorded;

public class Imports {

	private java.util.List<String> list;
	private List otherList;
	
	public Imports(String name) {
		list = asList(name);
		otherList = new List(name);
	}
	
	@Recorded
	@Override
	public String toString() {
		return list.toString() + ":" + otherList.getName();
	}
	
	
	public static class List {

		private String name;

		public List(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
	}
}
