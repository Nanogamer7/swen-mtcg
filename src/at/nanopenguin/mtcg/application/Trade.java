package at.nanopenguin.mtcg.application;

import at.nanopenguin.mtcg.Pair;
import at.nanopenguin.mtcg.application.service.schemas.TradingDeal;
import at.nanopenguin.mtcg.db.DbQuery;
import at.nanopenguin.mtcg.db.SqlCommand;
import at.nanopenguin.mtcg.db.SqlComparisonOperator;
import at.nanopenguin.mtcg.db.Table;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class Trade {
    public static TradingDeal[] get(UUID userUuid) throws SQLException {
        val dbQueryBuilder = DbQuery.builder()
                .command(SqlCommand.SELECT)
                .table(Table.TRADES)
                .column("uuid AS id")
                .column("card AS cardToTrade")
                .column("card_type AS type")
                .column("min_dmg AS minimumDamage")
                .condition("user_uuid", new Pair<>(userUuid, SqlComparisonOperator.NOT_EQUAL));

        ArrayList<TradingDeal> trades = new ArrayList<>();
        for (val row : dbQueryBuilder.executeQuery()) {
            trades.add(new ObjectMapper().convertValue(row, TradingDeal.class));
        }
        return trades.toArray(new TradingDeal[0]);
    }

    public static boolean addTrade(TradingDeal tradingDeal, UUID userUuid) throws SQLException {
        if (DbQuery.builder()
                .customSql("""
                        SELECT *
                        FROM cards
                        WHERE uuid = ?::uuid
                            AND owner = ?::uuid
                            AND trade = false
                            AND deck = false;
                        """)
                .value(tradingDeal.cardToTrade())
                .value(userUuid)
                .executeQuery().isEmpty())
            return false;

        String sql = tradingDeal.id() != null ?
                "INSERT INTO trades (uuid, card, card_type, min_dmg, user_uuid) VALUES (?::uuid, ?::uuid, ?::cardtype, ?, ?::uuid);" :
                "INSERT INTO trades (card, card_type, min_dmg, user_uuid) VALUES (?::uuid, ?::cardtype, ?, ?::uuid);";

        val dbQueryBuilder = DbQuery.builder()
                .customSql(sql);
        if (tradingDeal.id() != null) dbQueryBuilder.value(tradingDeal.id());
        dbQueryBuilder
                .value(tradingDeal.cardToTrade())
                .value(tradingDeal.type())
                .value(tradingDeal.minimumDamage())
                .value(userUuid)
                .executeUpdate();

        return true;
    }

    public static synchronized boolean acceptTrade(UUID tradeUuid, UUID cardUuid, UUID userUuid) throws SQLException, NullPointerException {

        val tradeResult = DbQuery.builder()
                .command(SqlCommand.SELECT)
                .table(Table.TRADES)
                .column("uuid AS id")
                .column("card AS cardToTrade")
                .column("card_type AS type")
                .column("min_dmg AS minimumDamage")
                .column("user_uuid")
                .condition("uuid", tradeUuid)
                .executeQuery();

        if (tradeResult.isEmpty()) throw new NullPointerException();
        if (tradeResult.get(0).get("user_uuid").equals(userUuid)) return false;

        TradingDeal trade = new ObjectMapper().convertValue(tradeResult.get(0), TradingDeal.class);

        val offeredResult = DbQuery.builder()
                .command(SqlCommand.SELECT)
                .table(Table.CARDS)
                .condition("uuid", cardUuid)
                .condition("deck", false)
                .condition("owner", userUuid)
                .executeQuery();

        if (offeredResult.isEmpty()) return false;
        val card = offeredResult.get(0);

        if ("spell".equals(trade.type()) && !((String) card.get("name")).toLowerCase().endsWith("spell")) return false;
        // if (!((String) card.get("name")).toLowerCase().endsWith(trade.type())) return false;

        DbQuery.builder()
                .command(SqlCommand.UPDATE)
                .table(Table.CARDS)
                .parameter("owner", userUuid)
                .parameter("trade", false)
                .condition("uuid", UUID.fromString(trade.cardToTrade()))
                .executeUpdate();

        DbQuery.builder()
                .command(SqlCommand.UPDATE)
                .table(Table.CARDS)
                .parameter("owner", tradeResult.get(0).get("user_uuid"))
                .parameter("trade", false)
                .condition("uuid", cardUuid)
                .executeUpdate();

        DbQuery.builder()
                .command(SqlCommand.DELETE)
                .table(Table.TRADES)
                .condition("uuid", tradeUuid)
                .executeUpdate();

        return true;
    }

    public static boolean removeTrade(UUID tradeUuid, UUID userUuid) throws SQLException {
        if (DbQuery.builder()
                .command(SqlCommand.SELECT)
                .table(Table.TRADES)
                .condition("uuid", tradeUuid)
                .executeQuery().isEmpty()) throw new NullPointerException();

        return DbQuery.builder()
                .command(SqlCommand.DELETE)
                .table(Table.TRADES)
                .condition("uuid", tradeUuid)
                .condition("user_uuid", userUuid)
                .executeUpdate() > 0;
    }
}
