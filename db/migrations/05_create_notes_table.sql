\c daily_noter;
CREATE TABLE IF NOT EXISTS notes (
    note_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    date DATE NOT NULL,
    content TEXT NOT NULl,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE
);
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE notes TO daily_noter;
CREATE INDEX notes_user_id_idx ON notes(user_id);
CREATE UNIQUE INDEX notes_date_idx ON notes(date);
