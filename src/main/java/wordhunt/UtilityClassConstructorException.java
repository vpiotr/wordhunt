package wordhunt;

public class UtilityClassConstructorException extends RuntimeException {
    public UtilityClassConstructorException() {
        super("Utility class constructor call not allowed");
    }
}