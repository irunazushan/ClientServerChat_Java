package edu.school21.sockets.repositories;

import edu.school21.sockets.models.Chatroom;
import edu.school21.sockets.models.Message;
import edu.school21.sockets.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;


@Component
public class ChatroomsRepositoryImpl implements ChatroomsRepository<Chatroom> {
    private final NamedParameterJdbcTemplate template;

    @Autowired
    public ChatroomsRepositoryImpl(@Qualifier("hikariDataSource") DataSource dataSource) {
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Optional<Chatroom> findById(Long id) {
        String query = "SELECT id, name, creator FROM chatrooms WHERE id = :id";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("id", id);
        Chatroom chatroom = template.queryForObject(query, namedParameters, (rs, rowNum) ->
                new Chatroom(rs.getLong("id"),
                        rs.getString("name"), findUserById(rs.getLong("creator")), getMessagesFromChatroom(id)));
        if (chatroom != null) {
            chatroom.setMessages(getMessagesFromChatroom(chatroom.getId()));
            return Optional.of(chatroom);
        }
        throw new NullPointerException("Message with id: '" + id + "' not found");
    }

    @Override
    public List<Chatroom> findAll() {
        String query = "SELECT * FROM chatrooms";
        List<Chatroom> chatrooms;
        chatrooms = template.query(query, (rs, rowNum) -> new Chatroom(rs.getLong("id"),
                rs.getString("name"), findUserById(rs.getLong("creator")), getMessagesFromChatroom(rs.getLong("id"))));
        return chatrooms;
    }

    @Override
    public void save(Chatroom entity) {
        String query = "INSERT INTO chatrooms (name, creator) VALUES (:name, :creator) RETURNING id";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("name", entity.getName())
                .addValue("creator", entity.getCreator().getId());
        Long newId = template.queryForObject(query, namedParameters, Long.class);
        entity.setId(newId);
    }

    @Override
    public void update(Chatroom entity) {
        String query = "UPDATE chatrooms SET name=:name, creator=:creator WHERE id=:id";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("name", entity.getName())
                .addValue("creator", entity.getCreator().getId());
        template.update(query, namedParameters);
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM messages WHERE id=:id";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("id", id);
        template.update(query, namedParameters);
    }

    @Override
    public List<Message> getMessagesFromChatroom(Long id) {
        String query = "SELECT id, sender, chatroom, message, sent_time FROM messages WHERE chatroom=:chatroom";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("chatroom", id);
        List<Message> messages = template.query(query, namedParameters, (rs, rowNum) -> new Message(
                rs.getLong("id"),
                findUserById(rs.getLong("sender")),
                findChatroomById(rs.getLong("chatroom")),
                rs.getString("message"),
                rs.getTimestamp("sent_time") != null ? rs.getTimestamp("sent_time").toLocalDateTime() : null));
        return messages;
    }

    public User findUserById(Long id) {
        String query = "SELECT id, name, password FROM users WHERE id=:id";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("id", id);
        User user = template.queryForObject(query, namedParameters, (rs, rowNum) -> new User(
                rs.getLong("id"), rs.getString("name"), rs.getString("password"))
        );
        if (user != null) {
            return user;
        }
        throw new NullPointerException("User with id: '" + id + "' not found");
    }

    public Chatroom findChatroomById(Long id) {
        String query = "SELECT id, name, creator FROM chatrooms WHERE id=:id";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("id", id);
        Chatroom room = template.queryForObject(query, namedParameters, (rs, rowNum) -> new Chatroom(
                rs.getLong("id"), rs.getString("name"), findUserById(rs.getLong("creator")))
        );
        if (room != null) {
            return room;
        }
        throw new NullPointerException("Room with id: '" + id + "' not found");
    }
}
