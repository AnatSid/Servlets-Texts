package org.example.servletstexts;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.servletstexts.exception.BadRequestException;
import org.example.servletstexts.exception.InternalServerErrorException;
import org.example.servletstexts.exception.NotFoundException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseServlet extends HttpServlet {

    private static final Map<Class<? extends Exception>, Integer> EXCEPTIONS_AND_STATUS_MAP = new HashMap<>();

    protected abstract void handleGet(HttpServletRequest req, HttpServletResponse resp);

    protected abstract void handleDelete(HttpServletRequest req, HttpServletResponse resp);

    protected abstract void handlePost(HttpServletRequest req, HttpServletResponse resp);

    @Override
    public void init() throws ServletException {
        super.init();
        EXCEPTIONS_AND_STATUS_MAP.put(BadRequestException.class, HttpServletResponse.SC_BAD_REQUEST);
        EXCEPTIONS_AND_STATUS_MAP.put(NotFoundException.class, HttpServletResponse.SC_NOT_FOUND);
        EXCEPTIONS_AND_STATUS_MAP.put(InternalServerErrorException.class, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        handleGet(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        handleDelete(req, resp);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        handlePost(req, resp);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        try {
            super.service(req, resp);
        } catch (Exception ex) {
            Integer status = EXCEPTIONS_AND_STATUS_MAP.get(ex.getClass());
            if (status != null) {
                handleException(resp, status, ex.getMessage());
            } else {
                handleException(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
            }
        }
    }

    protected void handleException(HttpServletResponse resp, int status, String message) {
        resp.setStatus(status);
        try (PrintWriter writer = resp.getWriter()) {
            writer.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
