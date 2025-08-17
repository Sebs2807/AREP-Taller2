package eci.escuelaing.edu.co;

import java.net.*;
import java.io.*;
import java.nio.file.Files;

public class HttpServer {
    /**
     * Método principal que inicia el servidor HTTP.
     */
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(36000);
            System.out.println("Servidor iniciado en puerto 36000");
        } catch (IOException e) {
            System.err.println("El puerto 36000 está ocupado o no fue posible acceder a él.");
        }

        boolean running = true;
        while (running) {
            try (Socket clientSocket = serverSocket.accept()) {
                handleClient(clientSocket);
            } catch (IOException e) {
                System.err.println("Error al aceptar cliente: " + e.getMessage());
            }
        }
        serverSocket.close();
    }

    /**
     * Clase que maneja las peticiones de los clientes.
     * @param clientSocket Socket del cliente que realiza la petición.
     * @throws IOException en caso de error al leer o escribir en el socket.
     */
    private static void handleClient(Socket clientSocket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        OutputStream out = clientSocket.getOutputStream();

        String requestLine = in.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            return;
        }

        System.out.println("Petición: " + requestLine);

        String[] parts = requestLine.split(" ");
        URI reqUri;
        try {
            reqUri = new URI(parts[1]);
        } catch (Exception e) {
            System.err.println("Error al parsear la URI: " + e.getMessage());
            return;
        }

        String path = reqUri.getPath();
        String query = reqUri.getQuery();

        System.out.println("Path: " + path);
        if (query != null) {
            System.out.println("Query: " + query);
        }

        while (in.ready()) {
            String header = in.readLine();
            if (header.isEmpty()) break;
        }

        if (path.startsWith("/app/")) {
            procesarServicios(out, reqUri);
        } else {
            ventanaPrincipal(out, path);
        }

        out.flush();
    }

    /**
     * Muestra la ventana principal del servidor, sirviendo archivos estáticos.
     * @param out Salida donde se escribirá la respuesta.
     * @param path Ruta solicitada por el cliente.
     * @throws IOException en caso de error al leer el archivo o escribir en el OutputStream.
     */
    private static void ventanaPrincipal(OutputStream out, String path) throws IOException {
        if (path.equals("/")) {
            path = "/index.html";
        }

        File file = new File("www" + path);
        File errorFile = new File("www/404.html");

        if (file.exists()) {
            String contentType = asignarContentType(file.getName());
            byte[] fileBytes = Files.readAllBytes(file.toPath());

            PrintWriter headerOut = new PrintWriter(out, false);
            headerOut.print("HTTP/1.1 200 OK\r\n");
            headerOut.print("Content-Type: " + contentType + "\r\n");
            headerOut.print("Content-Length: " + fileBytes.length + "\r\n");
            headerOut.print("\r\n");
            headerOut.flush();
            out.write(fileBytes);
        } else {
            byte[] fileBytes = Files.readAllBytes(errorFile.toPath());
            PrintWriter headerOut = new PrintWriter(out, false);
            headerOut.print("HTTP/1.1 404 Not Found\r\n");
            headerOut.print("Content-Type: text/html\r\n");
            headerOut.print("Content-Length: " + fileBytes.length + "\r\n");
            headerOut.print("\r\n");
            headerOut.flush();
            out.write(fileBytes);
        }
    }

    /**
     * Procesa las peticiones a los servicios definidos en la aplicación.
     * @param out Salida donde se escribirá la respuesta.
     * @param requestUri URI de la petición del cliente.
     * @throws IOException en caso de error al escribir en el OutputStream.
     */
    private static void procesarServicios(OutputStream out, URI requestUri) throws IOException {
        String path = requestUri.getPath();
        String response = "";

        if (path.equals("/app/hello")) {
            response = helloService(requestUri);
        } else {
            response = "{ \"error\": \"Servicio no encontrado\" }";
        }

        PrintWriter outWriter = new PrintWriter(out, true);
        outWriter.println("HTTP/1.1 200 OK");
        outWriter.println("Content-Type: application/json");
        outWriter.println("Content-Length: " + response.length());
        outWriter.println();
        outWriter.println(response);
    }

    /**
     * Genera una respuesta para el servicio.
     * @param requestUri URI de la petición del cliente.
     * @return Respuesta en formato JSON.
     */
    private static String helloService(URI requestUri) {
        String name = " ";

        if (requestUri.getQuery() != null && requestUri.getQuery().startsWith("name=")) {
            name = requestUri.getQuery().split("=")[1];
        }

        return "{ \"mensaje\": \"Hola " + name + "\" }";
    }

    private static String asignarContentType(String archivo) {
        String extension = archivo.substring(archivo.lastIndexOf(".") + 1);
        switch (extension) {
            case "html":
                return "text/html";
            case "css":
                return "text/css";
            case "js":
                return "application/javascript";
            case "png":
                return "image/png";
            case "jpg":
                return "image/jpeg";
            case "gif":
                return "image/gif";
            default:
                return " ";
        }
    }
}
