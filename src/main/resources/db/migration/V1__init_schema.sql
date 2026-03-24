-- employees
CREATE TABLE employees (
                           id          BIGSERIAL    PRIMARY KEY,
                           username    VARCHAR(64)  NOT NULL UNIQUE,
                           full_name   VARCHAR(128) NOT NULL,
                           password    VARCHAR(255) NOT NULL,
                           phone       VARCHAR(20),
                           avatar_url  TEXT,
                           status      INTEGER     NOT NULL DEFAULT 1,
                           created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
                           updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
                           created_by  BIGINT,
                           updated_by  BIGINT,
                           deleted_at  TIMESTAMPTZ
);

-- customers
CREATE TABLE customers (
                           id           BIGSERIAL   PRIMARY KEY,
                           phone        VARCHAR(20) NOT NULL UNIQUE,
                           display_name VARCHAR(128),
                           avatar_url   TEXT,
                           gender       INTEGER    DEFAULT 0,
                           dob          DATE,
                           created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                           updated_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- menu_categories
CREATE TABLE menu_categories (
                                 id         BIGSERIAL    PRIMARY KEY,
                                 name       VARCHAR(128) NOT NULL,
                                 type       INTEGER     NOT NULL,
                                 sort_order INT          NOT NULL DEFAULT 0,
                                 created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
                                 updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
                                 created_by BIGINT REFERENCES employees(id),
                                 updated_by BIGINT REFERENCES employees(id),
                                 deleted_at TIMESTAMPTZ
);

-- menu_items
CREATE TABLE menu_items (
                            id          BIGSERIAL      PRIMARY KEY,
                            category_id BIGINT         NOT NULL REFERENCES menu_categories(id),
                            name        VARCHAR(128)   NOT NULL,
                            price       NUMERIC(10, 2) NOT NULL,
                            image_url   TEXT,
                            description TEXT,
                            status      INTEGER       NOT NULL DEFAULT 1,
                            sort_order  INT            NOT NULL DEFAULT 0,
                            created_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
                            updated_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
                            created_by  BIGINT REFERENCES employees(id),
                            updated_by  BIGINT REFERENCES employees(id),
                            deleted_at  TIMESTAMPTZ
);

-- item_customizations
CREATE TABLE item_customizations (
                                     id          BIGSERIAL   PRIMARY KEY,
                                     item_id     BIGINT      NOT NULL REFERENCES menu_items(id) ON DELETE CASCADE,
                                     option_name VARCHAR(64) NOT NULL,
                                     choices     TEXT        NOT NULL
    -- choices stores a JSON array as TEXT (e.g., '["Mild","Medium","Hot"]').
    -- Using TEXT instead of JSONB for portability across DB engines.
);

-- combo_meals
CREATE TABLE combo_meals (
                             id          BIGSERIAL      PRIMARY KEY,
                             category_id BIGINT         NOT NULL REFERENCES menu_categories(id),
                             name        VARCHAR(128)   NOT NULL,
                             price       NUMERIC(10, 2) NOT NULL,
                             image_url   TEXT,
                             description TEXT,
                             status      INTEGER       NOT NULL DEFAULT 1,
                             created_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
                             updated_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
                             created_by  BIGINT REFERENCES employees(id),
                             updated_by  BIGINT REFERENCES employees(id),
                             deleted_at  TIMESTAMPTZ
);

-- combo_meal_items
CREATE TABLE combo_meal_items (
                                  id       BIGSERIAL PRIMARY KEY,
                                  combo_id BIGINT    NOT NULL REFERENCES combo_meals(id) ON DELETE CASCADE,
                                  item_id  BIGINT    NOT NULL REFERENCES menu_items(id),
                                  quantity INT       NOT NULL DEFAULT 1,
                                  UNIQUE (combo_id, item_id)
    -- UNIQUE prevents the same item appearing twice in one combo.
    -- Larger portions are modeled via quantity, not duplicate rows.
);

-- delivery_addresses
CREATE TABLE delivery_addresses (
                                    id           BIGSERIAL    PRIMARY KEY,
                                    customer_id  BIGINT       NOT NULL REFERENCES customers(id),
                                    label        VARCHAR(64),
                                    recipient    VARCHAR(128) NOT NULL,
                                    phone        VARCHAR(20)  NOT NULL,
                                    street_line1 VARCHAR(255) NOT NULL,
                                    street_line2 VARCHAR(255),
                                    city         VARCHAR(128) NOT NULL DEFAULT 'San Francisco',
                                    state        CHAR(2)      NOT NULL DEFAULT 'CA',
                                    zip_code     VARCHAR(10)  NOT NULL,
                                    is_default   BOOLEAN      NOT NULL DEFAULT FALSE,
                                    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
                                    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- ── orders ────────────────────────────────────────────────
CREATE TABLE orders (
                        id                    BIGSERIAL      PRIMARY KEY,
                        order_number          VARCHAR(64)    NOT NULL UNIQUE,
                        customer_id           BIGINT         NOT NULL REFERENCES customers(id),
                        address_id            BIGINT         REFERENCES delivery_addresses(id),
                        status                INTEGER       NOT NULL DEFAULT 1,
    -- Status flow:
    --   1.pending → 2.confirmed → 3.preparing → 4.out_for_delivery → 5.delivered
    --   Any state → 6.cancelled
                        amount                NUMERIC(10, 2) NOT NULL,
                        delivery_fee          NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
                        payment_method        INTEGER       NOT NULL DEFAULT 1,
                        payment_status        INTEGER       NOT NULL DEFAULT 0,
                        estimated_delivery_at TIMESTAMPTZ,
                        note                  TEXT,
                        rejection_reason      TEXT,
                        created_at            TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
                        updated_at            TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

-- order_line_items
CREATE TABLE order_line_items (
                                  id         BIGSERIAL      PRIMARY KEY,
                                  order_id   BIGINT         NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
                                  item_name  VARCHAR(128)   NOT NULL,
                                  image_url  TEXT,
                                  unit_price NUMERIC(10, 2) NOT NULL,
                                  quantity   INT            NOT NULL,
                                  subtotal   NUMERIC(10, 2) NOT NULL
);

-- ── Seed data
-- Default admin account. Password is bcrypt hash of "admin123".
INSERT INTO employees (username, full_name, password, status)
VALUES ('admin', 'Admin User',
        '$2a$12$78yfMKloodacC9kBWZkIguZBG3te/F1ZOgMSiPDlUpB7jHhciknTq', 1);