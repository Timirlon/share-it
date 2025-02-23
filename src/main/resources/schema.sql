CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(255),
                       email VARCHAR(255) UNIQUE
);

CREATE TABLE items (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(255),
                       description VARCHAR(255),
                       is_available BOOLEAN,
                       owner_id INT REFERENCES users
);

CREATE TABLE bookings (
                          id SERIAL PRIMARY KEY,
                          start_date TIMESTAMP WITHOUT TIME ZONE,
                          end_date TIMESTAMP WITHOUT TIME ZONE,
                          item_id INT REFERENCES items,
                          booker_id INT REFERENCES users,
                          status VARCHAR(255)
);

CREATE TABLE comments (
                          id SERIAL PRIMARY KEY,
                          text VARCHAR(255),
                          item_id INT REFERENCES items,
                          author_id INT REFERENCES users,
                          created TIMESTAMP WITHOUT TIME ZONE
);
