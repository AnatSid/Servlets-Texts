package org.example.homework.servlets.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.homework.servlets.dto.TextCreateRequest;
import org.example.homework.servlets.dto.TextCreateResponse;
import org.example.homework.servlets.model.Text;
import org.example.homework.servlets.service.IdGenerator;
import org.example.homework.servlets.service.TextService;
import org.example.homework.servlets.exception.BadRequestException;
import org.example.homework.servlets.exception.InternalServerErrorException;

import java.io.IOException;
import java.io.PrintWriter;

public class TextsServlet extends BaseServlet {

    private final ObjectMapper objectMapper;
    private final IdGenerator idGenerator;
    private final TextService textService;


    public TextsServlet(ObjectMapper objectMapper, IdGenerator idGenerator, TextService textService) {
        this.objectMapper = objectMapper;
        this.idGenerator = idGenerator;
        this.textService = textService;
    }

    private static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    private static final String EMPTY_TEXT_ERROR = "Text cannot be empty";
    private static final String INVALID_REQUEST_PATH = "Invalid request path";

    @Override
    public void handleGet(HttpServletRequest req, HttpServletResponse resp) {

        try {
            PrintWriter respWriter = resp.getWriter();
            String pathInfo = req.getPathInfo();
            Long userId = (Long) req.getAttribute("userId");

            if (pathInfo.equals("/")) {
                respWriter.write(textService.getAllTexts(userId).toString());
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    Long textId = parseStringToLong(pathParts[1], pathInfo);
                    Text text = textService.getTextById(userId, parseStringToLong(pathParts[1], pathInfo));

                    resp.setStatus(HttpServletResponse.SC_OK);
                    respWriter.write("Text with id " + textId + ": " + text.getValue());
                } else {
                    throw new BadRequestException(INVALID_REQUEST_PATH + ": " + pathInfo);
                }
            }

        } catch (IOException ex) {
            throw new InternalServerErrorException(INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public void handlePost(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");

        try {
            PrintWriter respWriter = resp.getWriter();
            TextCreateRequest requestBody = objectMapper.readValue(req.getReader(), TextCreateRequest.class);
            TextCreateResponse responseBody = new TextCreateResponse();
            String text = requestBody.getText();

            if (text == null || text.trim().isEmpty()) {
                throw new BadRequestException(EMPTY_TEXT_ERROR);
            } else {
                Long userId = (Long) req.getAttribute("userId");
                long textId = idGenerator.getNextId();
                textService.addText(userId, new Text(textId, text, userId));

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
    public void handleDelete(HttpServletRequest req, HttpServletResponse resp) {

        try {
            PrintWriter respWriter = resp.getWriter();
            String pathInfo = req.getPathInfo();
            Long userId = (Long) req.getAttribute("userId");

            if (pathInfo.equals("/deleteAll")) {
                textService.deleteAll(userId);
                resp.setStatus(HttpServletResponse.SC_OK);
                respWriter.write("All text has been deleted");
            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    Long textId = parseStringToLong(pathParts[1], pathInfo);
                    textService.deleteTextById(userId, textId);

                    resp.setStatus(HttpServletResponse.SC_OK);
                    respWriter.write("Text with id: " + textId + " has been deleted");
                } else {
                    throw new BadRequestException(INVALID_REQUEST_PATH + ": " + pathInfo);
                }
            }
        } catch (IOException ex) {
            throw new InternalServerErrorException(INTERNAL_SERVER_ERROR);
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
