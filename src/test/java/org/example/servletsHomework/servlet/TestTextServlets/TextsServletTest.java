package org.example.servletsHomework.servlet.TestTextServlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.example.servletsHomework.dao.TextDao;
import org.example.servletsHomework.exception.BadRequestException;
import org.example.servletsHomework.exception.NotFoundException;
import org.example.servletsHomework.model.Texts;
import org.example.servletsHomework.model.User;
import org.example.servletsHomework.service.TextService;
import org.example.servletsHomework.servlet.TestTextServlets.MockClass.*;
import org.example.servletsHomework.servlet.TextsServlet;
import org.example.servletsHomework.storage.TokensAndUserStorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;


class TextsServletTest {
    private TextsServlet textsServlet;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockPrintWriter writer;


    @BeforeEach
    void setUp() {

        TextDao textDao = new MockTextDao2();
        TextService textService = new TextService(textDao);
        TokensAndUserStorage tokensAndUserStorage = new TokensAndUserStorage();
        textsServlet = new TextsServlet(new ObjectMapper(), new MockIdGenerator(1L), textService, tokensAndUserStorage);
        writer = new MockPrintWriter(new StringWriter());

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse(writer);

        String tokenUserOne = "validToken1";
        String tokenUserTwo = "validToken2";

        User userOne = new User("testUsername1", "testPassword1", 1L, System.currentTimeMillis());
        User userTwo = new User("testUsername2", "testPassword2", 2L, System.currentTimeMillis());

        tokensAndUserStorage.addToken(tokenUserOne, userOne);
        tokensAndUserStorage.addToken(tokenUserTwo, userTwo);

        textService.addText(1L, new Texts(1L, "text1"));
        textService.addText(2L, new Texts(1L, "text3"));
    }

    @Test
    void shouldReturnAllTexts() {

        request.setHeaders("token", "validToken1");
        request.setMethod("GET");
        request.setPathInfo("/");

        textsServlet.handleGet(request, response);

        Integer expectedStatus = 200;
        String expectedOutput = "[[Texts{id=1, value='Test'}]]";

        assertEquals(expectedOutput, writer.writtenStrings.toString());
        assertEquals(expectedStatus, response.getStatus());
    }

    @Test
    void shouldReturnTextById() {

        request.setHeaders("token", "validToken1");
        request.setMethod("GET");
        request.setPathInfo("/1");

        textsServlet.handleGet(request, response);

        Integer expectedStatus = 200;
        String expectedOutput = "[Text with id 1: TestText]";

        assertEquals(expectedOutput, writer.writtenStrings.toString());
        assertEquals(expectedStatus, response.getStatus());
    }


    @Test
    void shouldThrowBadRequestExceptionWhenRequestWithInvalidId() {

        request.setHeaders("token", "validToken1");
        request.setMethod("GET");

        List<String> invalidPaths = List.of("/a", "/ ", "/!", "%", "1/2/3/");

        for (String path : invalidPaths) {
            request.setPathInfo(path);
            BadRequestException exception = assertThrows(BadRequestException.class, () -> textsServlet.handleGet(request, response));
            String expectedMessage = "Invalid request path: " + path;
            assertEquals(expectedMessage, exception.getMessage());
        }
    }

    @Test
    void shouldThrowNotFoundExceptionWhenTextByIdIsNotExist() {

        String id = "44";
        request.setHeaders("token", "validToken1");
        request.setMethod("GET");
        request.setPathInfo("/" + id);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> textsServlet.handleGet(request, response));

        String expectedMessage = "Text not found with id " + id;
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void shouldDeleteAllTexts() {
        request.setHeaders("token", "validToken1");
        request.setMethod("DELETE");
        request.setPathInfo("/deleteAll");

        textsServlet.handleDelete(request, response);

        Integer expectedStatus = 200;
        String expectedOutput = "[All text has been deleted]";

        assertEquals(expectedStatus, response.getStatus());
        assertEquals(expectedOutput, writer.writtenStrings.toString());
    }

    @Test
    void shouldDeleteTextById() {

        request.setHeaders("token", "validToken1");
        request.setMethod("DELETE");
        request.setPathInfo("/1");

        textsServlet.handleDelete(request, response);

        Integer expectedStatus = 200;
        String expectedOutput = "[Text: TestText with id: 1 has been deleted]";

        assertEquals(expectedOutput, writer.writtenStrings.toString());
        assertEquals(expectedStatus, response.getStatus());
    }

    //todo:  shouldReturnCreatedResponseForValidTextPost
    @Test
    void shouldPostText() {

        String jsonRequest = """
                {
                "text": "TestText123"
                }
                """;


        request.setHeaders("token", "validToken1");
        request.setMethod("POST");
        BufferedReader reader = new BufferedReader(new StringReader(jsonRequest));
        request.setBufferedReader(reader);

        response = new MockHttpServletResponse(writer);

        textsServlet.handlePost(request, response);

        Integer expectedStatus = HttpServletResponse.SC_CREATED;
        String expectedOutput = "{\"text\":\"TestText123\",\"message\":\"Text saved with id: 1\",\"textId\":1}";

        assertEquals(expectedStatus, response.getStatus());
        assertEquals(expectedOutput, writer.writtenStrings.get(0));

    }

    //todo:: whenRequestHasInvalidTextMessage
    @Test
    void shouldThrowBadRequestExceptionWhenRequestWithEmptyBody() {

        String jsonRequest = """
                {
                "text": " "
                }
                """;

        request = new MockHttpServletRequest();
        request.setHeaders("token", "validToken1");
        request.setMethod("POST");
        BufferedReader reader = new BufferedReader(new StringReader(jsonRequest));
        request.setBufferedReader(reader);

        response = new MockHttpServletResponse(writer);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> textsServlet.handlePost(request, response));

        String expectedOutput = "Text cannot be empty";
        assertEquals(expectedOutput, exception.getMessage());
    }

}