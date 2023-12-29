package at.nanopenguin.mtcg.application.service.schemas;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public record UserCredentials(String username, String password) {
}
