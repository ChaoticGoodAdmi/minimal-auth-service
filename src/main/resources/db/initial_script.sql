-- Создание пользователя базы данных
CREATE USER auth_user WITH ENCRYPTED PASSWORD 'secure_password';
GRANT ALL PRIVILEGES ON DATABASE auth_service_db TO auth_user;

-- Подключение к базе данных
\c auth_service_db;

-- Таблица для хранения пользователей
CREATE TABLE auth_user (
                           id BIGSERIAL PRIMARY KEY,
                           username VARCHAR(50) NOT NULL UNIQUE,
                           password_hash VARCHAR(255) NOT NULL,
                           email VARCHAR(255) NOT NULL UNIQUE,
                           created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                           updated_at TIMESTAMP
);

-- Таблица для хранения refresh-токенов
CREATE TABLE refresh_token (
                               id BIGSERIAL PRIMARY KEY,
                               user_id BIGINT NOT NULL REFERENCES auth_user(id) ON DELETE CASCADE,
                               token VARCHAR(255) NOT NULL,
                               expires_at TIMESTAMP NOT NULL,
                               created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Индексы для оптимизации запросов
CREATE UNIQUE INDEX idx_username ON auth_user (username);
CREATE UNIQUE INDEX idx_email ON auth_user (email);
CREATE INDEX idx_user_id ON refresh_token (user_id);

-- Скрипт для удаления просроченных токенов
DELETE FROM refresh_token WHERE expires_at < NOW();
