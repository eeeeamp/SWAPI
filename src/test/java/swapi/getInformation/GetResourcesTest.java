package swapi.getInformation;

import swapi.base.BaseTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.List;
import java.util.stream.Stream;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;
import static org.assertj.core.api.Assertions.*;

public class GetResourcesTest extends BaseTest {

    @DisplayName("Get all people, vehicles and species")
    @ParameterizedTest(name = "resource: {0}")
    @MethodSource("getDataSource")
    public void getAllResources(String resource, int count, String name) {

        Response response = given()
                .spec(reqSpec)
                .when()
                .get(BASE_URL + resource)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        JsonPath jsonResponse = response.jsonPath();

        List<String> namesList = jsonResponse.getList("results.name");

        assertThat(jsonResponse.getInt("count")).isEqualTo(count);
        assertThat(namesList.get(1)).isEqualTo(name);

    }

    @DisplayName("Get one person, vehicle and species")
    @ParameterizedTest(name = "resource: {0}")
    @MethodSource("getOneDataSource")
    public void getSecondResource(String resource, String name, int id) {

        Response response = given()
                .spec(reqSpec)
                .pathParam("id", id)
                .when()
                .get(BASE_URL + resource + "/{id}")
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        JsonPath jsonResponse = response.jsonPath();

        assertThat(jsonResponse.getString("name")).isEqualTo(name);

    }

    @DisplayName("Get person, vehicle and species by search filtering")
    @ParameterizedTest(name = "resource: {0}")
    @MethodSource("getBySearchParamDataSource")
    public void getResourceBySearchParam(String resource, String search, String name) {

        Response response = given()
                .spec(reqSpec)
                .queryParam("search", search)
                .when()
                .get(BASE_URL + resource)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        JsonPath jsonResponse = response.jsonPath();

        List<String> namesList = jsonResponse.getList("results.name");

        assertThat(namesList.get(0)).isEqualTo(name);

    }



    private static Stream<Arguments> getDataSource() {

        return Stream.of(
                Arguments.of("people", 87, "C-3PO"),
                Arguments.of("vehicles", 39, "T-16 skyhopper"),
                Arguments.of("species", 37, "Droid")
        );
    }

    private static Stream<Arguments> getOneDataSource() {

        return Stream.of(
                Arguments.of("people", "C-3PO", 2),
                Arguments.of("vehicles", "T-16 skyhopper", 6),
                Arguments.of("species", "Droid", 2)
        );
    }

    private static Stream<Arguments> getBySearchParamDataSource() {

        return Stream.of(
                Arguments.of("people", "vad", "Darth Vader" ),
                Arguments.of("vehicles", "snow", "Snowspeeder"),
                Arguments.of("species", "hum", "Human")
        );
    }

}
