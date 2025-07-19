package com.widuq.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.widuq.dto.ExchangeRequestDto;
import com.widuq.exception.CurrencyNotFoundException;
import com.widuq.exception.DatabaseUnavailableException;
import com.widuq.exception.ExchangeRateNotFoundException;
import com.widuq.exception.ValidationException;
import com.widuq.service.ExchangeService;
import com.widuq.validator.Error;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private final ExchangeService exchangeService = ExchangeService.getInstance();
    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        ExchangeRequestDto exchangeRequestDto = ExchangeRequestDto.builder()
                .baseCurrencyCode(req.getParameter("from"))
                .targetCurrencyCode(req.getParameter("to"))
                .amount(req.getParameter("amount"))
                .build();

        var writer = resp.getWriter();

        try {

            var exchangeResponseDto = exchangeService.exchange(exchangeRequestDto);
            jsonMapper.writeValue(writer, exchangeResponseDto);

        } catch(ValidationException validationException) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonMapper.writeValue(writer, validationException.getErrors().get(0));
        } catch (CurrencyNotFoundException currencyNotFoundException) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            jsonMapper.writeValue(writer, currencyNotFoundException.getError());
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
}
