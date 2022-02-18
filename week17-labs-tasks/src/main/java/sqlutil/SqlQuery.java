package sqlutil;

import java.sql.*;

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

    @Override
    public void close() throws SQLException {
        if (result != null) result.close();
        if (statement != null) statement.close();
        if (connection != null) connection.close();
    }
}
