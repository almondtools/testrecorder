package net.amygdalum.testrecorder.util.testobjects;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class Hidden {

    public interface VisibleInterface {

    }

    private static class PartiallyHidden implements VisibleInterface {

    }

    public static Class<?> classOfPartiallyHidden() {
        return PartiallyHidden.class;
    }

    public static VisibleInterface createPartiallyHidden() {
        return new PartiallyHidden();
    }

    private static class CompletelyHidden {

    }

    public static Class<?> classOfCompletelyHidden() {
        return CompletelyHidden.class;
    }

    public static Object createCompletelyHidden() {
        return new CompletelyHidden();
    }

    private static class HiddenList<T> extends ArrayList<T> implements OrthogonalInterface {

    }

    public static Class<?> classOfHiddenList() {
        return HiddenList.class;
    }

    @SafeVarargs
    public static <S> HiddenList<S> hiddenList(S... elements) {
        HiddenList<S> hiddenList = new HiddenList<>();
        for (S element : elements) {
            hiddenList.add(element);
        }
        return hiddenList;
    }

    private static class HiddenQueue<T> extends LinkedList<T> implements OrthogonalInterface {

    }

    public static Class<?> classOfHiddenQueue() {
        return HiddenQueue.class;
    }

    private static class HiddenMap<K, V> extends LinkedHashMap<K, V> implements OrthogonalInterface {

    }
    
    public static Class<?> classOfHiddenMap() {
        return HiddenMap.class;
    }

    private static class HiddenSet<T> extends LinkedHashSet<T> implements OrthogonalInterface {

    }
    
    public static Class<?> classOfHiddenSet() {
        return HiddenSet.class;
    }

    private static enum HiddenEnum {
        VALUE1, VALUE2;
    }

    public static Class<?> classOfHiddenEnum() {
        return HiddenEnum.class;
    }

}
