package database;

import configuration.DataBaseConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ForgotPasswordTokensGateway {

    private static final String INSERT_TOKEN_SQL ="INSERT INTO poll_app.forgot_password_tokens" +
            "(forgot_password_token, username) VALUES" + "(?,?);";

    private static final String DELETE_TOKEN_SQL ="DELETE FROM poll_app.forgot_password_tokens" +
            " WHERE forgot_password_token = ?;";

    private static final String SELECT_USERNAME_BY_TOKEN_SQL ="SELECT username FROM poll_app.forgot_password_tokens" +
            " WHERE  forgot_password_token = ?;";

    private static final String DELETE_ALL_TOKENS_SQL ="DELETE FROM poll_app.forgot_password_tokens;";


    public static void saveToken(String token, String userName) {
        try(Connection connection = DataBaseConfiguration.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_TOKEN_SQL)) {
            preparedStatement.setString(1, token);
            preparedStatement.setString(2, userName);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteToken (String token) {
        try(Connection connection = DataBaseConfiguration.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_TOKEN_SQL)) {
            preparedStatement.setString(1, token);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteAllTokens () {
        try(Connection connection = DataBaseConfiguration.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ALL_TOKENS_SQL)) {
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getUsernameFromToken(String token) {
        String username = null;
        try(Connection connection = DataBaseConfiguration.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USERNAME_BY_TOKEN_SQL)) {
            preparedStatement.setString(1, token);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    username = resultSet.getString("username");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return username;
    }
}
