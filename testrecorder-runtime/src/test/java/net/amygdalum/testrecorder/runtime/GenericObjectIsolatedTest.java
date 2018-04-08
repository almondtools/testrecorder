package net.amygdalum.testrecorder.runtime;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.Serializable;
import java.security.Permission;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.runtime.GenericObject;
import net.amygdalum.testrecorder.runtime.Throwables;
import net.amygdalum.testrecorder.util.WithSecurityManager;

@SuppressWarnings("unused")
public class GenericObjectIsolatedTest {

    @Test
    public void testNewInstanceFailingBruteForceReflection() throws Exception {
        WithSecurityManager.with(new SecurityManager() {
            @Override
            public void checkPackageAccess(String pkg) {
                if (pkg.equals("sun.reflect")) {
                    throw new SecurityException("security manager preventing reflection");
                }
            }

            @Override
            public void checkPermission(Permission perm) {
            }
        }).execute(() -> {
            Throwable captured = Throwables.capture(() -> GenericObject.newInstance(NonSerializableConstructor.class));

            assertThat(captured.getMessage()).containsSequence(
"NonSerializableConstructor(null)",
"NonSerializableConstructor(\"\")",
"NonSerializableConstructor(\"String\")");
        });
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
