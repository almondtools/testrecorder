package net.amygdalum.testrecorder.util;

import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isPublic;
import static net.amygdalum.testrecorder.util.Types.getDeclaredField;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

public final class Reflections {

    private static final String MODIFIERS = "modifiers";

    private Reflections() {
    }

    public static <T extends AccessibleObject & Member> Accessing<T> accessing(T o) {
        return new Accessing<>(o);
    }

    public static Object getValue(String fieldName, Object item) throws ReflectiveOperationException {
        Field field = getDeclaredField(item.getClass(), fieldName);

        return getValue(field, item);
    }

    public static Object getValue(Field field, Object item) throws ReflectiveOperationException {
        return accessing(field).call(f -> f.get(item));
    }

    public static class Accessing<T extends AccessibleObject & Member> {

        private T object;

        public Accessing(T object) {
            this.object = object;
        }

        public <S> S call(AccessFunction<T, S> code) throws ReflectiveOperationException {
            boolean reset = ensureAccess();
            try {
                S result = code.apply(object);
                return result;
            } finally {
                resetAccess(reset);
            }
        }

        public void exec(AccessConsumer<T> code) throws ReflectiveOperationException {
            boolean reset = ensureAccess();
            try {
                code.accept(object);
            } finally {
                resetAccess(reset);
            }
        }

        private boolean ensureAccess() throws ReflectiveOperationException {
            int modifiers = object.getModifiers();
            if (isFinal(modifiers) && object instanceof Field) {
                makeNonFinal((Field) object);
            }
            if (isPublic(modifiers) && isPublic(object.getDeclaringClass().getModifiers())) {
                return false;
            } else if (!object.isAccessible()) {
                object.setAccessible(true);
                return true;
            } else {
                return false;
            }
        }

        private void makeNonFinal(Field field) throws ReflectiveOperationException {
            Field modifiersField = Field.class.getDeclaredField(MODIFIERS);
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        }

        private void resetAccess(boolean reset) {
            if (reset) {
                object.setAccessible(false);
            }
        }

    }

    public interface AccessFunction<T, S> {

        S apply(T member) throws ReflectiveOperationException;

    }

    public interface AccessConsumer<T> {

        void accept(T member) throws ReflectiveOperationException;

    }

}
