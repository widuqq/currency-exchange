package com.widuq.dao;

import com.widuq.entity.Currency;
import com.widuq.exception.DaoException;
import com.widuq.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class CurrencyDao implements Dao<Integer, Currency> {
    private static final CurrencyDao INSTANCE = new CurrencyDao();

    private static final String FIND_ALL_SQL =
            """
                    SELECT id, code, full_name, sign
                    FROM currencies;
                    """;

    private static final String FIND_BY_ID_SQL =
            """
                    SELECT id, code, full_name, sign
                    FROM currencies
                    WHERE id = ?;
                    """;

    private static final String FIND_BY_CODE_SQL =
            """
                    SELECT id, code, full_name, sign
                    FROM currencies
                    WHERE code = ?;
                    """;

    private static final String SAVE_SQL =
            """
                    INSERT INTO currencies (code, full_name, sign)
                    VALUES (?, ?, ?);
                    """;

    private static final String CHECK_EXISTS_BY_CODE_SQL =
            """
                    SELECT COUNT(*) FROM currencies
                    WHERE code = ?;
                    """;

    private CurrencyDao() {

    }


    @Override
    public List<Currency> findAll() {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            List<Currency> currencies = new ArrayList<>();

            var resultSet = prepareStatement.executeQuery();

            while (resultSet.next()) {
                currencies.add(build(resultSet));
            }

            return currencies;
        } catch (SQLException e) {
            throw new DaoException("Error in findAll()", e);
        }
    }

    @Override
    public Optional<Currency> findById(Integer id) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            prepareStatement.setObject(1, id);

            var resultSet = prepareStatement.executeQuery();

            return resultSet.next() ? Optional.ofNullable(build(resultSet)) : Optional.empty();
        } catch (SQLException e) {
            throw new DaoException("Error in findById()", e);
        }
    }

    public Optional<Currency> findByCode(String code) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(FIND_BY_CODE_SQL)) {
            prepareStatement.setString(1, code);

            var resultSet = prepareStatement.executeQuery();

            return resultSet.next() ? Optional.ofNullable(build(resultSet)) : Optional.empty();
        } catch (SQLException e) {
            throw new DaoException("Error in findByCode()", e);
        }
    }

    @Override
    public Currency save(Currency entity) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(SAVE_SQL, RETURN_GENERATED_KEYS)) {

            prepareStatement.setObject(1, entity.getCode());
            prepareStatement.setObject(2, entity.getFullName());
            prepareStatement.setObject(3, entity.getSign());

            prepareStatement.executeUpdate();

            var resultSet = prepareStatement.getGeneratedKeys();

            if (resultSet.next())
                entity.setId(resultSet.getObject("id", Integer.class));

            return entity;
        } catch (SQLException e) {
            throw new DaoException("Error in save()", e);
        }
    }

    public boolean checkExistsByCode(String code) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(CHECK_EXISTS_BY_CODE_SQL)) {
            prepareStatement.setObject(1, code);

            int count = 0;

            var resultSet = prepareStatement.executeQuery();

            if (resultSet.next())
                count = resultSet.getInt(1);

            return count > 0;
        } catch (SQLException e) {
            throw new DaoException("Error in checkExistsByCode()", e);
        }
    }

    private Currency build(ResultSet resultSet) throws SQLException {
        return Currency.builder()
                .id(resultSet.getObject("id", Integer.class))
                .code(resultSet.getObject("code", String.class))
                .fullName(resultSet.getObject("full_name", String.class))
                .sign(resultSet.getObject("sign", String.class))
                .build();
    }

    public static CurrencyDao getInstance() {
        return INSTANCE;
    }
}
