package ec.banco.api_reclamos_service.enumerado;

public enum TipoReclamo {

    TARJETAS_CREDITO("Tarjetas de Crédito"),
    TRANSFERENCIAS("Transferencias"),
    PAGO_SERVICIOS("Pago de Servicios");

    private final String descripcion;

    TipoReclamo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

