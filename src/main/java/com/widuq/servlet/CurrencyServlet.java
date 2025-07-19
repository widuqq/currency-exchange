package com.widuq.servlet;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.widuq.dto.ReadCurrencyDto;
import com.widuq.exception.CurrencyNotFoundException;
import com.widuq.exception.DatabaseUnavailableException;
import com.widuq.exception.ValidationException;
import com.widuq.service.CurrencyService;
import com.widuq.validator.Error;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private static final int CURRENCY_CODE_LENGTH = 3;
    private static final int PATH_INFO_MIN_LENGTH = CURRENCY_CODE_LENGTH + 1;
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException  {
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();

        var writer = resp.getWriter();

        if (pathInfo == null || pathInfo.length() != PATH_INFO_MIN_LENGTH) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonMapper.writeValue(writer, Error.of("Currency code is missing"));
            return;
        }

        String currencyCode = pathInfo.substring(1).toUpperCase();

        try {
            ReadCurrencyDto currencyDto = currencyService.findByCode(currencyCode);
            jsonMapper.writeValue(writer, currencyDto);
        } catch (ValidationException validationException) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonMapper.writeValue(writer, validationException.getErrors().get(0));
        } catch (CurrencyNotFoundException currencyNotFoundException) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            jsonMapper.writeValue(resp.getWriter(), currencyNotFoundException.getError());
        } catch (DatabaseUnavailableException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonMapper.writeValue(resp.getWriter(), Error.of(e.getMessage()));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
