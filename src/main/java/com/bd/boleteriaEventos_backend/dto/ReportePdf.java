package com.bd.boleteriaEventos_backend.dto;

import com.bd.boleteriaEventos_backend.exception.ResourceNotFoundException;

import java.util.List;

public enum ReportePdf {
    FUNCIONES(1, "Próximas funciones turísticas", "proximas-funciones",
            "ID función", "Atracción", "Prestador", "Municipio", "Fecha", "Apertura", "Cierre", "Estado"),
    RESERVAS_CLIENTE(2, "Reservas por cliente", "reservas-por-cliente",
            "ID perfil", "Documento", "Cliente", "ID reserva", "Fecha y hora", "Estado", "Plan turístico"),
    COMPROBANTE(3, "Comprobante de reserva", "comprobante-reserva",
            "Código de boleta", "Atracción", "Fecha de función", "Zona", "Tipo de entrada", "Combo", "Precio pagado"),
    OCUPACION(4, "Ocupación por zona", "ocupacion-por-zona",
            "Atracción", "ID función", "Fecha", "ID zona", "Zona", "Aforo", "Cupos ocupados",
            "Cupos disponibles", "Ocupación"),
    INGRESOS_ATRACCION(5, "Ingresos por atracción y tipo de entrada", "ingresos-por-atraccion-y-tipo",
            "Atracción", "Tipo de entrada", "Boletas vendidas", "Total ingresos", "Precio promedio"),
    RESERVAS_MES(6, "Reservas por mes y estado", "reservas-por-mes-y-estado",
            "Mes", "Estado", "Total reservas"),
    INGRESOS_MUNICIPIO(7, "Ingresos por municipio y tipo de prestador", "ingresos-por-municipio-y-tipo",
            "Municipio", "Tipo de prestador", "Boletas vendidas", "Total ingresos"),
    COMBOS(8, "Combos más vendidos", "combos-mas-vendidos",
            "ID combo", "Combo", "Precio actual", "Estado", "Reservas con combo", "Boletas de combo", "Total ingresos"),
    RECURRENTES(9, "Clientes recurrentes", "clientes-recurrentes",
            "ID perfil", "Documento", "Cliente", "Correo", "Reservas confirmadas", "Primera reserva", "Última reserva"),
    AUDITORIA(10, "Auditoría de menores sin adulto", "auditoria-de-menores-sin-adulto",
            "ID reserva", "Cliente", "Atracción", "ID función", "Fecha", "Boletas de menores");

    private final int numero;
    private final String titulo;
    private final String nombreArchivo;
    private final List<String> encabezados;

    ReportePdf(int numero, String titulo, String nombreArchivo, String... encabezados) {
        this.numero = numero;
        this.titulo = titulo;
        this.nombreArchivo = nombreArchivo;
        this.encabezados = List.of(encabezados);
    }

    public static ReportePdf buscar(int numero) {
        for (ReportePdf reporte : values()) {
            if (reporte.numero == numero) {
                return reporte;
            }
        }
        throw new ResourceNotFoundException("Reporte no encontrado con número: " + numero);
    }

    public int numero() { return numero; }
    public String titulo() { return titulo; }
    public String nombreArchivo() { return nombreArchivo; }
    public List<String> encabezados() { return encabezados; }
}
