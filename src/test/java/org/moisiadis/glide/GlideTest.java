package org.moisiadis.glide;

import org.junit.jupiter.api.Test;
import org.moisiadis.glide.exceptions.NoContextException;
import org.moisiadis.glide.util.network.HTTPResponseWriter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

class GlideTest {
    private static final int PORT = 8080;

    private final HttpClient client = HttpClient.newHttpClient();

    @Test
    public void testGlideSimpleHandle() throws NoContextException, IOException, InterruptedException {
        Glide glide = new Glide(PORT);
        glide.setContext("/", (request) -> {
            request.sendResponse(200);
        });
        new Thread(() -> {
            try {
                glide.start();
            } catch (IOException | NoContextException e) {
                e.printStackTrace();
            }
        }).start();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + PORT + "/"))
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.statusCode());
    }
}