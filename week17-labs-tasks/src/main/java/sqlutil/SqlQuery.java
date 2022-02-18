package sqlutil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SqlQuery implements AutoCloseable {

    private final Connection connection;
    private PreparedStatement statement;
    private ResultSet result;

    public SqlQuery(Connection connection) {
        this.connection = connection;
    }

    public Connection connection() {
        return connection;
    }

    public PreparedStatement statement() {
        return statement;
    }

    public void setStatement(PreparedStatement statement) throws SQLException {
        if (this.statement != null) this.statement.close();
        this.statement = statement;
    }

    public ResultSet result() {
        return result;
    }

    public void setResult(ResultSet result) throws SQLException {
        if (this.result != null) this.result.close();
        this.result = result;
    }

    public void setParams(SqlParam... params) throws SQLException {
        if (statement == null) throw new IllegalStateException("Statement is null");
        for (SqlParam param : params) {
            if (param.getValue() instanceof String s) {
                statement.setString(param.getIndex(), s);
            } else if (param.getValue() instanceof Integer i) {
                statement.setInt(param.getIndex(), i);
            } else if (param.getValue() instanceof Long l) {
                statement.setLong(param.getIndex(), l);
            } else if (param.getValue() instanceof Double d) {
                statement.setDouble(param.getIndex(), d);
            } else if (param.getValue() instanceof LocalDate date) {
                statement.setDate(param.getIndex(), Date.valueOf(date));
            } else if (param.getValue() instanceof LocalDateTime dateTime) {
                statement.setTimestamp(param.getIndex(), Timestamp.valueOf(dateTime));
            } else if (param.getValue() instanceof LocalTime time) {
                statement.setTime(param.getIndex(), Time.valueOf(time));
            } else {
                throw new IllegalArgumentException(String.format("Invalid param type at index %d: %s", param.getIndex(), param.getValue().getClass().getName()));
            }
        }
    }

    @Override
    public void close() throws SQLException {
        if (result != null) result.close();
        if (statement != null) statement.close();
        if (connection != null) connection.close();
    }
}
