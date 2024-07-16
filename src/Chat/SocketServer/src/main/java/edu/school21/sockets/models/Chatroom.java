package edu.school21.sockets.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
public class Chatroom {
    private Long id;
    private String name;
    private User creator;
    private List<Message> messages;

    public Chatroom(Long id, String name, User creator) {
        this.id = id;
        this.name = name;
        this.creator = creator;
    }

    public Chatroom(Long id, String name, User creator, List<Message> messages) {
        this.id = id;
        this.name = name;
        this.creator = creator;
        if (messages != null) {
            this.messages = new CopyOnWriteArrayList<>(messages);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof Chatroom)) return false;
        if (this.hashCode() != obj.hashCode()) return false;
        Chatroom other = (Chatroom) obj;
        return Objects.equals(this.id, other.id)
                && Objects.equals(this.name, other.name)
                && Objects.equals(this.creator, other.creator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, creator);
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", name=\"" + name + '"' +
                ", creator=\"" + creator + '"' +
                '}';
    }
}
