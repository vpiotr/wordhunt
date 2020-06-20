package wordhunt;

public class IndexAccessException extends RuntimeException {
    public IndexAccessException() {
        super();
    }

    public IndexAccessException(String message, Throwable t) {
        super(message, t);
    }

    public IndexAccessException(Throwable t) {
        super(t);
    }
}
