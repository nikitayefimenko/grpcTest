package task.grpc.exception;

public abstract class BaseException extends Exception {

    public BaseException(Exception e) {
        super(e);
    }

    public BaseException(String message, Exception e) {
        super(message, e);
    }

    public BaseException(String message, Object... args) {
        super(String.format(message, args));
    }

    public BaseException(Exception e, String message, Object... args) {
        super(String.format(message, args), e);
    }

}
