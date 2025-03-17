package db.inserts;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface InsertProperties {
    void replaceQueryPlaceholders(PreparedStatement statement) throws SQLException;
    String getQuery();
}
