package edu.school21.sockets.services;

import edu.school21.sockets.models.Chatroom;
import edu.school21.sockets.models.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UsersService {
    void signUp(String name, String password);

    User signIn(String name, String password);

    Chatroom createChatroom(String name, User creator);

    List<Chatroom> chooseChatroom();

    public void sendMessage(User user, Chatroom chatroom, String message);
}
