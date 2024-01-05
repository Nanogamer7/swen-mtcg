package at.nanopenguin.mtcg.db;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Table {
    USERS("users"),
    CARDS("cards"),
    PACKAGES("packages");

    public final String table;
}
