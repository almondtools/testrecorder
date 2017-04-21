package net.amygdalum.testrecorder.util.testobjects;

public class Hidden {

    public interface VisibleInterface {
        
    }
    
    private static class PartiallyHidden implements VisibleInterface {
        
    }
    
    private static class CompletelyHidden {
        
    }
    
    public static VisibleInterface createPartiallyHidden() {
        return new PartiallyHidden();
    }

    public static Object createCompletelyHidden() {
        return new CompletelyHidden();
    }
    
}
