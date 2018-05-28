package net.amygdalum.testrecorder.util.testobjects;

public class GenericCycle<T> {
    private T a;
    public GenericCycle<T> next;

    private GenericCycle(T a) {
        this.a = a;
    }

    public static <T> GenericCycle<T> recursive(T a) {
        GenericCycle<T> cycle = new GenericCycle<>(a);
        cycle.next = cycle;

        return cycle;
    }

    public T getA() {
        return a;
    }

    public boolean insert(GenericCycle<T> element) {
    	GenericCycle<T> temp = next;
    	this.next = element;
    	element.next = temp;
    	return element == this;
    }
    
}