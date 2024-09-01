package org.example.servletsHomework.servlet.stubTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.example.servletsHomework.model.Text;
import org.example.servletsHomework.model.User;
import org.example.servletsHomework.service.TextService;
import org.example.servletsHomework.servlet.stubTests.stubClass.*;
import org.example.servletsHomework.servlet.TextsServlet;
import org.example.servletsHomework.storage.TokensAndUserStorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;


class StubTextServletTest {
    private TextsServlet textsServlet;
    private StubTextDao textDao;
    private StubHttpServletRequest request;
    private StubHttpServletResponse response;
    private StubPrintWriter writer;


    @BeforeEach
    void setUp() {

        textDao = new StubTextDao();
        TextService textService = new TextService(textDao);
        TokensAndUserStorage tokensAndUserStorage = new TokensAndUserStorage();
        textsServlet = new TextsServlet(new ObjectMapper(), new StubIdGenerator(1L), textService);
        writer = new StubPrintWriter(new StringWriter());

        request = new StubHttpServletRequest();
        response = new StubHttpServletResponse(writer);

        String tokenUserOne = "validToken1";
        User userOne = new User("testUsername1", "testPassword1", 1L, System.currentTimeMillis());
        tokensAndUserStorage.addToken(tokenUserOne, userOne);

        textService.addText(1L, new Text(1L, "text1", 1L));

    }

    @Test
    void shouldReturnAllTexts() {

        request.setAttribute("userId", 1L);
        request.setMethod("GET");
        request.setPathInfo("/");

        textsServlet.handleGet(request, response);

        String expectedOutput = "[Text{textId=1, value='Test', userId=1}]";

        assertEquals(expectedOutput, writer.writtenStrings.get(0));
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertTrue(textDao.isGetAllCalled());
    }


    @Test
    void shouldReturnTextById() {

        request.setMethod("GET");
        request.setPathInfo("/1");

        textsServlet.handleGet(request, response);

        String expectedOutput = "Text with id 1: TestText";

        assertEquals(expectedOutput, writer.writtenStrings.get(0));
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertTrue(textDao.isGetByIdCalled());
    }


    @Test
    void shouldReturnBadRequestStatusWhenRequestWithInvalidId() {

        request.setMethod("GET");

        List<String> invalidPaths = List.of("/a", "/ ", "/!", "%", "1/2/3/");

        for (String path : invalidPaths) {
            request.setPathInfo(path);
            textsServlet.service(request, response);

            String expectedMessage = "Invalid request path: " + path;

            assertEquals(expectedMessage, writer.writtenStrings.get(0));
            assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
            assertFalse(textDao.isGetByIdCalled());

            writer.writtenStrings.clear();
        }
    }

    @Test
    void shouldReturnNotFoundStatusWhenTextIdDoesNotExist() {

        String id = "44";
        request.setMethod("GET");
        request.setPathInfo("/" + id);

        textsServlet.service(request, response);

        String expectedMessage = "Text not found with id " + id;
        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
        assertEquals(expectedMessage, writer.writtenStrings.get(0));
        assertTrue(textDao.isGetByIdCalled());
    }

    @Test
    void shouldDeleteAllTexts() {

        request.setMethod("DELETE");
        request.setPathInfo("/deleteAll");

        textsServlet.handleDelete(request, response);

        String expectedOutput = "All text has been deleted";

        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertEquals(expectedOutput, writer.writtenStrings.get(0));
        assertTrue(textDao.isDeleteAllCalled());
    }

    @Test
    void shouldDeleteTextById() {

        request.setMethod("DELETE");
        request.setPathInfo("/1");

        textsServlet.handleDelete(request, response);

        String expectedOutput = "Text with id: 1 has been deleted";

        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertEquals(expectedOutput, writer.writtenStrings.get(0));
        assertTrue(textDao.isDeleteCalled());
    }

    @Test
    void shouldPostTextAndReturnSuccessResponse() {

        String jsonRequest = """
                {
                "text": "TestText123"
                }
                """;

        request.setMethod("POST");
        BufferedReader reader = new BufferedReader(new StringReader(jsonRequest));
        request.setBufferedReader(reader);

        textsServlet.handlePost(request, response);

        String expectedOutput = "{\"text\":\"TestText123\",\"message\":\"Text saved with id: 1\",\"textId\":1}";

        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());
        assertEquals(expectedOutput, writer.writtenStrings.get(0));
        assertTrue(textDao.isAddCalled());
    }

    @Test
    void shouldReturnBadRequestStatusWhenRequestWithEmptyBody() {

        String jsonRequest = """
                {
                "text": ""
                }
                """;

        request.setMethod("POST");
        BufferedReader reader = new BufferedReader(new StringReader(jsonRequest));
        request.setBufferedReader(reader);

        textsServlet.service(request, response);

        String expectedOutput = "Text cannot be empty";

        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
        assertEquals(expectedOutput, writer.writtenStrings.get(0));
    }

}