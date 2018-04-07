package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.hints.SkipChecks;
import net.amygdalum.testrecorder.profile.Recorded;

public class ExcludedFromChecking {

    @SkipChecks
    private long longVar;
    
    private int intVar;
    

    public ExcludedFromChecking(int init) {
        this.longVar = init;
        this.intVar = init * init;
    }

    @Recorded
    @SkipChecks
    public int getIntVar() {
        return intVar;
    }

    
    @Recorded
    public void reinit(@SkipChecks int... factors) {
        for (int i = 0; i < factors.length; i++) {
            longVar *= factors[i];
        }
        intVar = (int) longVar;
    }

    @Recorded
    public long next() {
        int temp = (int) longVar;
        longVar *= 2;
        intVar = temp;
        return longVar;
    }
    
}
