CREATE TABLE IF NOT EXISTS users (
    id uuid PRIMARY KEY,
    name VARCHAR(255),
    surname VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(128) check ( length(trim(password)) > 8 ),
    role VARCHAR(128),
    status VARCHAR(128)
);