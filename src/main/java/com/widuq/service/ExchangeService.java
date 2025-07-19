package com.widuq.service;

import com.widuq.dao.ExchangeRateDao;
import com.widuq.dto.ExchangeRequestDto;
import com.widuq.dto.ExchangeResponseDto;
import com.widuq.dto.ReadCurrencyDto;
import com.widuq.exception.ExchangeRateNotFoundException;
import com.widuq.exception.ValidationException;
import com.widuq.validator.CreateExchangeValidator;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ExchangeService {
    private static final ExchangeService INSTANCE = new ExchangeService();
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();
    private final CreateExchangeValidator exchangeValidator = CreateExchangeValidator.getInstance();

    private ExchangeService() {}

    public ExchangeResponseDto exchange(ExchangeRequestDto exchangeRequestDto) {
        var validationResult = exchangeValidator.isValid(exchangeRequestDto);

        if (!validationResult.isValid()) {
            throw new ValidationException(validationResult.getErrors());
        }

        var baseCurrency = currencyService.findByCode(exchangeRequestDto.getBaseCurrencyCode());
        var targetCurrency = currencyService.findByCode(exchangeRequestDto.getTargetCurrencyCode());

        var exchangeRate = exchangeRateDao
                .findByCurrencyPair(
                        baseCurrency.getCode(),
                        targetCurrency.getCode());


        if (exchangeRate.isPresent()) {
            return buildResponse(baseCurrency, targetCurrency,
                    exchangeRate.get().getRate(),
                    exchangeRequestDto.getAmount());
        }

        exchangeRate = exchangeRateDao
                .findByCurrencyPair(
                        targetCurrency.getCode(),
                        baseCurrency.getCode());

        if (exchangeRate.isPresent()) {
            BigDecimal reverseRate = exchangeRate.get().getRate();
            BigDecimal directRate = BigDecimal.ONE.divide(reverseRate, 6, RoundingMode.HALF_UP);
            return buildResponse(baseCurrency, targetCurrency,
                    directRate,
                    exchangeRequestDto.getAmount());
        }

        var usdToBaseRate  = exchangeRateDao
                .findByCurrencyPair(
                        "USD",
                        baseCurrency.getCode());

        var usdToTargetRate  = exchangeRateDao
                .findByCurrencyPair(
                        "USD",
                        targetCurrency.getCode());

        if (usdToBaseRate.isPresent() && usdToTargetRate.isPresent()) {
            BigDecimal rate = usdToTargetRate.get().getRate()
                    .divide(usdToBaseRate.get().getRate(), 6, RoundingMode.HALF_UP);
            return buildResponse(baseCurrency, targetCurrency,
                    rate,
                    exchangeRequestDto.getAmount());
        }

        throw new ExchangeRateNotFoundException(
                baseCurrency.getCode(),
                targetCurrency.getCode());
    }

    private ExchangeResponseDto buildResponse(ReadCurrencyDto baseCurrency,
                                              ReadCurrencyDto targetCurrency,
                                              BigDecimal rate,
                                              String amountStr) {
        BigDecimal amount = new BigDecimal(amountStr);
        BigDecimal convertedAmount = amount.multiply(rate)
                .setScale(2, RoundingMode.HALF_UP);

        return ExchangeResponseDto.builder()
                .baseCurrency(baseCurrency)
                .targetCurrency(targetCurrency)
                .rate(rate.setScale(2, RoundingMode.HALF_UP))
                .amount(amount)
                .convertedAmount(convertedAmount)
                .build();
    }

    public static ExchangeService getInstance() {
        return INSTANCE;
    }
}
