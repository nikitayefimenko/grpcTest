package task.grpc.exception;

public class WebResourceException extends BaseException {
    public WebResourceException(Exception e) {
        super(e);
    }

    public WebResourceException(String message, Exception e) {
        super(message, e);
    }

    public WebResourceException(String message, Object... args) {
        super(message, args);
    }

    public WebResourceException(Exception e, String message, Object... args) {
        super(e, message, args);
    }
}
