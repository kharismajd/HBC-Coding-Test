CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL CHECK (LENGTH(password) >= 5),
    is_deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE forms (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255) NULL,
    limit_one_response BOOLEAN DEFAULT FALSE,
    creator_id BIGINT NOT NULL,
    CONSTRAINT fk_forms_creator FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE allowed_domains (
    id BIGSERIAL PRIMARY KEY,
    form_id BIGINT NOT NULL,
    domain VARCHAR(255) NOT NULL,
    CONSTRAINT fk_allowed_domains_form FOREIGN KEY (form_id) REFERENCES forms(id) ON DELETE CASCADE
);

CREATE TABLE questions (
    id BIGSERIAL PRIMARY KEY,
    form_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    choice_type VARCHAR(255) NOT NULL CHECK (choice_type IN ('short answer', 'paragraph', 'date', 'time', 'multiple choice', 'dropdown', 'checkboxes')),
    choices TEXT NULL,
    is_required BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_questions_form FOREIGN KEY (form_id) REFERENCES forms(id) ON DELETE CASCADE
);

CREATE TABLE responses (
    id BIGSERIAL PRIMARY KEY,
    form_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_responses_form FOREIGN KEY (form_id) REFERENCES forms(id) ON DELETE CASCADE,
    CONSTRAINT fk_responses_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE answers (
    id BIGSERIAL PRIMARY KEY,
    response_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    value TEXT NULL,
    CONSTRAINT fk_answers_response FOREIGN KEY (response_id) REFERENCES responses(id) ON DELETE CASCADE,
    CONSTRAINT fk_answers_question FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

CREATE INDEX idx_allowed_domains_form_id ON allowed_domains(form_id);
CREATE INDEX idx_questions_form_id ON questions(form_id);
CREATE INDEX idx_responses_form_user ON responses(form_id, user_id);
CREATE INDEX idx_answers_response_question ON answers(response_id, question_id);
