package edu.school21.sockets.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class User {
    private Long id;
    private String name;
    private String password;

    public User(Long id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, password);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if ((!(obj instanceof User)) || this.hashCode() != obj.hashCode()) return false;
        User other = (User) obj;
        return Objects.equals(this.id, other.id)
                && Objects.equals(this.name, other.name)
                && Objects.equals(this.password, other.password);
    }

    @Override
    public String toString() {
        return "{" +
                "id: " + id +
                ", name: " + name +
                ", password: " + password +
                '}';
    }

}
