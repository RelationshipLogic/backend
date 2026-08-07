CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE analysis_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    relationship_type VARCHAR(50) NOT NULL,
    context_memo TEXT,
    input_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP
);

CREATE TABLE analysis_unlocks (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL,
    grade VARCHAR(50) NOT NULL,
    method VARCHAR(50) NOT NULL,
    payment_id BIGINT,
    unlocked_at TIMESTAMP,
    CONSTRAINT uk_analysis_unlocks_session_grade UNIQUE (session_id, grade)
);

CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    portone_payment_id VARCHAR(255) NOT NULL,
    amount INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP,
    paid_at TIMESTAMP,
    CONSTRAINT uk_payments_portone_payment_id UNIQUE (portone_payment_id)
);
