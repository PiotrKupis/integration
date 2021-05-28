package edu.iis.mto.blog.rest.test;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class GetPostTest extends FunctionalTests {

    private static final String GET_USER_POST_API = "/blog/user/{id}/post";

    @Test
    void shouldReturnTwoPostsWhenUserAddedTwoPosts() {
        Response response = given().accept(ContentType.JSON)
                .pathParams("id", 3)
                .expect()
                .log()
                .all()
                .statusCode(HttpStatus.SC_OK)
                .when()
                .get(GET_USER_POST_API);

        JSONArray responseBody = new JSONArray(response.getBody().asString());
        assertThat(responseBody.length(), equalTo(2));
    }

}
