package org.example.servletsHomework.servlet;


import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.servletsHomework.exception.BadRequestException;
import org.example.servletsHomework.exception.InternalServerErrorException;
import org.example.servletsHomework.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public abstract class BaseServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(BaseServlet.class);

    private static final Map<Class<? extends Exception>, Integer> EXCEPTIONS_AND_STATUS_MAP =
            Map.of(
                    BadRequestException.class, HttpServletResponse.SC_BAD_REQUEST,
                    NotFoundException.class, HttpServletResponse.SC_NOT_FOUND,
                    InternalServerErrorException.class, HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

    protected abstract void handleGet(HttpServletRequest req, HttpServletResponse resp);

    protected abstract void handleDelete(HttpServletRequest req, HttpServletResponse resp);

    protected abstract void handlePost(HttpServletRequest req, HttpServletResponse resp);


    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        handleGet(req, resp);
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        handleDelete(req, resp);
    }


    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        handlePost(req, resp);
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) {
        try {
            super.service(req, resp);
        } catch (Exception ex) {
            Integer status = EXCEPTIONS_AND_STATUS_MAP.get(ex.getClass());
            if (status == null) {
                handleException(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error");
                logger.error(ex.getMessage(), ex);
            } else {
                handleException(resp, status, ex.getMessage());
                if (status >= HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        }
    }

    public void handleException(HttpServletResponse resp, int status, String message) {
        resp.setContentType("text/plain");
        try {
            resp.setStatus(status);
            try (PrintWriter writer = resp.getWriter()) {
                writer.write(message);
            }
        } catch (IOException e) {
            logger.error("Error writing response", e);
        }
    }

}
