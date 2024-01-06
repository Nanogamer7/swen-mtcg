package at.nanopenguin.mtcg.db;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

interface StatementExecutor {
    int executeUpdate() throws SQLException;
    ResultSet executeQuery() throws SQLException;
    void setObject(int i, Object o) throws SQLException;

    boolean execute() throws SQLException;
}

class PreparedStatementExecutor implements StatementExecutor {
    private final PreparedStatement preparedStatement;

    public PreparedStatementExecutor(PreparedStatement preparedStatement) {
        this.preparedStatement = preparedStatement;
    }

    @Override
    public int executeUpdate() throws SQLException {
        return preparedStatement.executeUpdate();
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        return preparedStatement.executeQuery();
    }

    @Override
    public void setObject(int i, Object o) throws SQLException {
        this.preparedStatement.setObject(i, o);
    }

    @Override
    public boolean execute() throws SQLException {
        return this.preparedStatement.execute();
    }
}

class CallableStatementExecutor implements StatementExecutor {
    private final CallableStatement callableStatement;

    public CallableStatementExecutor(CallableStatement callableStatement) {
        this.callableStatement = callableStatement;
    }

    @Override
    public int executeUpdate() throws SQLException {
        return callableStatement.executeUpdate();
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        return this.callableStatement.executeQuery();
    }

    @Override
    public void setObject(int i, Object o) throws SQLException {
        this.callableStatement.setObject(i, o);
    }

    @Override
    public boolean execute() throws SQLException {
        return this.callableStatement.execute();
    }
}
