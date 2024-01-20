package at.nanopenguin.mtcg.db;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Table {
    USERS("users"),
    CARDS("cards"),
    PACKAGES("packages"),
    TRADES("trades"),
    NAN(null);

    public final String table;
}
