package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.hints.LoadFromFile;
import net.amygdalum.testrecorder.profile.Recorded;

public class LargeIntArrays {

    @LoadFromFile(writeTo = "target/generated", readFrom = "target/generated")
    private int[][] entries;

    public LargeIntArrays(int entries) {
        this.entries = initInts(entries);
    }

    public LargeIntArrays() {
        this.entries = new int[0][0];
    }

    @Recorded
    @LoadFromFile(writeTo = "target/generated", readFrom = "target/generated")
    public int[][] initInts(int entries) {
        int counter = 0;
        int[][] is = new int[entries][entries];
        for (int i = 0; i < is.length; i++) {
            for (int j = 0; j < is[i].length; j++) {
                is[i][j] = counter++;
            }
        }
        return is;
    }

    @Recorded
    @LoadFromFile(writeTo = "target/generated", readFrom = "target/generated")
    public int[][] doubleInts(@LoadFromFile(writeTo = "target/generated", readFrom = "target/generated") int[][] entries) {
        int[][] is = new int[entries.length][];
        for (int i = 0; i < is.length; i++) {
            is[i] = new int[entries[i].length];
            for (int j = 0; j < is[i].length; j++) {
                is[i][j] = entries[i][j] * 2;
            }
        }
        return is;
    }

    @Recorded
    public long sum() {
        long sum = 0;
        for (int i = 0; i < entries.length; i++) {
            for (int j = 0; j < entries[i].length; j++) {
                sum += entries[i][j];
            }
        }
        return sum;
    }

}
