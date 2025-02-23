CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE items (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       description VARCHAR(500) NOT NULL,
                       is_available BOOLEAN NOT NULL,
                       owner_id INT REFERENCES users NOT NULL
);

CREATE TABLE bookings (
                          id SERIAL PRIMARY KEY,
                          start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                          end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                          item_id INT REFERENCES items NOT NULL,
                          booker_id INT REFERENCES users NOT NULL,
                          status VARCHAR(10) NOT NULL
);

CREATE TABLE comments (
                          id SERIAL PRIMARY KEY,
                          text VARCHAR(500),
                          item_id INT REFERENCES items NOT NULL,
                          author_id INT REFERENCES users NOT NULL,
                          created TIMESTAMP WITHOUT TIME ZONE NOT NULL
);