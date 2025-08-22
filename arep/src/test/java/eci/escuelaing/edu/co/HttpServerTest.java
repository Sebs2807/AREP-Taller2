// package eci.escuelaing.edu.co;
// import org.junit.jupiter.api.*;
// import java.io.*;
// import java.net.*;
// import java.util.concurrent.*;

// import static org.junit.jupiter.api.Assertions.*;

// class HttpServerTest {

//     private static ExecutorService executor;

//     @BeforeAll
//     static void setUp() {
//         executor = Executors.newSingleThreadExecutor();
//         executor.submit(() -> {
//             try {
//                 HttpServer.main(new String[]{});
//             } catch (IOException e) {
//                 e.printStackTrace();
//             }
//         });

//         try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
//     }

//     @AfterAll
//     static void tearDown() {
//         executor.shutdownNow();
//     }

//     @Test
//     void testHelloApi() throws Exception {
//         URL url = new URL("http://localhost:36000/app/hello?name=Sebastian");
//         HttpURLConnection con = (HttpURLConnection) url.openConnection();
//         con.setRequestMethod("GET");

//         assertEquals(200, con.getResponseCode());

//         try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
//             String response = in.readLine();
//             assertTrue(response.contains("Hola Sebastian"));
//         }
//     }

//     @Test
//     void testIndexHtmlExists() throws Exception {
//         URL url = new URL("http://localhost:36000/");
//         HttpURLConnection con = (HttpURLConnection) url.openConnection();
//         con.setRequestMethod("GET");

//         assertEquals(200, con.getResponseCode());
//         assertEquals("text/html", con.getContentType());
//     }

//     @Test
//     void testNotFound() throws Exception {
//         URL url = new URL("http://localhost:36000/prueba.html");
//         HttpURLConnection con = (HttpURLConnection) url.openConnection();
//         con.setRequestMethod("GET");

//         assertEquals(404, con.getResponseCode());
//     }

//     @Test
//     void testServicioNoEncontrado() throws Exception {
//         URL url = new URL("http://localhost:36000/app/noEndpoint");
//         HttpURLConnection con = (HttpURLConnection) url.openConnection();
//         con.setRequestMethod("GET");

//         assertEquals("application/json", con.getContentType());

//         try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
//             String response = in.readLine();
//             assertTrue(response.contains("Servicio no encontrado"));
//         }
//     }
// }
