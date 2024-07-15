DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS chatrooms;
DROP TABLE IF EXISTS users;


CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL
 );

CREATE TABLE chatrooms (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    creator BIGINT NOT NULL,
    CONSTRAINT fk_creator FOREIGN KEY(creator) REFERENCES users(id)
);

CREATE TABLE messages (
   id BIGSERIAL PRIMARY KEY,
   sender BIGINT NOT NULL,
   chatroom BIGINT NOT NULL,
   message TEXT,
   sent_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   CONSTRAINT fk_sender FOREIGN KEY(sender) REFERENCES users(id),
   CONSTRAINT fk_chatroom FOREIGN KEY(chatroom) REFERENCES chatrooms(id)
);

INSERT INTO users (name, password) VALUES ('Alice', 'password123');
INSERT INTO users (name, password) VALUES ('Bob', 'securepass');
INSERT INTO users (name, password) VALUES ('Charlie', 'qwerty');
INSERT INTO users (name, password) VALUES ('David', 'pass1234');
INSERT INTO users (name, password) VALUES ('Eve', 'password321');

INSERT INTO chatrooms (name, creator) VALUES ('Room 1', 2);
INSERT INTO chatrooms (name, creator) VALUES ('Room 2', 1);
INSERT INTO chatrooms (name, creator) VALUES ('Lounge', 3);
INSERT INTO chatrooms (name, creator) VALUES ('Study Group', 2);
INSERT INTO chatrooms (name, creator) VALUES ('Team Meeting', 5);

INSERT INTO messages (sender, chatroom, message, sent_time) VALUES (1, 1, 'Hello, how are you?', '2022-01-01 12:00:00');
INSERT INTO messages (sender, chatroom, message, sent_time) VALUES (2, 1, 'Hi there!', '2022-01-01 12:05:00');
INSERT INTO messages (sender, chatroom, message, sent_time) VALUES (3, 2, 'Good morning!', '2022-01-01 12:10:00');
INSERT INTO messages (sender, chatroom, message, sent_time) VALUES (4, 2, 'Hey, whats up?', '2022-01-01 12:15:00');
INSERT INTO messages (sender, chatroom, message, sent_time) VALUES (5, 3, 'Nice to meet you!', '2022-01-01 12:20:00');

SELECT * FROM messages;
SELECT * FROM chatrooms;
SELECT * FROM users;