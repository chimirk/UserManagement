package database;

import configuration.DataBaseConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserGateway {
    private static final String INSERT_USER_INFO_SQL ="INSERT INTO poll_app.users" +
            "(username, email, full_name) VALUES" + "(?,?,?);";

    private static final String UPDATE_USER_PASSWORD_SQL ="UPDATE poll_app.users SET password = ? WHERE username = ?;";
    private static final String UPDATE_USER_ACTIVATION_STATUS_SQL ="UPDATE poll_app.users SET isValidated = ? WHERE username = ?;";
    private static final String SELECT_ALL_EMAILS_SQL ="SELECT email FROM poll_app.users;";
    private static final String SELECT_ALL_USERS_SQL ="SELECT username FROM poll_app.users;";
    private static final String SELECT_ALL_USERS_INFO_SQL ="SELECT username, password, email, full_name FROM poll_app.users;";
    private static final String SELECT_USERNAME_FROM_EMAIL_SQL ="SELECT username FROM poll_app.users WHERE email = ?;";
    private static final String SELECT_USER_BY_USERNAME_AND_PASSWORD = "SELECT username, password FROM poll_app.users WHERE username = ? AND password = ?;";
    private static final String SELECT_PASSWORD_FROM_USERNAME = "SELECT password FROM poll_app.users WHERE password = ?;";
    private static final String DELETE_USER_BY_USERNAME = "DELETE FROM poll_app.users WHERE username = ?;";
    private static final String DELETE_USER_BY_EMAIL = "DELETE FROM poll_app.users WHERE email = ?;";





    public static void saveUser(String userName, String userEmail, String userFullName) {
        try(Connection connection = DataBaseConfiguration.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_INFO_SQL)) {
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, userEmail);
            preparedStatement.setString(3, userFullName);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void savePassword(String userPassword, String userName) {
        try(Connection connection = DataBaseConfiguration.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_PASSWORD_SQL)) {
            preparedStatement.setString(1, userPassword);
            preparedStatement.setString(2, userName);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateActivationStatus(String newStatus, String userName) {
        try(Connection connection = DataBaseConfiguration.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_ACTIVATION_STATUS_SQL)) {
            preparedStatement.setString(1, newStatus);
            preparedStatement.setString(2, userName);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getPasswordFromUsername(String username) throws SQLException {
        String temp = "";
        try(Connection connection = DataBaseConfiguration.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PASSWORD_FROM_USERNAME)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    temp = resultSet.getString("password");
                    if (temp != null) {
                        return temp;
                    }
                }
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
        return null;
    }

    public static ArrayList<User> getAllUsersInfo() {
        ArrayList<User> users = new ArrayList<>();
        try(Connection connection = DataBaseConfiguration.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS_INFO_SQL)) {
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    users.add(new User(
                            resultSet.getString("username"),
                            resultSet.getString("password"),
                            resultSet.getString("email"),
                            resultSet.getString("full_name")
                    ));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public static void deleteUserByUsername(String userName) {
        try(Connection connection = DataBaseConfiguration.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER_BY_USERNAME)) {
            preparedStatement.setString(1, userName);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteUserByEmail(String email) {
        try(Connection connection = DataBaseConfiguration.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER_BY_EMAIL)) {
            preparedStatement.setString(1, email);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isValidEmail (String userEmail) {
        String temp = "";
        try(Connection connection = DataBaseConfiguration.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_EMAILS_SQL)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    temp = resultSet.getString("email");
                    if (temp.equals(userEmail)) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isANewUser (String username) {
        String temp = "";
        try(Connection connection = DataBaseConfiguration.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS_SQL)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    temp = resultSet.getString("username");
                    if (temp.equals(username)) {
                        return  false;
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getUsernameFromEmail (String email) {
        String username;
        try(Connection connection = DataBaseConfiguration.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USERNAME_FROM_EMAIL_SQL)) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    username = resultSet.getString("username");
                    if (username != null) {
                        return username;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static boolean isUserNameAndOldPasswordValid (String username, String oldPassword) {
        String retrieved_username = null;
        String retrieved_password = null;
        try(Connection connection = DataBaseConfiguration.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_USERNAME_AND_PASSWORD)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, oldPassword);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    retrieved_username = resultSet.getString("username");
                    retrieved_password = resultSet.getString("password");
                    if (retrieved_username.equals(username) && retrieved_password.equals(oldPassword)) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }
}
