package eci.escuelaing.edu.co;

import java.net.URI;

public class HttpRequest {
    private final URI requestUri;

    public HttpRequest(URI requestUri) {
        this.requestUri = requestUri;
    }

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

    public String getPath() {
        return requestUri.getPath();
    }
}
