package org.example.servletstexts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

@WebServlet(name = "texts-servlet", value = "/texts/*")
public class TextsServlet extends HttpServlet {

    private int currentId = 1;
    private final HashMap<Integer, String> texts = new HashMap<>();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (PrintWriter respWriter = resp.getWriter()) {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                respWriter.write(texts.toString());
            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    responseProcessForDoGetMethod(pathParts[1], respWriter, resp);
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    respWriter.write("Invalid request path. You entered an extra '/'");
                }
            }
        } catch (IOException e1) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Internal server error");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (PrintWriter respWriter = resp.getWriter()) {
            String pathInfo = req.getPathInfo();
            if (pathInfo.equals("/deleteAll")) {
                texts.clear();
                respWriter.write("All text has been deleted");
            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2 && !pathParts[1].isEmpty()) {
                    responseProcessForDeleteMethod(pathParts[1], respWriter, resp);
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    respWriter.write("Invalid request path. You entered an extra '/'");
                }
            }
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Internal server error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                respWriter.write("Text cannot be empty");
            }
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Internal server error");
        }
    }

    private void responseProcessForDoGetMethod(String id_string, PrintWriter respWriter, HttpServletResponse response) {
        try {
            int id = Integer.parseInt(id_string);
            getTextById(id, respWriter, response);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            respWriter.write("Invalid id format");
        }
    }

    private void responseProcessForDeleteMethod(String id_string, PrintWriter respWriter, HttpServletResponse response) {
        try {
            int id = Integer.parseInt(id_string);
            deleteTextById(id, respWriter, response);
        } catch (NumberFormatException numberFormatException) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            respWriter.write("Invalid format");

        }
    }

    private void deleteTextById(int id, PrintWriter respWriter, HttpServletResponse response) {
        if (texts.remove(id) != null) {
            respWriter.write("Text with id " + id + " has been deleted");
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            respWriter.write("Text with id " + id + " not found");
        }
    }

    private void getTextById(int id, PrintWriter respWriter, HttpServletResponse response) {
        String text = texts.get(id);
        if (text != null) {
            respWriter.write("Text with id " + id + ": " + text);
        } else {
            respWriter.write("Text with id " + id + " not found");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

}
