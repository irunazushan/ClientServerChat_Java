package edu.school21.sockets.repositories;

import edu.school21.sockets.models.Message;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

public interface MessagesRepository<T> extends CrudRepository<T> {
    List<T> getMessagesByUserId(Long id);
}
