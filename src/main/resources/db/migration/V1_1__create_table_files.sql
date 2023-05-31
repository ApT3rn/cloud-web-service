CREATE TABLE IF NOT EXISTS files (
    id VARCHAR(16) PRIMARY KEY,
    name VARCHAR(255),
    path VARCHAR(255),
    user_id uuid
);