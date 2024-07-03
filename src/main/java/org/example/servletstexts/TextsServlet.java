package org.example.servletstexts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.servletstexts.exception.BadRequestException;
import org.example.servletstexts.exception.InternalServerErrorException;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "texts-servlet", value = "/texts/*")
public class TextsServlet extends BaseServlet {

    private int currentId = 1;
    private final Map<Integer, String> texts = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    private static final String INVALID_REQUEST_PATH = "Invalid request path.";
    private static final String INVALID_FORMAT = "Invalid id format";
    private static final String TEXT_NOT_FOUND = "Text not found";
    private static final String EMPTY_TEXT_ERROR = "Text cannot be empty";

    @Override
    protected void handleGet(HttpServletRequest req, HttpServletResponse resp) {
        try (PrintWriter respWriter = resp.getWriter()) {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                respWriter.write(texts.toString());
            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    getTextById(pathParts[1], resp);
                } else {
                    throw new BadRequestException(INVALID_REQUEST_PATH + " You entered an extra '/'");
                }
            }
        } catch (IOException e1) {
            throw new InternalServerErrorException(INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void handleDelete(HttpServletRequest req, HttpServletResponse resp) {
        try (PrintWriter respWriter = resp.getWriter()) {
            String pathInfo = req.getPathInfo();
            if (pathInfo.equals("/deleteAll")) {
                texts.clear();
                currentId = 0;
                respWriter.write("All text has been deleted");
            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2 && !pathParts[1].isEmpty()) {
                    deleteTextById(pathParts[1], resp);
                } else {
                    throw new BadRequestException(INVALID_REQUEST_PATH + " You entered an extra '/'");
                }
            }
        } catch (IOException e) {
            throw new InternalServerErrorException(INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void handlePost(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");

        try (PrintWriter respWriter = resp.getWriter()) {
            ObjectNode requestBody = objectMapper.readValue(req.getReader(), ObjectNode.class);
            String text = requestBody.get("text").asText();

            if (text != null && !text.trim().isEmpty()) {
                texts.put(currentId, text);

                ObjectNode responseBody = objectMapper.createObjectNode();
                responseBody.put("text", text);
                responseBody.put("message", "Text saved with id: " + currentId);
                responseBody.put("textId", currentId);
                respWriter.write(responseBody.toString());

                currentId++;
            } else {
                throw new BadRequestException(EMPTY_TEXT_ERROR);
            }

        } catch (IOException e) {
            throw new InternalServerErrorException(INTERNAL_SERVER_ERROR);
        }
    }


    private void deleteTextById(String id, HttpServletResponse resp) throws IOException {
        try {
            int textId = Integer.parseInt(id);
            if (texts.remove(textId) != null) {
                resp.getWriter().write("Text with id " + textId + " has been deleted");
            } else {
                throw new BadRequestException(TEXT_NOT_FOUND + " with id " + textId);
            }
        } catch (NumberFormatException e) {
            throw new BadRequestException(INVALID_FORMAT);
        }
    }

    private void getTextById(String id, HttpServletResponse resp) throws IOException {
        try {
            int textId = Integer.parseInt(id);
            String text = texts.get(textId);
            if (text != null) {
                resp.getWriter().write("Text with id " + textId + ": " + text);
            } else {
                throw new BadRequestException(TEXT_NOT_FOUND + " with id " + textId);
            }
        } catch (NumberFormatException e) {
            throw new BadRequestException(INVALID_FORMAT);
        }
    }


}
