package net.amygdalum.testrecorder.util;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

public final class Instantiations {

    private static volatile Objenesis objenesis;

    private Instantiations() {
    }

    public static <T> T newInstance(Class<T> clazz) {
        return getObjenesis().newInstance(clazz);
    }

    private static synchronized Objenesis getObjenesis() {
        if (objenesis == null) {
            objenesis = new ObjenesisStd();
        }
        return objenesis;
    }

    public static synchronized void resetInstatiations() {
        objenesis = null;
    }
    
}
