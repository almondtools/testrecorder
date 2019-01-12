package net.amygdalum.testrecorder.util.testobjects;

public class Container<T> {
	private T content;

	public Container(T content) {
		this.content = content;
	}
	
	public void setContent(T content) {
		this.content = content;
	}
	
	public T getContent() {
		return content;
	}

    @Override
    public String toString() {
        return content.toString();
    }
}