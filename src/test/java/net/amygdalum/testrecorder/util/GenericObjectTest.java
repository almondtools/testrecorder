package net.amygdalum.testrecorder.util;

import static com.almondtools.conmatch.conventions.ReflectiveEqualsMatcher.reflectiveEqualTo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.io.Serializable;

import org.junit.Test;

import net.amygdalum.testrecorder.Wrapped;

@SuppressWarnings("unused")
public class GenericObjectTest {

    @Test
    public void testAsSimple() throws Exception {
        assertThat(new GenericObject() {
            public String str = "myStr";
        }.as(Simple.class).getStr(), equalTo("myStr"));
    }

    @Test
    public void testAsComplex() throws Exception {
        assertThat(new GenericObject() {
            public Simple simple = new GenericObject() {
                public String str = "nestedStr";
            }.as(Simple.class);
        }.as(Complex.class).getSimple().getStr(), equalTo("nestedStr"));
    }

    @Test
    public void testAsSimplePrivateConstructor() throws Exception {
        assertThat(new GenericObject() {
            public String str = "myStr";
        }.as(SimplePrivateConstructor.class).getStr(), equalTo("myStr"));
    }

    @Test
    public void testAsSimpleImplicitConstructor() throws Exception {
        assertThat(new GenericObject() {
            public String str = "myStr";
        }.as(SimpleImplicitConstructor.class).getStr(), equalTo("myStr"));
    }

    @Test
    public void testAsSimpleNoDefaultConstructor() throws Exception {
        assertThat(new GenericObject() {
            public String str = "myStr";
        }.as(SimpleNoDefaultConstructor.class).getStr(), equalTo("myStr"));
    }

    @Test
    public void testAsConstructorSupplied() throws Exception {
        assertThat(new GenericObject() {
            public String str = "myStr";
        }.as(Simple::new).getStr(), equalTo("myStr"));
    }

    @Test
    public void testAsConstructorWrapped() throws Exception {
        Wrapped obj = GenericObject.forward(Wrapped.clazz(SimplePrivateConstructor.class.getName()));

        assertThat(((SimplePrivateConstructor) new GenericObject() {
            public String str = "myStr";
        }.as(obj).value()).getStr(), equalTo("myStr"));
    }

    @Test
    public void testSetField() throws Exception {
        Simple value = new Simple();
        Complex object = new Complex();

        GenericObject.setField(object, "simple", value);
        assertThat(object.simple, sameInstance(value));
    }

    @Test
    public void testSetFieldWrapped() throws Exception {
        Wrapped value = Wrapped.clazz(Simple.class.getName());
        Complex object = new Complex();

        GenericObject.setField(object, "simple", value);
        assertThat(object.simple, sameInstance(value.value()));
    }

    @Test
    public void testSetFieldField() throws Exception {
        Simple value = new Simple();
        Complex object = new Complex();

        GenericObject.setField(object, Complex.class.getDeclaredField("simple"), value);
        assertThat(object.simple, sameInstance(value));
    }

    @Test
    public void testCopyField() throws Exception {
        Simple from = new Simple();
        Simple to = new Simple();
        from.str = "stringToCopy";

        GenericObject.copyField(Simple.class.getDeclaredField("str"), from, to);

        assertThat(from.str, equalTo("stringToCopy"));
        assertThat(to.str, equalTo("stringToCopy"));
    }

    @Test
    public void testCopyArrayValues() throws Exception {
        String[] from = { "foo", "bar" };
        String[] to = new String[2];

        GenericObject.copyArrayValues(from, to);

        assertThat(from, arrayContaining("foo", "bar"));
        assertThat(to, arrayContaining("foo", "bar"));
    }

    @Test
    public void testForward() throws Exception {
        Simple obj = GenericObject.forward(Simple.class);

        assertThat(obj.str, nullValue());
    }

    @Test
    public void testForwardWrapped() throws Exception {
        Wrapped obj = GenericObject.forward(Wrapped.clazz(SimplePrivateConstructor.class.getName()));

        assertThat((SimplePrivateConstructor) obj.value(), reflectiveEqualTo(new SimplePrivateConstructor()));
    }

    @Test
    public void testDefine() throws Exception {
        Simple obj = GenericObject.forward(Simple.class);

        GenericObject.define(obj, new GenericObject() {
            String str = "definition";
        });

        assertThat(obj.str, equalTo("definition"));
    }

    @Test
    public void testDefineWrapped() throws Exception {
        Wrapped obj = GenericObject.forward(Wrapped.clazz(SimplePrivateConstructor.class.getName()));

        GenericObject.define(obj, new GenericObject() {
            String str = "definition";
        });

        assertThat(((SimplePrivateConstructor) obj.value()).str, equalTo("definition"));
    }

    @Test
    public void testNewInstanceWithNullParams() throws Exception {
        NullParamConstructor instance = GenericObject.newInstance(NullParamConstructor.class);

        assertThat(instance.getStr(), nullValue());
    }

    @Test
    public void testNewInstanceWithDefaultParams() throws Exception {
        DefaultParamConstructor instance = GenericObject.newInstance(DefaultParamConstructor.class);

        assertThat(instance.getStr(), isEmptyString());
    }

    @Test
    public void testNewInstanceWithNonDefaultParams() throws Exception {
        NonDefaultParamConstructor instance = GenericObject.newInstance(NonDefaultParamConstructor.class);

        assertThat(instance.getStr(), equalTo("String"));
    }

    @Test
    public void testNewInstanceBruteForceReflection() throws Exception {
        ExceptionConstructor instance = GenericObject.newInstance(ExceptionConstructor.class);

        assertThat(instance.getStr(), nullValue());
    }

    private interface AnInterface {
    }

    private enum AnEnum {
        ENUM;
    }

    private enum EmptyEnum {
    }

    private static class Simple {
        private String str;

        public Simple() {
        }

        public Simple(String str) {
            this.str = str;
        }

        public String getStr() {
            return str;
        }
    }

    private static class Complex {

        private Simple simple;

        public Complex() {
            this.simple = new Simple("otherStr");
        }

        public Simple getSimple() {
            return simple;
        }
    }

    private static class SimplePrivateConstructor {
        private String str;

        private SimplePrivateConstructor() {
        }

        public String getStr() {
            return str;
        }
    }

    private static class SimpleImplicitConstructor {
        private String str;

        public String getStr() {
            return str;
        }
    }

    private static class SimpleNoDefaultConstructor {
        private String str;

        public SimpleNoDefaultConstructor(String str) {
            this.str = str;
        }

        public String getStr() {
            return str;
        }
    }

    private static class NullParamConstructor {
        private String str;

        public NullParamConstructor(String str) {
            this.str = str;
        }

        public String getStr() {
            return str;
        }
    }

    private static class DefaultParamConstructor {
        private String str;

        public DefaultParamConstructor(String str) {
            if (str == null) {
                throw new NullPointerException();
            }
            this.str = str;
        }

        public String getStr() {
            return str;
        }
    }

    private static class NonDefaultParamConstructor {
        private String str;

        public NonDefaultParamConstructor(String str) {
            if (str == null) {
                throw new NullPointerException();
            } else if (str.isEmpty()) {
                throw new IllegalArgumentException();
            }
            this.str = str;
        }

        public String getStr() {
            return str;
        }
    }

    private static class ExceptionConstructor {
        private String str;

        public ExceptionConstructor(String str) {
            throw new IllegalArgumentException();
        }

        public String getStr() {
            return str;
        }
    }

    private static class NonSerializableConstructor implements Serializable {
        private String str;

        public NonSerializableConstructor(String str) {
            throw new IllegalArgumentException();
        }

        public String getStr() {
            return str;
        }
    }

}
