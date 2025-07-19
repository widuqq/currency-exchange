CREATE TABLE currencies (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code VARCHAR(3) NOT NULL UNIQUE,
    full_name VARCHAR(128) NOT NULL,
    sign VARCHAR(8) NOT NULL
);

CREATE TABLE exchange_rates (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    base_currency_id INTEGER NOT NULL REFERENCES currencies (id),
    target_currency_id INTEGER NOT NULL REFERENCES currencies (id),
    rate DECIMAL(10,6) NOT NULL,
    UNIQUE(base_currency_id, target_currency_id)
);
