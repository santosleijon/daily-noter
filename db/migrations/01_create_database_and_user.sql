CREATE DATABASE daily_noter;
CREATE USER daily_noter WITH ENCRYPTED PASSWORD 'secret';
GRANT ALL PRIVILEGES ON DATABASE daily_noter TO daily_noter;