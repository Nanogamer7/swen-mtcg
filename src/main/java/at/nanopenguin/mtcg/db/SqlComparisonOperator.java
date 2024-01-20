package at.nanopenguin.mtcg.db;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
public enum SqlComparisonOperator implements SqlOperator {
    EQUAL("="),
    GREATER_THAN(">"),
    LESS_THAN("<"),
    GREATER_EQUAL(">="),
    LESS_EQUAL("<="),
    NOT_EQUAL("!=");

    @Accessors(fluent = true)
    @Getter(onMethod = @__(@Override))
    public final String operator;
}
