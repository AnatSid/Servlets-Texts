package org.example.servletstexts.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.servletstexts.dao.TextsDAO;
import org.example.servletstexts.exception.BadRequestException;
import org.example.servletstexts.exception.InternalServerErrorException;
import org.example.servletstexts.exception.NotFoundException;
import org.example.servletstexts.handlers.TextCreateRequest;
import org.example.servletstexts.handlers.TextCreateResponse;
import org.example.servletstexts.service.TextService;

import java.io.IOException;
import java.io.PrintWriter;


@WebServlet(name = "texts-servlet", value = "/texts/*")
public class TextsServlet extends BaseServlet {

    private final transient TextsDAO textsDAO = new TextsDAO();
    private final transient TextService textService = new TextService(textsDAO);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    private static final String EMPTY_TEXT_ERROR = "Text cannot be empty";
    private static final String INVALID_REQUEST_PATH = "Invalid request path.";
    private static final String INVALID_FORMAT = "Invalid id format";
    private static final String TEXT_NOT_FOUND = "Text not found";


    @Override
    protected void handleGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            PrintWriter respWriter = resp.getWriter();
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                respWriter.write(textService.getAll());
            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    respWriter.write(handleTextRequest(pathParts[1], "get"));
                } else {
                    throw new BadRequestException(INVALID_REQUEST_PATH + " You entered an extra '/'");
                }
            }
        } catch (IOException ex) {
            throw new InternalServerErrorException(INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    protected void handleDelete(HttpServletRequest req, HttpServletResponse resp) {
        try {
            PrintWriter respWriter = resp.getWriter();
            String pathInfo = req.getPathInfo();
            if (pathInfo.equals("/deleteAll")) {
                textService.deleteAll();
                respWriter.write("All text has been deleted");
            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    respWriter.write(handleTextRequest(pathParts[1], "delete"));
                } else {
                    throw new BadRequestException(INVALID_REQUEST_PATH + " You entered an extra '/'");
                }
            }
        } catch (IOException ex) {
            throw new InternalServerErrorException(INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void handlePost(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");

        try {
            PrintWriter respWriter = resp.getWriter();
            TextCreateRequest requestBody = objectMapper.readValue(req.getReader(), TextCreateRequest.class);
            String text = requestBody.getText();

            if (text == null || text.trim().isEmpty()) {
                throw new BadRequestException(EMPTY_TEXT_ERROR);
            } else {
                TextCreateResponse responseBody = new TextCreateResponse();
                int id = textService.addText(text);
                responseBody.setText(text);
                responseBody.setMessage("Text saved with id: " + id);
                responseBody.setTextId(id);

                respWriter.write(objectMapper.writeValueAsString(responseBody));
            }
        } catch (IOException e) {
            throw new InternalServerErrorException(INTERNAL_SERVER_ERROR);
        }
    }

    private String handleTextRequest(String id, String action) {
        int textId = parseStringToInt(id);
        String text = textService.getTextById(textId);
        if (text == null) {
            throw new NotFoundException(TEXT_NOT_FOUND + " with id " + textId);
        } else {
            if (action.equals("delete")) {
                textService.deleteTextById(textId);
                return "Text with id " + textId + " has been deleted";
            } else if (action.equals("get")) {
                return "Text with id " + textId + ": " + text;
            } else {
                throw new BadRequestException("Invalid action: " + action);
            }
        }
    }

    private int parseStringToInt(String id) {
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            throw new BadRequestException(INVALID_FORMAT);
        }
    }

}
