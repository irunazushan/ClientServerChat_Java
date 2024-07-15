package edu.school21.sockets.repositories;

import edu.school21.sockets.models.Message;

import java.util.List;

public interface ChatroomsRepository<T> extends CrudRepository<T> {
    List<Message> getMessagesFromChatroom(Long id);
}
