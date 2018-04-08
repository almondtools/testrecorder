package net.amygdalum.testrecorder.util.testobjects;

import java.util.ArrayList;

public class Collections {

    @SafeVarargs
    public static <S> ArrayList<S> arrayList(S... elements) {
        ArrayList<S> hiddenList = new ArrayList<>();
        for (S element : elements) {
            hiddenList.add(element);
        }
        return hiddenList;
    }

}
