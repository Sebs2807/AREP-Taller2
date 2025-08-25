package eci.escuelaing.edu.co;

public class WebApplication {
    public static void main(String[] args) {
        HttpServer.staticfiles("webroot");

        HttpServer.get("/app/hello", (req, res) -> {
            String name = req.getQuery("name");
            return name != null ? "Hello " + name : "Hello World";
        });

        HttpServer.get("/app/pi", (req, res) -> String.valueOf(Math.PI));

        System.out.println("Aplicación corriendo en http://localhost:8080");
        try {
            HttpServer.main(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
