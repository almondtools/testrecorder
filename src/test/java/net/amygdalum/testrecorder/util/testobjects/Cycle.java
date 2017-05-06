package net.amygdalum.testrecorder.util.testobjects;

public class Cycle {
    private String a;
    public Cycle next;

    private Cycle(String a) {
        this.a = a;
    }

    public static Cycle recursive(String a) {
        Cycle cycle = new Cycle(a);
        cycle.next = cycle;

        return cycle;
    }

    public String getA() {
        return a;
    }

}