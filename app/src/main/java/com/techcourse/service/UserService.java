package com.techcourse.service;

import com.techcourse.dao.UserDao;
import com.techcourse.dao.UserHistoryDao;
import com.techcourse.domain.User;
import com.techcourse.domain.UserHistory;
import nextstep.jdbc.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserService {

    private final UserDao userDao;
    private final UserHistoryDao userHistoryDao;

    public UserService(final UserDao userDao, final UserHistoryDao userHistoryDao) {
        this.userDao = userDao;
        this.userHistoryDao = userHistoryDao;
    }

    public User findById(final long id) throws SQLException {
        return userDao.findById(id);
    }

    public void insert(final User user) throws SQLException {
        userDao.insert(user);
    }

    public void changePassword(final long id, final String newPassword, final String createBy) throws SQLException {

        Connection connection = null;
        try {
            connection = userDao.getConnection();
            connection.setAutoCommit(false);
            final var user = findById(id);
            user.changePassword(newPassword);
            userDao.update(connection, user);
            userHistoryDao.log(connection, new UserHistory(user, createBy));
            connection.commit();
        }
        catch (SQLException | DataAccessException e) {
            try {
                connection.rollback();
            }
            catch (SQLException ignored) {}
            throw new DataAccessException();
        }
        finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
}
