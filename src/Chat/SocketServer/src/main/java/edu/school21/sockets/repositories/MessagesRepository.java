package edu.school21.sockets.repositories;

import java.util.List;

public interface MessagesRepository<T> extends CrudRepository<T> {
    List<T> getMessagesByUserId(Long id);
}
