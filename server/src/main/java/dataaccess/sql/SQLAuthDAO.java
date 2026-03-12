package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {

        String sql = "SELECT auth_token, username FROM auth_tokens WHERE auth_token=?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, authToken);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    String token = rs.getString("auth_token");
                    String username = rs.getString("username");

                    return new AuthData(token, username);
                }
            }

            return null;

        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving auth token", e);
        }
    }

    @Override
    public void addAuth(AuthData authData) throws DataAccessException {

        String sql = "INSERT INTO auth_tokens (auth_token, username) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, authData.authToken());
            ps.setString(2, authData.username());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error inserting auth token", e);
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

        String sql = "DELETE FROM auth_tokens WHERE auth_token=?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, authToken);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error deleting auth token", e);
        }
    }

    @Override
    public void clear() throws DataAccessException {

        String sql = "DELETE FROM auth_tokens";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error clearing auth tokens", e);
        }
    }
}
