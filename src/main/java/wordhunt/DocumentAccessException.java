package wordhunt;

public class DocumentAccessException extends RuntimeException {
    public DocumentAccessException(Exception ex) {
        super(ex);
    }

    public DocumentAccessException(String message, Exception ex) {
        super(message, ex);
    }
}
