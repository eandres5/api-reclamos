package ec.banco.commons.api_commons.exception;

public class BadRequestException extends RuntimeException {

    /**
     * Constructor simple con mensaje personalizado.
     *
     * @param message mensaje descriptivo del error
     */
    public BadRequestException(String message) {
        super(message);
    }
}
