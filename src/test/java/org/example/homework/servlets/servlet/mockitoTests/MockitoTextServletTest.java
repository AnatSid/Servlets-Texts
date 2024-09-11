package org.example.homework.servlets.servlet.mockitoTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.homework.servlets.dao.TextDao;
import org.example.homework.servlets.service.IdGenerator;
import org.example.homework.servlets.model.Text;
import org.example.homework.servlets.service.TextService;
import org.example.homework.servlets.servlet.TextsServlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MockitoTextServletTest {

    private TextsServlet textsServlet;
    private TextDao textDao;
    private TextService textService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter stringWriter;
    private PrintWriter writer;
    private IdGenerator idGenerator;

    @BeforeEach
    void setUp() {
        textDao = Mockito.mock(TextDao.class);
        textService = new TextService(textDao);
        idGenerator = Mockito.mock(IdGenerator.class);
        textsServlet = new TextsServlet(new ObjectMapper(), idGenerator, textService);

        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);

    }


    @Test
    void shouldReturnAllTexts() throws IOException {

        Long userId = 1L;
        Text text1 = new Text(1L, "TestText", userId);
        Text text2 = new Text(2L, "TestText2", userId);

        when(request.getMethod()).thenReturn("GET");
        when(request.getPathInfo()).thenReturn("/");
        when(request.getAttribute("userId")).thenReturn(userId);

        when(textDao.getAllTexts(userId)).thenReturn(List.of(text1, text2));
        when(response.getWriter()).thenReturn(writer);

        textsServlet.handleGet(request, response);

        String expectedOutput = "[Text{textId=1, value='TestText', userId=1}, Text{textId=2, value='TestText2', userId=1}]";
        assertEquals(expectedOutput, stringWriter.toString());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void shouldReturnEmptyList() throws IOException {

        Long userId = 1L;

        when(request.getMethod()).thenReturn("GET");
        when(request.getPathInfo()).thenReturn("/");
        when(request.getAttribute("userId")).thenReturn(userId);

        when(textDao.getAllTexts(userId)).thenReturn(List.of());
        when(response.getWriter()).thenReturn(writer);

        textsServlet.handleGet(request, response);

        String expectedOutput = "[]";
        assertEquals(expectedOutput, stringWriter.toString());
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(textDao).getAllTexts(userId);
    }

    @Test
    void shouldReturnTextById() throws IOException {

        Long userId = 1L;
        Long textId = 10L;

        Text text = new Text(textId, "TestTextWithId" + textId, userId);

        when(request.getMethod()).thenReturn("GET");
        when(request.getPathInfo()).thenReturn("/" + textId);
        when(request.getAttribute("userId")).thenReturn(userId);

        when(response.getWriter()).thenReturn(writer);

        when(textDao.getTextById(userId, textId)).thenReturn(text);

        textsServlet.handleGet(request, response);

        String expectedOutput = "Text with id 10: TestTextWithId10";
        assertEquals(expectedOutput, stringWriter.toString());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void shouldReturnBadRequestStatusWhenRequestWithInvalidId() throws IOException {

        List<String> invalidPaths = List.of("/a", "/!", "/@@", "1/2/3/");
        Long userId = 1L;

        for (String path : invalidPaths) {

            PrintWriter mockWriter = Mockito.mock(PrintWriter.class);
            when(response.getWriter()).thenReturn(mockWriter);
            when(request.getMethod()).thenReturn("GET");
            when(request.getAttribute("userId")).thenReturn(userId);
            when(request.getPathInfo()).thenReturn(path);

            textsServlet.service(request, response);

            String expectedMessage = "Invalid request path: " + path;

            verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
            verify(mockWriter).write(expectedMessage);
            reset(response);

        }
    }


    @Test
    void shouldDeleteAllTexts() throws Exception {

        Long userId = 40L;

        when(request.getMethod()).thenReturn("DELETE");
        when(request.getPathInfo()).thenReturn("/deleteAll");
        when(request.getAttribute("userId")).thenReturn(userId);

        when(response.getWriter()).thenReturn(writer);

        textsServlet.handleDelete(request, response);

        String expectedMessage = "All text has been deleted";
        assertEquals(expectedMessage, stringWriter.toString());
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(textDao).deleteAll(userId);
    }

    @Test
    void shouldDeleteTextById() throws IOException {

        Long userId = 1L;
        Long textId = 10L;

        when(request.getMethod()).thenReturn("DELETE");
        when(request.getPathInfo()).thenReturn("/" + textId);
        when(request.getAttribute("userId")).thenReturn(userId);

        when(response.getWriter()).thenReturn(writer);

        textsServlet.handleDelete(request, response);

        String expectedOutput = "Text with id: 10 has been deleted";
        assertEquals(expectedOutput, stringWriter.toString());
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(textDao).delete(userId, textId);
    }

    @Test
    void shouldPostTextAndReturnSuccessResponse() throws Exception {

        Long userId = 1L;
        Long textId = 1L;
        Text text = new Text(textId, "TestText123", userId);

        String jsonRequest = "{\n" +
                "\"text\": \"TestText123\"\n" +
                "}";

        when(request.getAttribute("userId")).thenReturn(userId);
        when(request.getMethod()).thenReturn("POST");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));
        when(response.getWriter()).thenReturn(writer);

        when(idGenerator.getNextId()).thenReturn(textId);
        when(textDao.add(userId, text)).thenReturn(true);

        textsServlet.handlePost(request, response);

        String expectedOutput = "{\"text\":\"TestText123\",\"message\":\"Text saved with id: 1\",\"textId\":1}";
        assertEquals(expectedOutput, stringWriter.toString());
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(textDao).add(eq(1L), any(Text.class));
    }

    @Test
    void shouldReturnBadRequestStatusWhenRequestWithEmptyBody() throws IOException {

        String jsonRequest = "{\n" +
                "\"text\": \"\"\n" +
                "}";

        when(request.getAttribute("userId")).thenReturn(1L);
        when(request.getMethod()).thenReturn("POST");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));

        when(response.getWriter()).thenReturn(writer);

        textsServlet.service(request, response);

        String expectedOutput = "Text cannot be empty";
        assertEquals(expectedOutput, stringWriter.toString());
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void shouldReturnInternalServerErrorWhenIOException() throws IOException {
        when(request.getMethod()).thenReturn("POST");
        when(request.getReader()).thenThrow(new IOException());
        when(response.getWriter()).thenReturn(writer);

        textsServlet.service(request, response);

        assertEquals("Internal Server Error", stringWriter.toString());
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

}