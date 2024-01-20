package at.nanopenguin.mtcg.db;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
public enum SqlCompoundOperator implements SqlOperator {
    // this class does not work with PostgreSQL https://c.tenor.com/MYZgsN2TDJAAAAAC/tenor.gif
    ADD("+="),
    SUBTRACT("-="),
    MULTIPLY("*="),
    DIVIDE("/="),
    MODULO("%="),
    BIT_AND("&="),
    BIT_OR("|*="),
    BIT_XOR("^-=");

    @Accessors(fluent = true)
    @Getter(onMethod = @__(@Override))
    public final String operator;
}
