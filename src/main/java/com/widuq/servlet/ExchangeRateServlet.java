package com.widuq.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.widuq.dto.ReadExchangeRateDto;
import com.widuq.dto.UpdateExchangeRateDto;
import com.widuq.exception.DatabaseUnavailableException;
import com.widuq.exception.ExchangeRateNotFoundException;
import com.widuq.exception.ValidationException;
import com.widuq.service.ExchangeRateService;
import com.widuq.validator.Error;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import static com.widuq.util.HttpHelper.parseFormData;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private static final int CURRENCY_PAIR_LENGTH = 6;
    private static final int PATH_INFO_MIN_LENGTH = CURRENCY_PAIR_LENGTH + 1;

    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        validatePath(req, resp);

        String currencyPair = req.getPathInfo().substring(1).toUpperCase();
        String baseCurrencyCode = currencyPair.substring(0, 3);
        String targetCurrencyCode = currencyPair.substring(3);

        var writer = resp.getWriter();

        try {
            ReadExchangeRateDto exchangeRateDto = exchangeRateService.findByCurrencyPair(baseCurrencyCode, targetCurrencyCode);

            jsonMapper.writeValue(writer, exchangeRateDto);
        } catch (ExchangeRateNotFoundException exchangeRateNotFoundException) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            jsonMapper.writeValue(writer, exchangeRateNotFoundException.getError());
        } catch (DatabaseUnavailableException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonMapper.writeValue(resp.getWriter(), Error.of(e.getMessage()));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        validatePath(req, resp);

        String currencyPair = req.getPathInfo().substring(1).toUpperCase();
        String baseCurrencyCode = currencyPair.substring(0, 3);
        String targetCurrencyCode = currencyPair.substring(3);

        String body = req.getReader().lines().collect(Collectors.joining());

        Map<String, String> params = parseFormData(body);

        UpdateExchangeRateDto updateExchangeRateDto = UpdateExchangeRateDto.builder()
                .rate(params.get("rate"))
                .build();

        var writer = resp.getWriter();

        try {
            ReadExchangeRateDto readExchangeRateDto = exchangeRateService.update(updateExchangeRateDto, baseCurrencyCode, targetCurrencyCode);
            jsonMapper.writeValue(writer, readExchangeRateDto);
        } catch (ValidationException validationException) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonMapper.writeValue(writer, validationException.getErrors().get(0));
        } catch (ExchangeRateNotFoundException exchangeRateNotFoundException) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            jsonMapper.writeValue(writer, exchangeRateNotFoundException.getError());
        } catch (DatabaseUnavailableException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonMapper.writeValue(resp.getWriter(), Error.of(e.getMessage()));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

    private void validatePath(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.length() != PATH_INFO_MIN_LENGTH) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonMapper.writeValue(resp.getWriter(), Error.of("The currency pair codes are missing from the address or have an invalid format."));
        }
    }
}
