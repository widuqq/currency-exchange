INSERT INTO currencies (code, full_name, sign)
VALUES ('USD', 'United States dollar', '$'),
       ('EUR', 'Euro', '€'),
       ('GBP', 'British pound sterling', '£'),
       ('JPY', 'Japanese yen', '¥'),
       ('AUD', 'Australian dollar', 'A$'),
       ('CAD', 'Canadian dollar', 'C$'),
       ('CHF', 'Swiss franc', 'CHF'),
       ('CNY', 'Chinese yuan', '¥'),
       ('SEK', 'Swedish krona', 'kr'),
       ('NZD', 'New Zealand dollar', 'NZ$');

INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate)
VALUES (1, 2, 0.92),
       (1, 3, 0.79),
       (1, 4, 151.50),
       (1, 5, 1.52),
       (1, 6, 1.36),
       (2, 1, 1.09),
       (2, 3, 0.86),
       (2, 7, 0.96),
       (3, 1, 1.27),
       (3, 2, 1.17);