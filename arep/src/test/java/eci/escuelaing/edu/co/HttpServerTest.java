package eci.escuelaing.edu.co;

import org.junit.jupiter.api.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class HttpServerTest {

    private static ExecutorService executor;

    @BeforeAll
    static void setUp() {
        executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                HttpServer.main(new String[]{});
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
    }

    @AfterAll
    static void tearDown() {
        executor.shutdownNow();
    }

    @Test
    void testHelloApiJson() throws Exception {
        URL url = new URL("http://localhost:8080/hello?name=Sebastian");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        assertEquals(200, con.getResponseCode());
        assertEquals("application/json; charset=UTF-8", con.getContentType());

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String response = in.readLine();
            assertTrue(response.contains("\"Hola Sebastian\""));
        }
    }

    @Test
    void testHelloQueryPlainText() throws Exception {
        URL url = new URL("http://localhost:8080/helloQuery?name=Sebastian");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        assertEquals(200, con.getResponseCode());
        assertEquals("text/plain; charset=UTF-8", con.getContentType());

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String response = in.readLine();
            assertEquals("Hola Sebastian", response);
        }
    }

    @Test
    void testIndexHtmlExists() throws Exception {
        URL url = new URL("http://localhost:8080/");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        assertEquals(200, con.getResponseCode());
        assertEquals("text/html", con.getContentType());
    }

    @Test
    void testNotFoundStaticFile() throws Exception {
        URL url = new URL("http://localhost:8080/noExiste.html");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        assertEquals(404, con.getResponseCode());
        assertEquals("text/html", con.getContentType());
    }

    @Test
    void testServicioNoEncontrado() throws Exception {
        URL url = new URL("http://localhost:8080/app/noEndpoint");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        assertEquals(404, con.getResponseCode());
        assertEquals("text/html", con.getContentType());
    }

}
