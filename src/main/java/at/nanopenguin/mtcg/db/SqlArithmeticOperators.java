package at.nanopenguin.mtcg.db;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
public enum SqlArithmeticOperators implements SqlOperator {
    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    MODULO("%");

    @Accessors(fluent = true)
    @Getter(onMethod = @__(@Override))
    public final String operator;
}
