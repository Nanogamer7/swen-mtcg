package at.nanopenguin.mtcg.application.service;

import at.nanopenguin.mtcg.http.HttpRequest;
import at.nanopenguin.mtcg.http.Response;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.SQLException;

public interface Service {

    Response handleRequest(HttpRequest request) throws JsonProcessingException, SQLException, ArrayIndexOutOfBoundsException;
}
