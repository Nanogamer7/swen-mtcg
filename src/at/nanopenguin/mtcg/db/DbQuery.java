package at.nanopenguin.mtcg.db;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.val;

import java.sql.*;
import java.util.*;
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


    public static class DbQueryBuilder {

        public List<Map<String, Object>> executeQuery() throws SQLException {
            DbQuery dbQuery = this.build();
            if (dbQuery.command != SqlCommand.SELECT && dbQuery.returnColumn == null) throw new SQLException();
            if (dbQuery.customSql != null) return DbQuery.executeQuery(dbQuery.customSql, dbQuery.values);
            return switch (dbQuery.command) {
                case INSERT -> dbQuery.create(true);
                case SELECT -> dbQuery.read();
                case UPDATE -> dbQuery.update(true);
                default -> throw new SQLException();
            };
        }

        public int executeUpdate() throws SQLException {
            if (this.returnColumn != null) throw new SQLException();
            DbQuery dbQuery = this.build();
            if (dbQuery.customSql != null) return DbQuery.executeUpdate(dbQuery.customSql, dbQuery.values);
            return switch (dbQuery.command) {
                case INSERT -> dbQuery.create();
                case UPDATE -> dbQuery.update();
                case DELETE -> dbQuery.delete();
                default -> throw new SQLException();
            };
        }
    }

    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(connectionString);
    }

    private String buildParameterizedString(@NonNull SortedMap<String, Object> parameters) {
        if (parameters.isEmpty()) {
            return "";
        }
        return String.join(" = ?, ", parameters.keySet()) + " = ?";
    }

    private static int executeUpdate(String sql, List<Object> parameterValues) throws SQLException {
        try (Connection connection = connect()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            int i = 1;
            for (val value : parameterValues) {
                preparedStatement.setObject(i++, value);
            }

            return preparedStatement.executeUpdate();
        }
    }

    private static List<Map<String, Object>> executeQuery(String sql, List<Object> parameterValues) throws SQLException {
        try (Connection connection = connect()) {

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            int i = 1;
            for (val value : parameterValues) {
                preparedStatement.setObject(i++, value);
            }

            ResultSet resultSet = preparedStatement.executeQuery();

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

    private String getInsertStatement(boolean hasReturn) throws SQLException {
        if (this.parameters.isEmpty()) throw new SQLException("No parameters provided for INSERT statement.");

        String columns = this.parameters.keySet().stream()
                .filter(columnName -> columnName.matches("[a-zA-Z0-9_]+"))
                .collect(Collectors.joining(", "));

        return String.format("INSERT INTO %s (%s) VALUES (%s) ON CONFLICT DO NOTHING%s;",
                table.table,
                columns,
                String.join(", ", Collections.nCopies(this.parameters.size(), "?")),
                hasReturn ? " RETURNING " + this.returnColumn : "");
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

        String sql = String.format("SELECT %s FROM %s%s;",
                columnJoiner,
                table.table,
                this.conditions.isEmpty() ? "" : " WHERE (" + this.buildParameterizedString(this.conditions) + ")");

        return executeQuery(sql, new ArrayList<>(this.conditions.values()));
    }

    private String getUpdateStatement(boolean hasReturn) throws SQLException {
        if (this.parameters.isEmpty()) throw new SQLException();
        if (this.conditions.isEmpty()) throw new SQLException();

        return String.format("UPDATE %s SET %s WHERE (%s)%s;",
                table.table,
                this.buildParameterizedString(this.parameters),
                this.buildParameterizedString(this.conditions),
                hasReturn ? " RETURNING " + this.returnColumn : "");
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

        String sql = String.format("DELETE FROM %s WHERE (%s);",
                table.table,
                this.buildParameterizedString(this.conditions));

        return executeUpdate(sql, new ArrayList<>(this.conditions.values()));
    }
}
