package eci.escuelaing.edu.co;

public interface Servicio {
    /**
     * Maneja las peticiones HTTP para un servicio específico.
     * @param request Objeto que contiene la información de la solicitud HTTP.
     * @param response Objeto que permite configurar la respuesta HTTP.
     * @return Respuesta en formato String.
     */
    String peticiones(HttpRequest request, HttpResponse response);
}
