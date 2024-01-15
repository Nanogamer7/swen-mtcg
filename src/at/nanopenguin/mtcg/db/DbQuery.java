package at.nanopenguin.mtcg.db;

import at.nanopenguin.mtcg.Pair;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.val;

import java.sql.*;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Builder
public final class DbQuery {
    private static final String connectionString = "jdbc:postgresql://localhost:5432/mydb?user=postgres&password=postgres";

    @NonNull
    private final SqlCommand command;
    @NonNull
    private final Table table;
    @Singular
    private List<String> columns;
    @Singular
    private SortedMap<String, Object> parameters;
    @Singular
    private SortedMap<String, Object> conditions;
    private String returnColumn;
    private String customSql;
    @Singular
    private List<Object> values;
    private boolean isScript;
    private String postfix = null;


    public static class DbQueryBuilder {

        public List<Map<String, Object>> executeQuery() throws SQLException {
            DbQuery dbQuery = this.build();
            if (dbQuery.customSql != null)
                return DbQuery.executeQuery(dbQuery.customSql, dbQuery.values, dbQuery.isScript);
            if (dbQuery.command != SqlCommand.SELECT && dbQuery.returnColumn == null) throw new SQLException();
            return switch (dbQuery.command) {
                case INSERT -> dbQuery.create(true);
                case SELECT -> dbQuery.read();
                case UPDATE -> dbQuery.update(true);
                default -> throw new SQLException();
            };
        }

        public int executeUpdate() throws SQLException {
            DbQuery dbQuery = this.build();
            if (dbQuery.customSql != null)
                return DbQuery.executeUpdate(dbQuery.customSql, dbQuery.values, dbQuery.isScript);
            if (this.returnColumn != null) throw new SQLException();
            return switch (dbQuery.command) {
                case INSERT -> dbQuery.create();
                case UPDATE -> dbQuery.update();
                case DELETE -> dbQuery.delete();
                default -> throw new SQLException();
            };
        }

        public boolean execute() throws SQLException {
            DbQuery dbQuery = this.build();
            if (dbQuery.customSql == null) throw new SQLException();
            return DbQuery.execute(dbQuery.customSql, dbQuery.values, dbQuery.isScript);
        }

        public DbQuery.DbQueryBuilder customSql(String customSql) {
            this.customSql = customSql;
            this.command = SqlCommand.CUSTOM;
            this.table = Table.NAN;
            return this;
        }

        public DbQuery.DbQueryBuilder isScript() {
            this.isScript = true;
            return this;
        }
    }

    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(connectionString);
    }

    private String buildParameterizedString(@NonNull SortedMap<String, Object> parameterMap, String separator) {

        if (parameterMap.isEmpty()) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (val parameter : parameterMap.entrySet()) {
            if (!stringBuilder.isEmpty()) stringBuilder.append(separator);
            if (parameter.getValue() instanceof List) {
                stringBuilder
                        .append(parameter.getKey())
                        .append(" IN (")
                        .append(String.join("", Collections.nCopies(((List<?>) parameter.getValue()).size() - 1, "?, ")))
                        .append("?)");
                continue;
            }

            if(parameter.getValue() instanceof Pair && ((Pair<?, ?>) parameter.getValue()).right() instanceof SqlComparisonOperator sqlComparisonOperator) {
                stringBuilder
                        .append(parameter.getKey())
                        .append(" ")
                        .append(sqlComparisonOperator.Operator)
                        .append(" ?");
                continue;
            }

            stringBuilder
                    .append(parameter.getKey())
                    .append(" = ?");
        }

        return stringBuilder.toString();
    }

    private static int executeUpdate(String sql, List<Object> parameterValues) throws SQLException {
        return executeUpdate(sql, parameterValues, false);
    }

    private static int executeUpdate(String sql, List<Object> parameterValues, boolean isScript) throws SQLException {
        try (Connection connection = connect()) {
            StatementExecutor statementExecutor = isScript ?
                    new CallableStatementExecutor(connection.prepareCall(sql)) :
                    new PreparedStatementExecutor(connection.prepareStatement(sql));
            int i = 1;
            for (val value : parameterValues) {
                if (value instanceof List<?>) {
                    for (Object o : (List<?>) value)
                        statementExecutor.setObject(i++, o);
                    continue;
                }
                statementExecutor.setObject(i++, value);
            }

            return statementExecutor.executeUpdate();
        }
    }

    private static boolean execute(String sql, List<Object> parameterValues) throws SQLException {
        return execute(sql, parameterValues, false);
    }

    private static boolean execute(String sql, List<Object> parameterValues, boolean isScript) throws SQLException {
        try (Connection connection = connect()) {
            StatementExecutor statementExecutor = isScript ?
                    new CallableStatementExecutor(connection.prepareCall(sql)) :
                    new PreparedStatementExecutor(connection.prepareStatement(sql));
            int i = 1;
            for (val value : parameterValues) {
                if (value instanceof List<?>) {
                    for (Object o : (List<?>) value)
                        statementExecutor.setObject(i++, o);
                    continue;
                }
                statementExecutor.setObject(i++, value);
            }

            return statementExecutor.execute();
        }
    }

    private static List<Map<String, Object>> executeQuery(String sql, List<Object> parameterValues) throws SQLException {
        return executeQuery(sql, parameterValues, false);
    }

    private static List<Map<String, Object>> executeQuery(String sql, List<Object> parameterValues, boolean isScript) throws SQLException {
        try (Connection connection = connect()) {
            StatementExecutor statementExecutor = isScript ?
                    new CallableStatementExecutor(connection.prepareCall(sql)) :
                    new PreparedStatementExecutor(connection.prepareStatement(sql));
            int i = 1;
            for (val value : parameterValues) {
                if (value instanceof Pair<?,?>) {
                    statementExecutor.setObject(i++, ((Pair<?, ?>) value).left());
                    continue;
                }
                if (value instanceof List<?>) {
                    for (Object o : (List<?>) value)
                        statementExecutor.setObject(i++, o);
                    continue;
                }
                statementExecutor.setObject(i++, value);
            }

            try (ResultSet resultSet = statementExecutor.executeQuery()) {

                List<Map<String, Object>> result = new ArrayList<>();
                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                        row.put(resultSet.getMetaData().getColumnName(i), resultSet.getObject(i));
                    }
                    result.add(row);
                }

                return result;
            }
        }
    }

    private String getInsertStatement(boolean hasReturn) throws SQLException {
        if (this.parameters.isEmpty()) throw new SQLException("No parameters provided for INSERT statement.");

        String columns = this.parameters.keySet().stream()
                .filter(columnName -> columnName.matches("^[a-zA-Z0-9_]+( AS [a-zA-Z0-9_]+)?$"))
                .collect(Collectors.joining(", "));

        return String.format("INSERT INTO %s (%s) VALUES (%s) ON CONFLICT DO NOTHING%s%s;",
                table.table,
                columns,
                String.join(", ", Collections.nCopies(this.parameters.size(), "?")),
                hasReturn ? " RETURNING " + this.returnColumn : "",
                this.postfix != null ? " " + this.postfix : "");
    }

    private int create() throws SQLException {
        return executeUpdate(getInsertStatement(false), new ArrayList<>(this.parameters.values()));
    }

    private List<Map<String, Object>> create(boolean hasReturn) throws SQLException {
        if (!hasReturn) {
            // throw dev-is-stupid error
        }
        return executeQuery(getInsertStatement(true), new ArrayList<>(this.parameters.values()));
    }

    private List<Map<String, Object>> read() throws SQLException {
        StringJoiner columnJoiner = new StringJoiner(", ");
        if (this.columns.isEmpty()) {
            columnJoiner.add("*");
        }
        this.columns.forEach(columnJoiner::add);

        String sql = String.format("SELECT %s FROM %s%s%s;",
                columnJoiner,
                table.table,
                this.conditions.isEmpty() ? "" : " WHERE " + this.buildParameterizedString(this.conditions, " AND "),
                this.postfix != null ? " " + this.postfix : "");

        return executeQuery(sql, new ArrayList<>(this.conditions.values()));
    }

    private String getUpdateStatement(boolean hasReturn) throws SQLException {
        if (this.parameters.isEmpty()) throw new SQLException();
        if (this.conditions.isEmpty()) throw new SQLException();

        return String.format("UPDATE %s SET %s WHERE %s%s%s;",
                table.table,
                this.buildParameterizedString(this.parameters, ", "),
                this.buildParameterizedString(this.conditions, " AND "),
                hasReturn ? " RETURNING " + this.returnColumn : "",
                this.postfix != null ? " " + this.postfix : "");
    }

    private int update() throws SQLException {
        return executeUpdate(getUpdateStatement(false), new ArrayList<>(
                Stream.concat(
                        this.parameters.values().stream(),
                        this.conditions.values().stream()
                ).collect(Collectors.toList())));
    }

    private List<Map<String, Object>> update(boolean hasReturn) throws SQLException {
        if (!hasReturn) {
            // throw dev-is-stupid error
        }
        return executeQuery(getUpdateStatement(true), new ArrayList<>(
                Stream.concat(
                        this.parameters.values().stream(),
                        this.conditions.values().stream()
                ).collect(Collectors.toList())));
    }

    private int delete() throws SQLException {
        if (this.conditions.isEmpty()) throw new SQLException();

        String sql = String.format("DELETE FROM %s WHERE %s%s;",
                table.table,
                this.buildParameterizedString(this.conditions, " AND "),
                this.postfix != null ? " " + this.postfix : "");

        return executeUpdate(sql, new ArrayList<>(this.conditions.values()));
    }
}
