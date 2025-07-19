package com.widuq.service;

import com.widuq.dao.CurrencyDao;
import com.widuq.dto.CreateCurrencyDto;
import com.widuq.dto.ReadCurrencyDto;
import com.widuq.exception.CurrencyNotFoundException;
import com.widuq.exception.DuplicateCurrencyException;
import com.widuq.exception.ValidationException;
import com.widuq.mapper.CreateCurrencyMapper;
import com.widuq.mapper.ReadCurrencyMapper;
import com.widuq.util.CurrencyCodeValidationHelper;
import com.widuq.validator.CreateCurrencyValidator;
import com.widuq.validator.ValidationResult;

import java.util.List;

public class CurrencyService {
    private static final CurrencyService INSTANCE = new CurrencyService();

    private final CreateCurrencyMapper createCurrencyMapper = CreateCurrencyMapper.getInstance();

    private final ReadCurrencyMapper readCurrencyMapper = ReadCurrencyMapper.getInstance();
    private final CreateCurrencyValidator createCurrencyValidator = CreateCurrencyValidator.getInstance();


    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private CurrencyService() {}

    public List<ReadCurrencyDto> findAll() {
        return currencyDao.findAll().stream()
                .map(readCurrencyMapper::mapFrom)
                .toList();
    }

    public ReadCurrencyDto findByCode(String code) {
        var validationResult = new ValidationResult();

        CurrencyCodeValidationHelper.validateCode(code, validationResult);

        if (!validationResult.isValid()) {
            throw new ValidationException(validationResult.getErrors());
        }

        var currency = currencyDao.findByCode(code)
                .orElseThrow(() -> new CurrencyNotFoundException(code));

        return readCurrencyMapper.mapFrom(currency);
    }

    public ReadCurrencyDto create(CreateCurrencyDto currencyDto) {
        var validationResult = createCurrencyValidator.isValid(currencyDto);

        if (!validationResult.isValid()) {
            throw new ValidationException(validationResult.getErrors());
        }

        if (currencyDao.checkExistsByCode(currencyDto.getCode())) {
            throw new DuplicateCurrencyException(currencyDto.getCode());
        }

        var currency = createCurrencyMapper.mapFrom(currencyDto);

        return readCurrencyMapper.mapFrom(currencyDao.save(currency));
    }

    public static CurrencyService getInstance() {
        return INSTANCE;
    }
}
