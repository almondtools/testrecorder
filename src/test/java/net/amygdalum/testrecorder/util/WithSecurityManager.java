package net.amygdalum.testrecorder.util;

public class WithSecurityManager {

    private SecurityManager securityManager;

    public WithSecurityManager(SecurityManager securityManager) {
        this.securityManager = securityManager;
    }

    public static WithSecurityManager with(SecurityManager securityManager) {
        return new WithSecurityManager(securityManager);
    }

    public void execute(Runnable runnable) {
        SecurityManager old = System.getSecurityManager();
        try {
            System.setSecurityManager(securityManager);
            runnable.run();
        } finally {
            System.setSecurityManager(old);
        }
    }

}
