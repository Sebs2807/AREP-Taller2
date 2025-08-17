package eci.escuelaing.edu.co;

import java.net.*;
import java.io.*;
import java.nio.file.Files;

public class HttpServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(36000);
            System.out.println("Servidor iniciado en puerto 36000");
        } catch (IOException e) {
            System.err.println("El puerto 36000 esta ocupado o no fue posible acceder a él.");
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

        if (path.startsWith("/api/")) {
            API(out, path, query);
        } else {
            ventanaPrincipal(out, path);
        }

        out.flush();
    }

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

    private static void API(OutputStream out, String path, String query) throws IOException {
        String response = " ";

        if (path.startsWith("/api/hello")) {
            String name = null;

            if (query != null && query.startsWith("name=")) {
                name = query.split("name=")[1];
            }

            response = "{ \"message\": \"Hola " + name + "!\" }";
        }

        PrintWriter outWriter = new PrintWriter(out, true);
        outWriter.println("HTTP/1.1 200 OK");
        outWriter.println("Content-Type: application/json");
        outWriter.println("Content-Length: " + response.length());
        outWriter.println();
        outWriter.println(response);
    }


    private static String asignarContentType(String archivo) {
        String encabezado = " ";
        String extension = archivo.substring(archivo.indexOf(".") + 1);
        switch (extension) {
            case "html":
                encabezado = "text/html";
                break;
            case "css":
                encabezado = "text/css";
                break;
            case "js":
                encabezado = "application/javascript";
                break;
            case "png":
                encabezado = "image/png";
                break;
            case "jpg":
                encabezado = "image/jpeg";
                break;
            case "gif":
                encabezado = "image/gif";
                break;
            default:
                break;
        }
        return encabezado;
    }
}
