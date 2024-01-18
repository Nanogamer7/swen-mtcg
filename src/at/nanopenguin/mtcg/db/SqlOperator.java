package at.nanopenguin.mtcg.db;

import lombok.RequiredArgsConstructor;

public interface SqlOperator {
    default String operator() { return "="; };
}

