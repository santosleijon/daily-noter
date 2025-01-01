\c daily_noter;
CREATE TABLE IF NOT EXISTS user_sessions (
    session_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    user_agent TEXT NOT NULL,
    ip_address VARCHAR(15) NOT NULl,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    valid_to TIMESTAMP WITH TIME ZONE NOT NULL
);
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE user_sessions TO daily_noter;
CREATE INDEX user_sessions_user_id_idx ON user_sessions(user_id);