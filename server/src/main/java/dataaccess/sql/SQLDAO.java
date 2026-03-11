package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;

import java.sql.*;

public abstract class SQLDAO {
    private final Gson gson = new Gson();

    protected int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {

                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];

                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof ChessGame p) ps.setString(i + 1, gson.toJson(p));
                    else if (param != null) ps.setString(i + 1, param.toString());
                    else ps.setNull(i + 1, Types.NULL);
                }

                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }

        } catch (SQLException e) {
            throw new DataAccessException("Unable to update database", e);
        }
    }
}
