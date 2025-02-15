package org.littil.api.school.api;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.junit.mockito.InjectSpy;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.oidc.OidcSecurity;
import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.littil.api.auth.service.AuthenticationService;
import org.littil.api.coordinates.service.Coordinates;
import org.littil.api.coordinates.service.CoordinatesService;
import org.littil.api.exception.ErrorResponse;
import org.littil.api.school.service.School;
import org.littil.api.user.service.User;
import org.littil.api.user.service.UserService;
import org.littil.mock.auth0.APIManagementMock;

import io.quarkus.test.security.oidc.Claim;

import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

@QuarkusTest
@TestHTTPEndpoint(SchoolResource.class)
@QuarkusTestResource(APIManagementMock.class)
class SchoolResourceTest {
    
    @InjectSpy
    UserService userService;
    
    @InjectMock
    CoordinatesService coordinatesService;
    
    @InjectMock
    AuthenticationService authenticationService;
    
    
    @Test
    void givenFindAllUnauthorized_thenShouldReturnForbidden() {
        given() //
                .when() //
                .get() //
                .then() //
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "littil", roles = "viewer")
    @OidcSecurity(claims = {
            @Claim(key = "https://littil.org/littil_user_id", value = "0ea41f01-cead-4309-871c-c029c1fe19bf") })
    void givenFindAll_thenShouldReturnMultipleSchools() {
        given() //
                .when() //
                .get() //
                .then() //

                .statusCode(200);
    }
 
    @Test
    @TestSecurity(user = "littil", roles = "viewer")
    @OidcSecurity(claims = {
            @Claim(key = "https://littil.org/littil_user_id", value = "0ea41f01-cead-4309-871c-c029c1fe19bf") })
    void givenGetSchoolById_thenShouldReturnSuccessfully() {

        SchoolPostResource school = getDefaultSchool();
        School saved = saveSchool(school);
  
        School got = given()
                .when()
                .get("/{id}", saved.getId())
                .then()
                .statusCode(200)
                .extract().as(School.class);

        assertThat(saved).isEqualTo(got);
    }


    @Test
    @TestSecurity(user = "littil", roles = "viewer")
    @OidcSecurity(claims = {
            @Claim(key = "https://littil.org/littil_user_id", value = "0ea41f01-cead-4309-871c-c029c1fe19bf") })
    void givenGetSchoolByUnknownId_thenShouldReturnNotFound() {
        given()
                .when()
                .get("/{id}", UUID.randomUUID())
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = "littil", roles = "viewer")
    @OidcSecurity(claims = {
            @Claim(key = "https://littil.org/littil_user_id", value = "0ea41f01-cead-4309-871c-c029c1fe19bf") })
    void givenGetSchoolByName_thenShouldReturnSuccessfully() {

        SchoolPostResource school = getDefaultSchool();
        School saved = saveSchool(school);
        
        String validName = saved.getName();
        
        List<School> got = given()
                .when()
                .get("/name/{name}", validName)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getList(".", School.class);

        assertThat(saved).isIn(got);
    }

    @Test
    @TestSecurity(user = "littil", roles = "viewer")
    @OidcSecurity(claims = {
            @Claim(key = "https://littil.org/littil_user_id", value = "0ea41f01-cead-4309-871c-c029c1fe19bf") })
    void givenGetSchoolByUnknownName_thenShouldReturnNotFound() {
        given()
                .when()
                .get("/{name}", RandomStringUtils.randomAlphabetic(10))
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = "littil", roles = "viewer")
    @OidcSecurity(claims = {
            @Claim(key = "https://littil.org/littil_user_id", value = "0ea41f01-cead-4309-871c-c029c1fe19bf") })
    void givenCreateNewSchool_thenShouldBeCreatedSuccessfully() {       
        SchoolPostResource school = getDefaultSchool();
        School savedSchool = saveSchool(school);
        assertThat(savedSchool.getId()).isNotNull();
    }

    @Test
    @TestSecurity(user = "littil", roles = "viewer")
    @OidcSecurity(claims = {
            @Claim(key = "https://littil.org/littil_user_id", value = "0ea41f01-cead-4309-871c-c029c1fe19bf") })
    void givenCreateNewSchoolWithoutRequiredName_thenShouldReturnWithAnErrorResponse() {
        SchoolPostResource school = getDefaultSchool();
        school.setName(null);

        User createdUser = createAndSaveUser();
        doReturn(Optional.ofNullable(createdUser)).when(userService).getUserById(any(UUID.class)); 
        doNothing().when(authenticationService).addAuthorization(any(),any(), any());

        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .body(school)
                .put()
                .then()
                .statusCode(400)
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId()).isNull();
        assertThat(errorResponse.getErrors())
                .isNotNull()
                .hasSize(1)
                .contains(new ErrorResponse.ErrorMessage("createOrUpdate.school.name", getErrorMessage("School.name.required")));
    }

    @Test
    @TestSecurity(user = "littil", roles = "viewer")
    @OidcSecurity(claims = {
            @Claim(key = "https://littil.org/littil_user_id", value = "0ea41f01-cead-4309-871c-c029c1fe19bf") })
    void givenCreateNewSchoolWithRequiredNameBlank_thenShouldReturnWithAnErrorResponse() {
        SchoolPostResource school = getDefaultSchool();
        school.setName("");

        User createdUser = createAndSaveUser();
        doReturn(Optional.ofNullable(createdUser)).when(userService).getUserById(any(UUID.class)); 
        doNothing().when(authenticationService).addAuthorization(any(),any(), any());

        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .body(school)
                .put()
                .then()
                .statusCode(400)
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId()).isNull();
        assertThat(errorResponse.getErrors())
                .isNotNull()
                .hasSize(1)
                .contains(new ErrorResponse.ErrorMessage("createOrUpdate.school.name", getErrorMessage("School.name.required")));
    }
    @Test
    @TestSecurity(user = "littil", roles = "viewer")
    @OidcSecurity(claims = {
            @Claim(key = "https://littil.org/littil_user_id", value = "0ea41f01-cead-4309-871c-c029c1fe19bf") })
    void givenCreateNewSchoolWithoutRequiredNameAndAddressAndInvalidContactPersonEmail_thenShouldReturnWithAnErrorResponse() {
        SchoolPostResource school = getDefaultSchool();
        school.setName(null);
        school.setAddress(null);

        User createdUser = createAndSaveUser();
        doReturn(Optional.ofNullable(createdUser)).when(userService).getUserById(any(UUID.class)); 
        doNothing().when(authenticationService).addAuthorization(any(),any(), any());

        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .body(school)
                .put()
                .then()
                .statusCode(400)
                .extract()
                .as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId()).isNull();
        assertThat(errorResponse.getErrors())
                .isNotNull()
                .hasSize(2)
                .contains(
                        new ErrorResponse.ErrorMessage("createOrUpdate.school.name", getErrorMessage("School.name.required")),
                        new ErrorResponse.ErrorMessage("createOrUpdate.school.address", getErrorMessage("School.address.required"))
                );
    }

    @Test
    @TestSecurity(user = "littil", roles = "schools")
    @OidcSecurity(claims = {
            @Claim(key = "https://littil.org/littil_user_id", value = "0ea41f01-cead-4309-871c-c029c1fe19bf") })
    void givenDeleteNonExistingSchoolById_thenShouldReturnNotFound() {
        given()
                .contentType(ContentType.JSON)
                .delete("/{id}", UUID.randomUUID())
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "littil", roles = "schools")
    @OidcSecurity(claims = {
            @Claim(key = "https://littil.org/littil_user_id", value = "0ea41f01-cead-4309-871c-c029c1fe19bf") })
    @Disabled("tokenHelper.getCustomClaim() returns null in AbstractSecurityInterceptor")
    void givenDeleteSchoolById_thenShouldDeleteSuccessfully() {
        SchoolPostResource school = getDefaultSchool();
        School savedSchool = saveSchool(school);

        given()
                .contentType(ContentType.JSON)
                .delete("/{id}", savedSchool.getId())
                .then()
                .statusCode(200);

        given()
                .contentType(ContentType.JSON)
                .get("/{id}", savedSchool.getId())
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = "littil", roles = "viewer")
    @OidcSecurity(claims = {
            @Claim(key = "https://littil.org/littil_user_id", value = "0ea41f01-cead-4309-871c-c029c1fe19bf") })
    void givenUpdatingNameOfSchool_thenShouldUpdateSuccessfully() {
        SchoolPostResource school = getDefaultSchool();
        String newName = RandomStringUtils.randomAlphabetic(10);

        School saved = saveSchool(school);
        saved.setName(newName);
        assertNotNull(saved.getId());

        School updated = given()
                .contentType(ContentType.JSON)
                .body(saved)
                .put()
                .then()
                .statusCode(200)
                .extract().as(School.class);

        assertThat(updated.getName()).isEqualTo(newName);
        assertThat(updated).isEqualTo(saved);
    }

    @Test
    @TestSecurity(user = "littil", roles = "viewer")
    @OidcSecurity(claims = {
            @Claim(key = "https://littil.org/littil_user_id", value = "0ea41f01-cead-4309-871c-c029c1fe19bf") })
    void givenUpdatingUnknownSchool_thenShouldReturnWithErrorResponse() {
        SchoolPostResource school = getDefaultSchool();
        school.setId(UUID.randomUUID());

        given()
                .contentType(ContentType.JSON)
                .body(school)
                .put()
                .then()
                .statusCode(404);
    }

    private School saveSchool(SchoolPostResource school) {
        User createdUser = createAndSaveUser();
        doReturn(Optional.ofNullable(createdUser)).when(userService).getUserById(any(UUID.class)); 
        
        Coordinates coordinates = Coordinates.builder() //
                .lat(0.0) //
                .lon(0.0) //
                .build();
        doReturn(coordinates).when(coordinatesService).getCoordinates(any(), any());
        
        doNothing().when(authenticationService).addAuthorization(any(),any(), any());
          
        School saved = given()
                .contentType(ContentType.JSON)
                .body(school)
                .put()
                .then()
                .statusCode(200)
                .extract().as(School.class);
        return saved;
    }

    private SchoolPostResource getDefaultSchool() {
        SchoolPostResource school = new SchoolPostResource();
        school.setName(RandomStringUtils.randomAlphabetic(10));
        school.setAddress(RandomStringUtils.randomAlphabetic(10));
        school.setPostalCode(RandomStringUtils.randomAlphabetic(6));
        school.setFirstName(RandomStringUtils.randomAlphabetic(10));
        school.setSurname(RandomStringUtils.randomAlphabetic(10));

        return school;
    }

    private User createAndSaveUser() {
        String emailAdress = RandomStringUtils.randomAlphabetic(10) + "@adres.nl";
        User user = new User();
        user.setEmailAddress(emailAdress);
        User createdUser = userService.createUser(user);

        return createdUser;
    }

    private String getErrorMessage(String key) {
        return ResourceBundle.getBundle("ValidationMessages").getString(key);
    }
}
