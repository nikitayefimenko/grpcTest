package task.grpc.exception;

public class ValidationException extends BaseException {
    public ValidationException(Exception e) {
        super(e);
    }

    public ValidationException(String message, Exception e) {
        super(message, e);
    }

    public ValidationException(String message, Object... args) {
        super(message, args);
    }

    public ValidationException(Exception e, String message, Object... args) {
        super(e, message, args);
    }
}
