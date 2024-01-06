package at.nanopenguin.mtcg.db;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Table {
    USERS("users"),
    CARDS("cards"),
    PACKAGES("packages"),
    NAN(null);

    public final String table;
}
