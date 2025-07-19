package com.widuq.dao;

import com.widuq.entity.Currency;
import com.widuq.entity.ExchangeRate;
import com.widuq.exception.DaoException;
import com.widuq.util.ConnectionManager;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class ExchangeRateDao implements Dao<Integer, ExchangeRate>{
    private static final ExchangeRateDao INSTANCE = new ExchangeRateDao();

    private static final String FIND_ALL_SQL =
            """
            SELECT
                e.id,
                e.rate,
                bc.id         AS base_id,
                bc.code       AS base_code,
                bc.full_name  AS base_full_name,
                bc.sign       AS base_sign,
                tc.id         AS target_id,
                tc.code       AS target_code,
                tc.full_name  AS target_full_name,
                tc.sign       AS target_sign
            FROM exchange_rates e
                JOIN currencies bc ON e.base_currency_id   = bc.id
                JOIN currencies tc ON e.target_currency_id = tc.id;
            """;
    public static final String FIND_BY_CURRENCY_PAIR =
            """
            SELECT
                e.id,
                e.rate,
                bc.id         AS base_id,
                bc.code       AS base_code,
                bc.full_name  AS base_full_name,
                bc.sign       AS base_sign,
                tc.id         AS target_id,
                tc.code       AS target_code,
                tc.full_name  AS target_full_name,
                tc.sign       AS target_sign
            FROM exchange_rates e
                JOIN currencies bc ON e.base_currency_id   = bc.id
                JOIN currencies tc ON e.target_currency_id = tc.id
            WHERE bc.code = ? AND tc.code = ?;
            """;
    private static final String FIND_BY_ID_SQL =
            """
            SELECT
                e.id,
                e.rate,
                bc.id         AS base_id,
                bc.code       AS base_code,
                bc.full_name  AS base_full_name,
                bc.sign       AS base_sign,
                tc.id         AS target_id,
                tc.code       AS target_code,
                tc.full_name  AS target_full_name,
                tc.sign       AS target_sign
            FROM exchange_rates e
                JOIN currencies bc ON e.base_currency_id   = bc.id
                JOIN currencies tc ON e.target_currency_id = tc.id
            WHERE e.id = ?;
            """;
    private static final String SAVE_SQL =
            """
            INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate)
            VALUES (?, ?, ?);
            """;
    private static final String UPDATE_RATE_SQL =
            """
            UPDATE exchange_rates
            SET rate = ?
            WHERE base_currency_id = (SELECT id FROM currencies WHERE code = ?)
            AND target_currency_id = (SELECT id FROM currencies WHERE code = ?)
            """;


    private ExchangeRateDao() {}

    public static ExchangeRateDao getInstance() {
        return INSTANCE;
    }

    @Override
    public List<ExchangeRate> findAll() {
        try (var connection = ConnectionManager.get();
        var prepareStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            List<ExchangeRate> exchangeRates = new ArrayList<>();

            var resultSet = prepareStatement.executeQuery();

            while (resultSet.next())
                exchangeRates.add(build(resultSet));

            return exchangeRates;
        } catch (SQLException e) {
            throw new DaoException("Error in findAll()", e);
        }
    }

    @Override
    public Optional<ExchangeRate> findById(Integer id) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            prepareStatement.setObject(1, id);

            var resultSet = prepareStatement.executeQuery();

            return resultSet.next() ? Optional.ofNullable(build(resultSet)) : Optional.empty();
        } catch (SQLException e) {
            throw new DaoException("Error in findById()", e);
        }
    }

    public Optional<ExchangeRate> findByCurrencyPair(String baseCurrencyCode, String targetCurrencyCode) {
        try (var connection = ConnectionManager.get();
        var prepareStatement = connection.prepareStatement(FIND_BY_CURRENCY_PAIR)) {

            prepareStatement.setObject(1, baseCurrencyCode);
            prepareStatement.setObject(2, targetCurrencyCode);

            var resultSet = prepareStatement.executeQuery();

            return resultSet.next() ? Optional.ofNullable(build(resultSet)) : Optional.empty();
        } catch (SQLException e) {
            throw new DaoException("Error in findByCurrencyPair()", e);
        }
    }

    public ExchangeRate updateRate(String baseCode, String targetCode, BigDecimal newRate) {
        try (var connection = ConnectionManager.get();
        var prepareStatement = connection.prepareStatement(UPDATE_RATE_SQL)) {
            prepareStatement.setObject(1, newRate);
            prepareStatement.setObject(2, baseCode);
            prepareStatement.setObject(3, targetCode);

            prepareStatement.executeUpdate();

            return findByCurrencyPair(baseCode, targetCode).orElseThrow(() ->
                    new DaoException("Updated but cannot re-fetch", null));
        } catch (SQLException e) {
            throw new DaoException("Error in updateRate()", e);
        }
    }

    @Override
    public ExchangeRate save(ExchangeRate entity) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(SAVE_SQL, RETURN_GENERATED_KEYS)) {

            prepareStatement.setObject(1, entity.getBaseCurrency().getId());
            prepareStatement.setObject(2, entity.getTargetCurrency().getId());
            prepareStatement.setObject(3, entity.getRate());

            prepareStatement.executeUpdate();

            var resultSet = prepareStatement.getGeneratedKeys();

            if (resultSet.next())
                entity.setId(resultSet.getObject("id", Integer.class));

            return entity;
        } catch (SQLException e) {
            throw new DaoException("Error in save()", e);
        }
    }

    private ExchangeRate build(ResultSet resultSet) throws SQLException {
        Currency baseCurrency = Currency.builder()
                .id(resultSet.getObject("base_id", Integer.class))
                .code(resultSet.getObject("base_code", String.class))
                .fullName(resultSet.getObject("base_full_name", String.class))
                .sign(resultSet.getObject("base_sign", String.class))
                .build();

        Currency targetCurrency = Currency.builder()
                .id(resultSet.getObject("target_id", Integer.class))
                .code(resultSet.getObject("target_code", String.class))
                .fullName(resultSet.getObject("target_full_name", String.class))
                .sign(resultSet.getObject("target_sign", String.class))
                .build();

        return ExchangeRate.builder()
                .id(resultSet.getObject("id", Integer.class))
                .baseCurrency(baseCurrency)
                .targetCurrency(targetCurrency)
                .rate(resultSet.getObject("rate", BigDecimal.class))
                .build();
    }
}
