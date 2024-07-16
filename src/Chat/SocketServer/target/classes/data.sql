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

SELECT * FROM messages;
SELECT * FROM chatrooms;
SELECT * FROM users;