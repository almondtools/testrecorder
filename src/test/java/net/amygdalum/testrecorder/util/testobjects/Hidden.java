package net.amygdalum.testrecorder.util.testobjects;

import java.lang.reflect.Type;
import java.util.ArrayList;

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

    private static class HiddenList<T> extends ArrayList<T> {

    }

    public static Type classOfHiddenList() {
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

    private static enum HiddenEnum {
        VALUE1, VALUE2;
    }

    public static Class<?> classOfHiddenEnum() {
        return HiddenEnum.class;
    }

}
