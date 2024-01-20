package at.nanopenguin.mtcg.application;

import java.util.UUID;

public record UserInfo(UUID id, String username, boolean admin) {
}
