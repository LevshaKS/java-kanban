package exceptions;

public class HttpException extends RuntimeException {

    public HttpException(final String message) {
        super(message);
    }
}