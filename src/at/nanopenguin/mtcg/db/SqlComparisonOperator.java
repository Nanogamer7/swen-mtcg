package at.nanopenguin.mtcg.db;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SqlComparisonOperator {
    EQUAL("="),
    GREATER_THAN(">"),
    LESS_THAN("<"),
    GREATER_EQUAL(">="),
    LESS_EQUAL("<="),
    NOT_EQUAL("!=");

    public final String Operator;

}
