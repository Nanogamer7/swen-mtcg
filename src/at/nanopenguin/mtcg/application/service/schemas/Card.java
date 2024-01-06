package at.nanopenguin.mtcg.application.service.schemas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Card(@JsonProperty("id") UUID id, String name, Float damage) {
}
