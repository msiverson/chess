package dataaccess.sql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import chess.ChessGame;

public class SQLGameDAO implements GameDAO {

    private final Gson gson = new Gson();

    @Override
    public List<GameData> listGames() throws DataAccessException {
        String sql = "SELECT * FROM games";
        List<GameData> games = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("game_id");
                String white = rs.getString("white_username");
                String black = rs.getString("black_username");
                String name = rs.getString("game_name");

                String gameJson = rs.getString("game_state");
                ChessGame game = gson.fromJson(gameJson, ChessGame.class);

                games.add(new GameData(id, white, black, name, game));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error listing games", e);
        }
        return games;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT * FROM games WHERE game_id=?";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, gameID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String white = rs.getString("white_username");
                    String black = rs.getString("black_username");
                    String name = rs.getString("game_name");

                    String gameJson = rs.getString("game_state");
                    ChessGame game = gson.fromJson(gameJson, ChessGame.class);

                    return new GameData(gameID, white, black, name, game);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving game", e);
        }
    }

    @Override
    public int createGame(GameData gameData) throws DataAccessException {
        String sql = """
                INSERT INTO games (white_username, black_username, game_name, game_state)
                VALUES (?, ?, ?, ?)
                """;

        if (gameData.gameName() == null) {
            throw new DataAccessException("Game name cannot be null");
        }

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, gameData.whiteUsername());
            ps.setString(2, gameData.blackUsername());
            ps.setString(3, gameData.gameName());
            ps.setString(4, gson.toJson(gameData.game()));

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new DataAccessException("Failed to retrieve generated game ID");

        } catch (SQLException e) {
            throw new DataAccessException("Error creating game", e);
        }
    }

    @Override
    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException {
        String sql = """
                UPDATE games
                SET white_username=?, black_username=?, game_name=?, game_state=?
                WHERE game_id=?
                """;

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, updatedGame.whiteUsername());
            ps.setString(2, updatedGame.blackUsername());
            ps.setString(3, updatedGame.gameName());
            ps.setString(4, gson.toJson(updatedGame.game()));
            ps.setInt(5, gameID);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error updating game", e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM games";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing games", e);
        }
    }
}
