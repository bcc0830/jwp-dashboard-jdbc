package com.techcourse.dao;

import com.techcourse.config.DataSourceConfig;
import com.techcourse.domain.User;
import nextstep.jdbc.DataAccessException;
import nextstep.jdbc.JdbcMapper;
import nextstep.jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserDao {
    private static final JdbcMapper<User> userMapper =
            (resultSet -> new User(
                    resultSet.getLong("id"),
                    resultSet.getString("account"),
                    resultSet.getString("password"),
                    resultSet.getString("email")));

    private final JdbcTemplate userJdbcTemplate;

    public UserDao(final JdbcTemplate userJdbcTemplate) {
        this.userJdbcTemplate = userJdbcTemplate;
    }

    public void insert(final User user) {
        String sql = "insert into users (account, password, email) values (?, ?, ?)";
        this.userJdbcTemplate.nonSelectQuery(sql, user.getAccount(), user.getPassword(), user.getEmail());
    }

    public void update(final User user) {
        String sql = "update users set password = ? where account = ?";
        this.userJdbcTemplate.nonSelectQuery(sql, user.getPassword(), user.getAccount());

    }

    public List<User> findAll() {
        String sql = "select id, account, password, email from users";
        return this.userJdbcTemplate.selectQuery(sql, userMapper);
    }

    public User findById(final Long id) {
        String sql = "select id, account, password, email from users where id = ?";
        List<User> users = this.userJdbcTemplate.selectQuery(sql, userMapper, id);
        return getFirstUser(users);
    }

    public User findByAccount(final String account) {
        String sql = "select id, account, password, email from users where account = ?";
        List<User> users = this.userJdbcTemplate.selectQuery(sql, userMapper, account);
        return getFirstUser(users);
    }

    private User getFirstUser(List<User> users) {
        if (users.size() != 1) {
            throw new DataAccessException();
        }
        return users.get(0);
    }
}
