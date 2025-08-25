package eci.escuelaing.edu.co;

import java.net.URI;

public class HttpRequest {
    private final URI requestUri;

    /**
     * Constructor de la clase HttpRequest.
     * @param requestUri URI de la solicitud HTTP.
     */
    public HttpRequest(URI requestUri) {
        this.requestUri = requestUri;
    }

    /**
     * Obtiene el valor de un parámetro de consulta específico de la URI.
     * @param key Nombre del parámetro de consulta.
     * @return Valor del parámetro de consulta o null si no existe.
     */
    public String getQuery(String key) {
        if (requestUri.getQuery() != null) {
            String[] params = requestUri.getQuery().split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2 && keyValue[0].equals(key)) {
                    return keyValue[1];
                }
            }
        }
        return null;
    }

    /**
     * Obtiene la ruta de la solicitud.
     * @return Ruta de la solicitud.
     */
    public String getPath() {
        return requestUri.getPath();
    }
}
