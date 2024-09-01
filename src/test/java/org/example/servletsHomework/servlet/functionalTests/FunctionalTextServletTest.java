package org.example.servletsHomework.servlet.functionalTests;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class FunctionalTextServletTest {

    String baseUrl = "http://localhost:8080/";
    String registerUrl = "register";
    String textUrl = "texts/";
    String token;
    HttpClient client;

    @BeforeEach
    void setUp() throws URISyntaxException, IOException, InterruptedException {

        client = HttpClient.newHttpClient();

        HttpRequest registerRequest = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + registerUrl))
                .POST(HttpRequest.BodyPublishers.noBody())
                .header("username", "admin" + System.currentTimeMillis())
                .header("password", "1234")
                .build();

        HttpResponse<String> registerResponse = client.send(registerRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpServletResponse.SC_CREATED, registerResponse.statusCode());

        token = registerResponse.body();
    }

    @Test
    void shouldReturnUnauthorizedStatusForRequestsWithoutToken() throws URISyntaxException, IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + textUrl))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String expectedOutput = "Token does not exist. Need registration";

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.statusCode());
        assertEquals(expectedOutput, response.body());
    }

    @Test
    void shouldReturnUnauthorizedStatusWhenTokenIsInvalid() throws URISyntaxException, IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + textUrl))
                .GET()
                .header("token", "INVALID_TOKEN")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String expectedOutput = "Token does not exist. Need registration";
        assertEquals(expectedOutput, response.body());
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.statusCode());
    }

    @Test
    void shouldReturnEmptyList() throws IOException, InterruptedException, URISyntaxException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + textUrl))
                .GET()
                .header("token", token)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String expectedOutput = "[]";
        assertEquals(expectedOutput, response.body());
        assertEquals(HttpServletResponse.SC_OK, response.statusCode());
    }

    @Test
    void shouldReturnNotFoundWhenTextDoesNotExist() throws URISyntaxException, IOException, InterruptedException {

        long textId = 100L;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + textUrl + "/" + textId))
                .GET()
                .header("token", token)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String expectedOutput = "Text not found with id " + textId;
        assertEquals(expectedOutput, response.body());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.statusCode());
    }

    @Test
    void shouldReturnBadRequestWhenInvalidPath() throws URISyntaxException, IOException, InterruptedException {

        List<String> invalidPaths = List.of("/afds", "/!", "/@@", "/1/2/3/");

        for (String path : invalidPaths) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(baseUrl + textUrl + path))
                    .GET()
                    .header("token", token)
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String expectedOutput = "Invalid request path: " + path;
            assertEquals(expectedOutput, response.body());
            assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.statusCode());
        }
    }

    @Test
    void shouldPostTextThenGetById() throws URISyntaxException, IOException, InterruptedException {
        String jsonRequest = """
                {
                "text": "TestText123"
                }
                """;

        HttpRequest requestPost = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + textUrl))
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .header("token", token)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> responsePost = client.send(requestPost, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpServletResponse.SC_CREATED, responsePost.statusCode());

        String responseBody = responsePost.body();

        String messageStart = "\"textId\":";
        int idStartIndex = responseBody.indexOf(messageStart) + messageStart.length();
        int idEndIndex = responseBody.indexOf("}", idStartIndex);
        String textId = responseBody.substring(idStartIndex, idEndIndex).trim();

        assertTrue(responseBody.contains("\"text\":\"TestText123\""));
        assertTrue(responseBody.contains("\"message\":\"Text saved with id: " + textId));

        HttpRequest requestGet = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + textUrl + textId))
                .GET()
                .header("token", token)
                .build();

        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        String expectedOutput = "Text with id " + textId + ": TestText123";
        assertEquals(expectedOutput, responseGet.body());
        assertEquals(HttpServletResponse.SC_OK, responseGet.statusCode());

    }

    @Test
    void shouldPostTextThenDeleteById() throws URISyntaxException, IOException, InterruptedException {
        String jsonRequest = """
                {
                "text": "TestText123"
                }
                """;

        HttpRequest requestPost = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + textUrl))
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .header("token", token)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> responsePost = client.send(requestPost, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpServletResponse.SC_CREATED, responsePost.statusCode());

        String responseBody = responsePost.body();
        String messageStart = "\"textId\":";
        int idStartIndex = responseBody.indexOf(messageStart) + messageStart.length();
        int idEndIndex = responseBody.indexOf("}", idStartIndex);
        String textId = responseBody.substring(idStartIndex, idEndIndex).trim();

        assertTrue(responseBody.contains("\"text\":\"TestText123\""));
        assertTrue(responseBody.contains("\"message\":\"Text saved with id: " + textId));

        HttpRequest requestGet = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + textUrl + textId))
                .DELETE()
                .header("token", token)
                .build();

        HttpResponse<String> responseDelete = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        String expectedOutput = "Text with id: " + textId + " has been deleted";
        assertEquals(expectedOutput, responseDelete.body());
        assertEquals(HttpServletResponse.SC_OK, responseDelete.statusCode());
    }

    @Test
    void shouldPostTextThenDeleteAll() throws URISyntaxException, IOException, InterruptedException {
        String jsonRequest = """
                {
                "text": "TestText123"
                }
                """;

        HttpRequest requestPost = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + textUrl))
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .header("token", token)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> responsePost = client.send(requestPost, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpServletResponse.SC_CREATED, responsePost.statusCode());

        String responseBody = responsePost.body();

        String messageStart = "\"textId\":";
        int idStartIndex = responseBody.indexOf(messageStart) + messageStart.length();
        int idEndIndex = responseBody.indexOf("}", idStartIndex);
        String textId = responseBody.substring(idStartIndex, idEndIndex).trim();

        assertTrue(responseBody.contains("\"text\":\"TestText123\""));
        assertTrue(responseBody.contains("\"message\":\"Text saved with id: " + textId));

        HttpRequest requestDeleteAll = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + textUrl + "/deleteAll"))
                .DELETE()
                .header("token", token)
                .build();

        HttpResponse<String> responseDeleteAll = client.send(requestDeleteAll, HttpResponse.BodyHandlers.ofString());

        String expectedOutputDeleteAll = "All text has been deleted";
        assertEquals(expectedOutputDeleteAll, responseDeleteAll.body());
        assertEquals(HttpServletResponse.SC_OK, responseDeleteAll.statusCode());

        HttpRequest requestGetAll = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + textUrl))
                .GET()
                .header("token", token)
                .build();

        HttpResponse<String> response = client.send(requestGetAll, HttpResponse.BodyHandlers.ofString());

        String expectedOutputGetAll = "[]";
        assertEquals(expectedOutputGetAll, response.body());
        assertEquals(HttpServletResponse.SC_OK, response.statusCode());
    }


    @Test
    void shouldPostTextThenGetAllTexts() throws URISyntaxException, IOException, InterruptedException {

        String jsonRequest = """
                {
                "text": "TestText123"
                }
                """;

        List<String> expectedId = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            HttpRequest requestPost = HttpRequest.newBuilder()
                    .uri(new URI(baseUrl + textUrl))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .header("token", token)
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> responsePost = client.send(requestPost, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpServletResponse.SC_CREATED, responsePost.statusCode());

            String responseBody = responsePost.body();
            assertTrue(responseBody.contains("\"text\":\"TestText123\""));
            assertTrue(responseBody.contains("\"message\":\"Text saved with id:"));

            String messageStart = "\"textId\":";
            int idStartIndex = responseBody.indexOf(messageStart) + messageStart.length();
            int idEndIndex = responseBody.indexOf("}", idStartIndex);
            String textId = responseBody.substring(idStartIndex, idEndIndex).trim();

            expectedId.add(textId);
        }

        HttpRequest requestGetAll = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + textUrl))
                .GET()
                .header("token", token)
                .build();

        HttpResponse<String> responseGet = client.send(requestGetAll, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpServletResponse.SC_OK, responseGet.statusCode());

        String responseBody = responseGet.body();

        for (String id : expectedId) {
            assertTrue(responseBody.contains("textId=" + id));
            assertTrue(responseBody.contains("value='TestText123'"));
        }
    }

    @Test
    void shouldRedirectToLoginWhenTokenExpires() throws Exception {

        Thread.sleep(185000);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + textUrl))
                .GET()
                .header("token", token)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("Token expired. Need to log in", response.body());
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.statusCode());
    }

}
