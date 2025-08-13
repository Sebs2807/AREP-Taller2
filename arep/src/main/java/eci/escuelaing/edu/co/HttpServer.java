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
            System.err.println("No se pudo abrir el puerto 36000.");
            System.exit(1);
        }

        boolean running = true;
        while (running) {
            try (Socket clientSocket = serverSocket.accept()) {
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());
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
        String method = parts[0];
        String path = parts[1];

        // Consumir el resto de cabeceras HTTP
        while (in.ready()) {
            String header = in.readLine();
            if (header.isEmpty()) break;
        }

        if (path.startsWith("/api/")) {
            handleApi(out, path);
        } else {
            serveFile(out, path);
        }

        out.flush();
    }

    private static void serveFile(OutputStream out, String path) throws IOException {
        if (path.equals("/")) {
            path = "/index.html";
        }

        File file = new File("www" + path);
        if (file.exists() && !file.isDirectory()) {
            String contentType = guessContentType(file.getName());
            byte[] fileBytes = Files.readAllBytes(file.toPath());

            PrintWriter headerOut = new PrintWriter(out, false);
            headerOut.print("HTTP/1.1 200 OK\r\n");
            headerOut.print("Content-Type: " + contentType + "\r\n");
            headerOut.print("Content-Length: " + fileBytes.length + "\r\n");
            headerOut.print("Connection: close\r\n");
            headerOut.print("\r\n");
            headerOut.flush();

            out.write(fileBytes);
        } else {
            String errorMsg = "<h1>404 Not Found</h1>";
            PrintWriter outWriter = new PrintWriter(out, true);
            outWriter.println("HTTP/1.1 404 Not Found");
            outWriter.println("Content-Type: text/html");
            outWriter.println("Content-Length: " + errorMsg.length());
            outWriter.println();
            outWriter.println(errorMsg);
        }
    }

    private static void handleApi(OutputStream out, String path) throws IOException {
        String response;

        if (path.startsWith("/api/hello")) {
            String name = "Mundo";
            if (path.contains("?name=")) {
                name = path.split("\\?name=")[1];
            }
            response = "{ \"message\": \"Hola " + name + "!\" }";
        } else {
            response = "{ \"error\": \"Endpoint no encontrado\" }";
        }

        PrintWriter outWriter = new PrintWriter(out, true);
        outWriter.println("HTTP/1.1 200 OK");
        outWriter.println("Content-Type: application/json");
        outWriter.println("Content-Length: " + response.length());
        outWriter.println();
        outWriter.println(response);
    }

    private static String guessContentType(String fileName) {
        if (fileName.endsWith(".html") || fileName.endsWith(".htm")) return "text/html";
        if (fileName.endsWith(".css")) return "text/css";
        if (fileName.endsWith(".js")) return "application/javascript";
        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
        if (fileName.endsWith(".gif")) return "image/gif";
        return "application/octet-stream";
    }
}
