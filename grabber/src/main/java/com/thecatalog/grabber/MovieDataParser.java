package com.thecatalog.grabber;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thecatalog.domain.Genre;
import com.thecatalog.domain.Movie;
import com.thecatalog.domain.ProductionCompany;
import com.thecatalog.domain.id.GenreId;
import com.thecatalog.domain.id.MovieId;
import com.thecatalog.domain.id.ProductionCompanyId;
import com.thecatalog.grabber.util.ToBooleanFunction;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToIntFunction;


public class MovieDataParser implements DataParser<Movie> {

    private final ObjectMapper objectMapper;

    public MovieDataParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Movie parse(byte[] bytes) {
        try {
            JsonNode movieNode = objectMapper.readValue(bytes, JsonNode.class);
            JsonNode genreNode = movieNode.get("genres");
            Set<Genre> genres = new HashSet<>();
            ToIntFunction<JsonNode> toInt = JsonNode::asInt;
            Function<JsonNode, String> toString = JsonNode::asText;
            ToBooleanFunction<JsonNode> toBool = JsonNode::asBoolean;

            genreNode.forEach(node -> genres.add(new Genre(new GenreId(toInt.applyAsInt(node.get("id"))), toString.apply(node.get("name")))));
            Set<ProductionCompany> productionCompanies = new HashSet<>();
            JsonNode productionCompanyNode = movieNode.get("production_companies");
            productionCompanyNode.forEach(node -> productionCompanies.add(new ProductionCompany(new ProductionCompanyId(toInt.applyAsInt(node.get("id"))),
                    toString.apply(node.get("name")))));
            return new Movie(new MovieId(toInt.applyAsInt(movieNode.get("id"))),
                    toString.apply(movieNode.get("original_title")),
                    toString.apply(movieNode.get("title")),
                    toString.apply(movieNode.get("tagline")),
                    toString.apply(movieNode.get("overview")),
                    toString.apply(movieNode.get("status")),
                    toString.apply(movieNode.get("homepage")),
                    toBool.apply(movieNode.get("adult")),
                    genres,
                    productionCompanies,
                    toInt.applyAsInt(movieNode.get("budget"))
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
