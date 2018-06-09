package net.amygdalum.testrecorder.util.testobjects;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {

	public List<TreeNode> children;
	public Object payload;

	public TreeNode() {
		this.children = new ArrayList<>();
	}
	
	public TreeNode setPayload(Object payload) {
		this.payload = payload;
		return this;
	}
	
	public TreeNode child(int i) {
		return children.get(i);
	}
	
	public TreeNode addChild(TreeNode child) {
		children.add(child);
		return this;
	}
	
}
