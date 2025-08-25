package eci.escuelaing.edu.co;

import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {
    private static final Map<String, Servicio> rutas = new HashMap<>();
    private static Path staticFilesRoot = Paths.get("www").toAbsolutePath().normalize();
    
    /**
     * Método para registrar un servicio en una ruta específica.
     * @param path Ruta donde se registrará el servicio.
     * @param servicio Servicio que se registrará en la ruta.
     */
    public static void get(String path, Servicio servicio) {
        rutas.put(path, servicio);
    };

    /**
     * Configura la carpeta desde la cual se servirán los archivos estáticos.
     * @param folder Ruta de la carpeta que contiene los archivos estáticos.
     */
    public static void staticfiles(String folder) {
        Path base = Paths.get(folder).toAbsolutePath().normalize();

        if (!base.toFile().exists()) {
            System.out.println("La carpeta '" + base + "' no existe. Se usará la carpeta por defecto ./www");
            base = Paths.get("www").toAbsolutePath().normalize();
        }

        staticFilesRoot = base;
        System.out.println("Carpeta de archivos estáticos configurada en: " + staticFilesRoot);
    }

    /**
     * Método principal que inicia el servidor HTTP.
     */
    public static void main(String[] args) throws IOException {
        staticfiles("/www");
        

        get("/hello", (req, res) -> {
            String name = req.getQuery("name");
            if (name == null || name.isEmpty()) {
                return "Hola Mundo";
            } else {
                return "{ \"mensaje\": \"Hola " + name + "\" }";
            }
        });

        get("/helloQuery", (req, res) -> {
            String name = req.getQuery("name");

            if (name != null){
                return "Hola"  + " " + name;
            }else{
                return "La sintaxis es incorrecta";
            }
        });

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(8080);
            System.out.println("Servidor iniciado en puerto 8080");
        } catch (IOException e) {
            System.err.println("El puerto 8080 está ocupado o no fue posible acceder a él.");
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

        // System.out.println("Petición: " + requestLine);

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

        // System.out.println("Path: " + path);
        if (query != null) {
            // System.out.println("Query: " + query);
        }

        while (in.ready()) {
            String header = in.readLine();
            if (header.isEmpty()) break;
        }

        if (rutas.containsKey(path)) {
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

        File file = staticFilesRoot.resolve(path.substring(1)).toFile();
        File errorFile = staticFilesRoot.resolve("404.html").toFile();

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
     * Procesa las peticiones a los servicios registrados en las rutas.
     * @param out Salida donde se escribirá la respuesta del servicio.
     * @param requestUri URI de la petición del cliente. 
     * @throws IOException en caso de error al escribir en el OutputStream o al procesar la petición del servicio.
     */
    private static void procesarServicios(OutputStream out, URI requestUri) throws IOException {
        HttpRequest request = new HttpRequest(requestUri);
        Servicio servicio = rutas.get(request.getPath());

        String response;
        String contentType;

        if (servicio != null) {
            HttpResponse res = new HttpResponse();
            response = servicio.peticiones(request, res);

            // Detectar si es JSON
            if (response.trim().startsWith("{") || response.trim().startsWith("[")) {
                contentType = "application/json; charset=UTF-8";
            } else {
                contentType = "text/plain; charset=UTF-8";
            }
        } else {
            response = "{ \"error\": \"Servicio no encontrado\" }";
            contentType = "application/json; charset=UTF-8";
        }

        PrintWriter outWriter = new PrintWriter(out, true);
        outWriter.println("HTTP/1.1 200 OK");
        outWriter.println("Content-Type: " + contentType);
        outWriter.println("Content-Length: " + response.getBytes().length);
        outWriter.println();
        outWriter.println(response);
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
