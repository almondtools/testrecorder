package net.amygdalum.testrecorder.util.testobjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ContainingList {

	private List<String> list;

	public ContainingList() {
	}

    public ContainingList(List<String> list) {
        this.list = list;
    }

	public ContainingList(Collection<String> list) {
		this.list = new ArrayList<>(list);
	}

	public List<String> getList() {
		return list;
	}
}