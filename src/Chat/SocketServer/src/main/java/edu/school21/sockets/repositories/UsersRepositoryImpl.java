package edu.school21.sockets.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import edu.school21.sockets.models.User;

import javax.sql.DataSource;
import java.util.*;


@Component
public class UsersRepositoryImpl implements UsersRepository<User> {
    private final NamedParameterJdbcTemplate template;

    @Autowired
    public UsersRepositoryImpl(@Qualifier("hikariDataSource") DataSource dataSource) {
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Optional<User> findById(Long id) {
        String query = "SELECT id, name FROM users WHERE id = :id";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("id", id);
        User user = template.queryForObject(query, namedParameters, (rs, rowNum) -> new User(rs.getLong("id"), rs.getString("name"), rs.getString("password")));
        if (user != null) {
            return Optional.of(user);
        }
        throw new NullPointerException("User with id: '" + id + "' not found");
    }

    @Override
    public List<User> findAll() {
        String query = "SELECT id, name, password FROM users";
        List<User> users;
        users = template.query(query, (rs, rowNum) -> new User(rs.getLong("id"), rs.getString("name"), rs.getString("password")));
        return users;
    }

    @Override
    public void save(User entity) {
        String query = "INSERT INTO users (name, password) VALUES (:name, :password) RETURNING id";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("name", entity.getName())
                .addValue("password", entity.getPassword());
        Long newId = template.queryForObject(query, namedParameters, Long.class);
        entity.setId(newId);
    }

    @Override
    public void update(User entity) {
        String query = "UPDATE users SET name=:name, password=:password WHERE id=:id";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("name", entity.getName())
                .addValue("password", entity.getPassword())
                .addValue("id", entity.getId());
        template.update(query, namedParameters);
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM users WHERE id=:id";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("id", id);
        template.update(query, namedParameters);
    }

    @Override
    public Optional<User> findByName(String name) {
        String query = "SELECT id, name, password FROM users WHERE name = :name";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("name", name);
        User user = null;
        try {
            user = template.queryForObject(query, namedParameters,
                    (rs, rowNum) -> new User(rs.getLong("id"), rs.getString("name"), rs.getString("password")));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
        return Optional.of(user);
    }
}
