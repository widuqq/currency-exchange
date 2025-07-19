package com.widuq.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.widuq.dto.CreateCurrencyDto;
import com.widuq.dto.ReadCurrencyDto;
import com.widuq.exception.DatabaseUnavailableException;
import com.widuq.exception.DuplicateCurrencyException;
import com.widuq.exception.ValidationException;
import com.widuq.service.CurrencyService;
import com.widuq.validator.Error;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final ObjectMapper jsonMapper = new ObjectMapper();

    private final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            jsonMapper.writeValue(resp.getWriter(), currencyService.findAll());
        } catch (DatabaseUnavailableException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonMapper.writeValue(resp.getWriter(), Error.of(e.getMessage()));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        CreateCurrencyDto currencyDto = CreateCurrencyDto.builder()
                .name(req.getParameter("name"))
                .code(req.getParameter("code"))
                .sign(req.getParameter("sign"))
                .build();

        var writer = resp.getWriter();

        try {
            ReadCurrencyDto readCurrencyDto = currencyService.create(currencyDto);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            jsonMapper.writeValue(writer, readCurrencyDto);

        } catch (ValidationException validationException) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonMapper.writeValue(writer, validationException.getErrors().get(0));
        } catch (DuplicateCurrencyException duplicateCurrencyException) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            jsonMapper.writeValue(writer, duplicateCurrencyException.getError());
        } catch (DatabaseUnavailableException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonMapper.writeValue(resp.getWriter(), Error.of(e.getMessage()));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
