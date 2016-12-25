package com.thecatalog.grabber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thecatalog.domain.Movie;
import com.thecatalog.domain.id.GenreId;
import com.thecatalog.domain.id.MovieId;
import com.thecatalog.domain.id.ProductionCompanyId;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class MovieDataParserTest {

    private final MovieDataParser movieDataParser = new MovieDataParser(new ObjectMapper());

    @Test
    public void shouldParseJSONCorrectly() {
        //given

        String json = "{\n" +
                "  \"adult\": false,\n" +
                "  \"backdrop_path\": \"/fCayJrkfRaCRCTh8GqN30f8oyQF.jpg\",\n" +
                "  \"belongs_to_collection\": null,\n" +
                "  \"budget\": 63000000,\n" +
                "  \"genres\": [\n" +
                "    {\n" +
                "      \"id\": 18,\n" +
                "      \"name\": \"Drama\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"homepage\": \"\",\n" +
                "  \"id\": 550,\n" +
                "  \"imdb_id\": \"tt0137523\",\n" +
                "  \"original_language\": \"en\",\n" +
                "  \"original_title\": \"Fight Club\",\n" +
                "  \"overview\": \"A ticking-time-bomb insomniac and a slippery soap salesman channel primal male aggression into a shocking new form of therapy. Their concept catches on, with underground \\\"fight clubs\\\" forming in every town, until an eccentric gets in the way and ignites an out-of-control spiral toward oblivion.\",\n" +
                "  \"popularity\": 0.5,\n" +
                "  \"poster_path\": null,\n" +
                "  \"production_companies\": [\n" +
                "    {\n" +
                "      \"name\": \"20th Century Fox\",\n" +
                "      \"id\": 25\n" +
                "    }\n" +
                "  ],\n" +
                "  \"production_countries\": [\n" +
                "    {\n" +
                "      \"iso_3166_1\": \"US\",\n" +
                "      \"name\": \"United States of America\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"release_date\": \"1999-10-12\",\n" +
                "  \"revenue\": 100853753,\n" +
                "  \"runtime\": 139,\n" +
                "  \"spoken_languages\": [\n" +
                "    {\n" +
                "      \"iso_639_1\": \"en\",\n" +
                "      \"name\": \"English\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"status\": \"Released\",\n" +
                "  \"tagline\": \"How much can you know about yourself if you've never been in a fight?\",\n" +
                "  \"title\": \"Fight Club\",\n" +
                "  \"video\": false,\n" +
                "  \"vote_average\": 7.8,\n" +
                "  \"vote_count\": 3439\n" +
                "}";

        //when
        Movie movie = movieDataParser.parse(json.getBytes());

        //then
        assertThat(movie.getMovieId(), is(new MovieId(550)));
        assertThat(movie.getOriginalTitle(), is("Fight Club"));
        assertThat(movie.getTitle(), is("Fight Club"));
        assertThat(movie.getTagLine(), is("How much can you know about yourself if you've never been in a fight?"));
        assertThat(movie.getOverview(), is("A ticking-time-bomb insomniac and a slippery soap salesman channel primal male aggression into a shocking new form of therapy. Their concept catches on, with underground \"fight clubs\" forming in every town, until an eccentric gets in the way and ignites an out-of-control spiral toward oblivion."));
        assertThat(movie.getStatus(), is("Released"));
        assertThat(movie.getHomePage(), is(""));
        assertThat(movie.isAdult(), is(false));
        assertThat(movie.getGenres().size(), is(1));
        assertThat(movie.getGenres().iterator().next().getGenreId(), is(new GenreId(18)));
        assertThat(movie.getGenres().iterator().next().getGenre(), is("Drama"));
        assertThat(movie.getProductionCompanies().iterator().next().getProductionCompanyId(), is(new ProductionCompanyId(25)));
        assertThat(movie.getProductionCompanies().iterator().next().getProductionCompany(), is("20th Century Fox"));
        assertThat(movie.getBudget(), is(63000000));
    }

}
