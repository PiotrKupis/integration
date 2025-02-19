package edu.iis.mto.blog.rest.test;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class GetUserTest extends FunctionalTests {

    private static final String GET_USER_API = "/blog/user/find";

    @Test
    void shouldReturnUserByEmailWhenPassedEmail() {
        Response response = given().accept(ContentType.JSON)
                .queryParam("searchString", "john@domain.com")
                .expect()
                .log()
                .all()
                .statusCode(HttpStatus.SC_OK)
                .when()
                .get(GET_USER_API);

        JSONArray responseBody = new JSONArray(response.getBody().asString());
        assertThat(responseBody.length(), equalTo(1));
    }

    @Test
    void shouldReturnUserByNameWhenPassedName() {
        Response response = given().accept(ContentType.JSON)
                .queryParam("searchString", "John")
                .expect()
                .log()
                .all()
                .statusCode(HttpStatus.SC_OK)
                .when()
                .get(GET_USER_API);

        JSONArray responseBody = new JSONArray(response.getBody().asString());
        assertThat(responseBody.length(), equalTo(1));
    }

    @Test
    void shouldReturnUserBySurnameWhenPassedSurname() {
        Response response = given().accept(ContentType.JSON)
                .queryParam("searchString", "Steward")
                .expect()
                .log()
                .all()
                .statusCode(HttpStatus.SC_OK)
                .when()
                .get(GET_USER_API);

        JSONArray responseBody = new JSONArray(response.getBody().asString());
        assertThat(responseBody.length(), equalTo(1));
    }

    @Test
    void shouldNotReturnUserWhenUserWasRemoved() {
        Response response = given().accept(ContentType.JSON)
                .queryParam("searchString", "dummyEmail3@domain.com")
                .expect()
                .log()
                .all()
                .statusCode(HttpStatus.SC_OK)
                .when()
                .get(GET_USER_API);

        JSONArray responseBody = new JSONArray(response.getBody().asString());
        assertThat(responseBody.length(), equalTo(0));
    }

}
