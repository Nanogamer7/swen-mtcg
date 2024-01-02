package at.nanopenguin.mtcg.db;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.val;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

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

    public static class DbQueryBuilder {
        public ResultSet executeQuery() throws SQLException {
            DbQuery dbQuery = this.build();
            if (dbQuery.command != SqlCommand.SELECT) throw new SQLException();
            return dbQuery.read();
        }

        public int executeUpdate() throws SQLException {
            DbQuery dbQuery = this.build();
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
        if (!parameters.isEmpty()) {
            return String.join(" = ?, ", parameters.keySet()) + " = ?";
        }
        return "";
    }

    private int create() throws SQLException {
        if (this.parameters.isEmpty()) throw new SQLException("No parameters provided for INSERT statement.");

        try (Connection connection = connect()) {
            String columns = this.parameters.keySet().stream()
                    .filter(columnName -> columnName.matches("[a-zA-Z0-9_]+"))
                    .collect(Collectors.joining(", "));

            String sql = String.format("INSERT INTO %s (%s) VALUES (%s) ON CONFLICT DO NOTHING;", table.table, columns, String.join(", ", Collections.nCopies(this.parameters.size(), "?")));
            // on conflict return int equals 0

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                int i = 1;
                for (val entry : this.parameters.entrySet()) {
                    preparedStatement.setObject(i++, entry.getValue());
                }

                return preparedStatement.executeUpdate();
            }
        }
    }

    private ResultSet read() throws SQLException {
        try (Connection connection = connect()) {
            StringJoiner columnJoiner = new StringJoiner(", ");
            if (this.columns.isEmpty()) {
                columnJoiner.add("*");
            }
            this.columns.forEach(columnJoiner::add);

            String sql = String.format("SELECT %s FROM %s%s;", columnJoiner, table.table,
                    this.conditions.isEmpty() ? "" : " WHERE (" + this.buildParameterizedString(this.conditions) + ")");

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                int i = 1;
                for (val entry : this.conditions.entrySet()) {
                    preparedStatement.setObject(i++, entry.getValue());
                }

                return preparedStatement.executeQuery();
            }
        }
    }

    private int update() throws SQLException {
        if (this.parameters.isEmpty()) throw new SQLException();
        if (this.conditions.isEmpty()) throw new SQLException();

        try (Connection connection = connect()) {
            String sql = String.format("UPDATE %s SET %s WHERE (%s);", table.table,
                    this.buildParameterizedString(this.parameters),
                    this.buildParameterizedString(this.conditions));

            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            int i = 1;
            for (val entry : this.parameters.entrySet()) {
                preparedStatement.setObject(i++, entry.getValue());
            }
            for (val entry : this.conditions.entrySet()) {
                preparedStatement.setObject(i++, entry.getValue());
            }

            return preparedStatement.executeUpdate();
        }
    }

    private int delete() throws SQLException {
        if (this.conditions.isEmpty()) throw new SQLException();
        try (Connection connection = connect()) {

            String sql = String.format("DELETE FROM %s WHERE (%s);", table.table, this.buildParameterizedString(this.conditions));

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                int i = 1;
                for (val entry : this.conditions.entrySet()) {
                    preparedStatement.setObject(i++, entry.getValue());
                }

                return preparedStatement.executeUpdate();
            }
        }
    }
}
