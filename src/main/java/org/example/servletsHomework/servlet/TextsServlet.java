package org.example.servletsHomework.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.servletsHomework.exception.BadRequestException;
import org.example.servletsHomework.exception.InternalServerErrorException;
import org.example.servletsHomework.exception.NotFoundException;
import org.example.servletsHomework.handler.TextCreateRequest;
import org.example.servletsHomework.handler.TextCreateResponse;
import org.example.servletsHomework.model.IdGenerator;
import org.example.servletsHomework.model.Texts;
import org.example.servletsHomework.service.TextService;
import org.example.servletsHomework.storage.TokensAndUserStorage;

import java.io.IOException;
import java.io.PrintWriter;

public class TextsServlet extends BaseServlet {

    private final ObjectMapper objectMapper;
    private final IdGenerator idGenerator;
    private final TextService textService;
    private final TokensAndUserStorage tokensAndUserStorage;

    public TextsServlet(ObjectMapper objectMapper, IdGenerator idGenerator, TextService textService, TokensAndUserStorage tokensAndUserStorage) {
        this.objectMapper = objectMapper;
        this.idGenerator = idGenerator;
        this.textService = textService;
        this.tokensAndUserStorage = tokensAndUserStorage;
    }

    private static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    private static final String EMPTY_TEXT_ERROR = "Text cannot be empty";
    private static final String INVALID_REQUEST_PATH = "Invalid request path";
    private static final String TEXT_NOT_FOUND = "Text not found";
    private static final String HEADER_NAME_TOKEN = "token";


    @Override
    public void handlePost(HttpServletRequest req, HttpServletResponse resp) {

        resp.setContentType("application/json");
        String token = req.getHeader(HEADER_NAME_TOKEN);

        try {
            PrintWriter respWriter = resp.getWriter();

            TextCreateRequest requestBody = objectMapper.readValue(req.getReader(), TextCreateRequest.class);
            TextCreateResponse responseBody = new TextCreateResponse();
            String text = requestBody.getText();

            if (text == null || text.trim().isEmpty()) {
                throw new BadRequestException(EMPTY_TEXT_ERROR);
            } else {
                long userId = tokensAndUserStorage.getUserIdByToken(token);
                long textId = idGenerator.getNextId();
                textService.addText(userId, new Texts(textId, text));

                responseBody.setTextId(textId);
                responseBody.setText(text);
                responseBody.setMessage("Text saved with id: " + textId);

                resp.setStatus(HttpServletResponse.SC_CREATED);
                respWriter.write(objectMapper.writeValueAsString(responseBody));
            }
        } catch (IOException e) {
            throw new InternalServerErrorException(INTERNAL_SERVER_ERROR);
        }

    }


    @Override
    public void handleGet(HttpServletRequest req, HttpServletResponse resp) {

        String token = req.getHeader(HEADER_NAME_TOKEN);
        long userId = tokensAndUserStorage.getUserIdByToken(token);

        try {
            PrintWriter respWriter = resp.getWriter();
            String pathInfo = req.getPathInfo();
            if (pathInfo.equals("/")) {
                respWriter.write(textService.getAllTexts(userId).toString());
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    respWriter.write(handleTextRequest(userId, pathParts[1], "get", pathInfo));
                    resp.setStatus(HttpServletResponse.SC_OK);
                } else {
                    throw new BadRequestException(INVALID_REQUEST_PATH + ": " + pathInfo);
                }
            }
        } catch (IOException ex) {
            throw new InternalServerErrorException(INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void handleDelete(HttpServletRequest req, HttpServletResponse resp) {

        String token = req.getHeader(HEADER_NAME_TOKEN);
        long userId = tokensAndUserStorage.getUserIdByToken(token);

        try {
            PrintWriter respWriter = resp.getWriter();
            String pathInfo = req.getPathInfo();
            if (pathInfo.equals("/deleteAll")) {
                textService.deleteAll(userId);
                resp.setStatus(HttpServletResponse.SC_OK);
                respWriter.write("All text has been deleted");
            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    respWriter.write(handleTextRequest(userId, pathParts[1], "delete", pathInfo));
                    resp.setStatus(HttpServletResponse.SC_OK);
                    textService.deleteTextById(userId, Long.parseLong(pathParts[1]));
                } else {
                    throw new BadRequestException(INVALID_REQUEST_PATH + ": " + pathInfo);
                }
            }
        } catch (IOException ex) {
            throw new InternalServerErrorException(INTERNAL_SERVER_ERROR);
        }
    }


    private String handleTextRequest(Long idUser, String idText, String action, String pathInfo) {
        Long textId = parseStringToLong(idText, pathInfo);
        Texts texts = textService.getTextById(idUser, textId);

        if (texts == null) {
            throw new NotFoundException(TEXT_NOT_FOUND + " with id " + textId);
        } else {
            if (action.equals("delete")) {
                return "Text: " + texts.getValue() + " with id: " + textId + " has been deleted";
            } else if (action.equals("get")) {
                return "Text with id " + textId + ": " + texts.getValue();
            } else {
                throw new BadRequestException("Invalid action: " + action);
            }
        }
    }

    private Long parseStringToLong(String id, String pathInfo) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new BadRequestException(INVALID_REQUEST_PATH + ": " + pathInfo);
        }
    }
}
