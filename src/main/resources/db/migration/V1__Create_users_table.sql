CREATE TABLE users (
                        id BIGSERIAL PRIMARY KEY,
                        username VARCHAR(100) NOT NULL,
                        email VARCHAR(100) NOT NULL UNIQUE,
                        password VARCHAR NOT NULL,
                        role VARCHAR NOT NULL
);

CREATE INDEX idx_users_email ON users(email);

CREATE TABLE tasks (
                       id BIGSERIAL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       description TEXT,
                       status VARCHAR(20) NOT NULL,
                       priority VARCHAR(20) NOT NULL,
                       author_id BIGINT NOT NULL,
                       created_at TIMESTAMP DEFAULT NOW(),
                       updated_at TIMESTAMP DEFAULT NOW(),
                       CONSTRAINT fk_task_author
                           FOREIGN KEY (author_id)
                               REFERENCES users(id)
                               ON DELETE CASCADE
);

CREATE INDEX idx_tasks_author_id ON tasks(author_id);


CREATE TABLE task_assignees (
                        task_id BIGINT NOT NULL,
                        user_id BIGINT NOT NULL,
                        PRIMARY KEY (task_id, user_id),
                        CONSTRAINT fk_task_assignee_task
                            FOREIGN KEY (task_id)
                                REFERENCES tasks(id)
                                ON DELETE CASCADE,
                        CONSTRAINT fk_task_assignee_user
                            FOREIGN KEY (user_id)
                                REFERENCES users(id)
                                ON DELETE CASCADE
);

CREATE TABLE comments (
                        id BIGSERIAL PRIMARY KEY,
                        text TEXT NOT NULL,
                        author_id BIGINT NOT NULL,
                        task_id BIGINT NOT NULL,
                        created_at TIMESTAMP DEFAULT NOW(),
                        CONSTRAINT fk_comment_author
                              FOREIGN KEY (author_id)
                                  REFERENCES users(id)
                                  ON DELETE CASCADE,
                        CONSTRAINT fk_comment_task
                              FOREIGN KEY (task_id)
                                  REFERENCES tasks(id)
                                  ON DELETE CASCADE
);

CREATE INDEX idx_comments_task_id ON comments(task_id);


