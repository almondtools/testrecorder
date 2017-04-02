package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.Recorded;
import net.amygdalum.testrecorder.hints.SkipChecks;

public class ExcludedFromChecking {

    @SkipChecks
    private long excluded;
    
    private int notExcluded;
    

    public ExcludedFromChecking(int init) {
        this.excluded = init;
        this.notExcluded = init * init;
    }

    @Recorded
    @SkipChecks
    public int getNotExcluded() {
        return notExcluded;
    }

    
    @Recorded
    public void reinit(@SkipChecks int... factors) {
        for (int i = 0; i < factors.length; i++) {
            excluded *= factors[i];
        }
        notExcluded = (int) excluded;
    }

    @Recorded
    public long next() {
        int temp = (int) excluded;
        excluded *= 2;
        notExcluded = temp;
        return excluded;
    }
    
}
