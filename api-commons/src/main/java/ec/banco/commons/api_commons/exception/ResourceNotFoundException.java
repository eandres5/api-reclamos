package ec.banco.commons.api_commons.exception;

public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructor simple con mensaje personalizado.
     *
     * @param message mensaje descriptivo del error
     */
    public ResourceNotFoundException(final String message) {
        super(message);
    }

    /**
     * Constructor que construye un mensaje dinamico basado en:
     *
     * @param resourceName nombre del recurso
     * @param fieldName    nombre del campo
     * @param fieldValue   valor del campo buscado
     */
    public ResourceNotFoundException(final String resourceName, final String fieldName, final String fieldValue) {
        super(String.format("%s no encontrado con %s: '%s'", resourceName, fieldName, fieldValue));
    }
}