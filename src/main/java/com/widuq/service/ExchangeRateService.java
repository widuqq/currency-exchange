package com.widuq.service;

import com.widuq.dao.ExchangeRateDao;
import com.widuq.dto.CreateExchangeRateDto;
import com.widuq.dto.ReadExchangeRateDto;
import com.widuq.dto.UpdateExchangeRateDto;
import com.widuq.entity.Currency;
import com.widuq.entity.ExchangeRate;
import com.widuq.exception.DuplicateExchangeRateException;
import com.widuq.exception.ExchangeRateNotFoundException;
import com.widuq.exception.ValidationException;
import com.widuq.mapper.ReadExchangeRateMapper;
import com.widuq.validator.CreateExchangeRateValidator;
import com.widuq.validator.UpdateExchangeRateValidator;

import java.math.BigDecimal;
import java.util.List;

public class ExchangeRateService {
    private static final ExchangeRateService INSTANCE = new ExchangeRateService();

    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();

    private final ReadExchangeRateMapper readExchangeRateMapper = ReadExchangeRateMapper.getInstance();

    private final CreateExchangeRateValidator createExchangeRateValidator = CreateExchangeRateValidator.getInstance();
    private final UpdateExchangeRateValidator updateExchangeRateValidator = UpdateExchangeRateValidator.getInstance();

    private final CurrencyService currencyService = CurrencyService.getInstance();

    private ExchangeRateService() {
    }

    public List<ReadExchangeRateDto> findAll() {
        return exchangeRateDao.findAll().stream()
                .map(readExchangeRateMapper::mapFrom)
                .toList();
    }

    public ReadExchangeRateDto findByCurrencyPair(String baseCurrencyCode, String targetCurrencyCode) {
        var exchangeRate = exchangeRateDao.findByCurrencyPair(baseCurrencyCode, targetCurrencyCode)
                .orElseThrow(() -> new ExchangeRateNotFoundException(baseCurrencyCode, targetCurrencyCode));

        return readExchangeRateMapper.mapFrom(exchangeRate);
    }

    public ReadExchangeRateDto create(CreateExchangeRateDto createExchangeRateDto) {
        var validationResult = createExchangeRateValidator.isValid(createExchangeRateDto);

        if (!validationResult.isValid()) {
            throw new ValidationException(validationResult.getErrors());
        }

        var baseCode = createExchangeRateDto.getBaseCurrencyCode();
        var targetCode = createExchangeRateDto.getTargetCurrencyCode();

        if (exchangeRateDao.findByCurrencyPair(baseCode, targetCode).isPresent()) {
            throw new DuplicateExchangeRateException(baseCode, targetCode);
        }

        var baseCurrency = currencyService.findByCode(createExchangeRateDto.getBaseCurrencyCode());
        var targetCurrency = currencyService.findByCode(createExchangeRateDto.getTargetCurrencyCode());

        var exchangeRate = ExchangeRate.builder()
                .baseCurrency(
                        Currency.builder()
                                .id(baseCurrency.getId())
                                .code(baseCurrency.getCode())
                                .fullName(baseCurrency.getName())
                                .sign(baseCurrency.getSign())
                                .build())
                .targetCurrency(
                        Currency.builder()
                                .id(targetCurrency.getId())
                                .code(targetCurrency.getCode())
                                .fullName(targetCurrency.getName())
                                .sign(targetCurrency.getSign())
                                .build())
                .rate(new BigDecimal(createExchangeRateDto.getRate()))
                .build();


        return readExchangeRateMapper.mapFrom(exchangeRateDao.save(exchangeRate));
    }

    public ReadExchangeRateDto update(UpdateExchangeRateDto updateExchangeRateDto, String baseCode, String targetCode) {
        var validationResult = updateExchangeRateValidator.isValid(updateExchangeRateDto);

        if (!validationResult.isValid()) {
            throw new ValidationException(validationResult.getErrors());
        }

        exchangeRateDao
                .findByCurrencyPair(baseCode, targetCode)
                .orElseThrow(() -> new ExchangeRateNotFoundException(baseCode, targetCode));

        ExchangeRate updatedExchangeRate = exchangeRateDao.updateRate(baseCode, targetCode,
                new BigDecimal(updateExchangeRateDto.getRate()));

        return readExchangeRateMapper.mapFrom(updatedExchangeRate);
    }

    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }
}
