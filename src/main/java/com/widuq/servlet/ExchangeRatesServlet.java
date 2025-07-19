package com.widuq.servlet;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.widuq.dto.CreateExchangeRateDto;
import com.widuq.dto.ReadExchangeRateDto;
import com.widuq.exception.CurrencyNotFoundException;
import com.widuq.exception.DatabaseUnavailableException;
import com.widuq.exception.DuplicateExchangeRateException;
import com.widuq.exception.ValidationException;
import com.widuq.service.ExchangeRateService;
import com.widuq.validator.Error;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        try {
            jsonMapper.writeValue(resp.getWriter(), exchangeRateService.findAll());
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
        var createExchangeRateDto = CreateExchangeRateDto.builder()
                .baseCurrencyCode(req.getParameter("baseCurrencyCode"))
                .targetCurrencyCode(req.getParameter("targetCurrencyCode"))
                .rate(req.getParameter("rate"))
                .build();

        var writer = resp.getWriter();

        try {
            ReadExchangeRateDto readExchangeRateDto = exchangeRateService.create(createExchangeRateDto);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            jsonMapper.writeValue(writer, readExchangeRateDto);
        } catch (ValidationException validationException) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonMapper.writeValue(writer, validationException.getErrors().get(0));
        } catch(DuplicateExchangeRateException duplicateCurrencyException) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            jsonMapper.writeValue(writer, duplicateCurrencyException.getError());
        } catch (CurrencyNotFoundException currencyNotFoundException) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            jsonMapper.writeValue(writer, currencyNotFoundException.getError());
        } catch (DatabaseUnavailableException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonMapper.writeValue(resp.getWriter(), Error.of(e.getMessage()));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
