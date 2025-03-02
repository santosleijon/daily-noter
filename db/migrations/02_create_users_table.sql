\c daily_noter;
CREATE TABLE IF NOT EXISTS users (
    user_id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE users TO daily_noter;
