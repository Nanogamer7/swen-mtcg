package at.nanopenguin.mtcg.db;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Table {
    USERS("users");

    public final String table;
}
