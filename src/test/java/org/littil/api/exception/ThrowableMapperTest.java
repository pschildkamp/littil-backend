package org.littil.api.exception;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.oidc.Claim;
import io.quarkus.test.security.oidc.OidcSecurity;
import org.junit.jupiter.api.Test;
import org.littil.api.guestTeacher.api.GuestTeacherResource;
import org.littil.api.guestTeacher.service.GuestTeacherService;
import org.mockito.Mockito;

import java.util.ResourceBundle;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@TestHTTPEndpoint(GuestTeacherResource.class)
class ThrowableMapperTest {

    @InjectMock
    GuestTeacherService teacherService;

    @Test
    @TestSecurity(user = "littil", roles = "viewer")
    @OidcSecurity(claims = {
            @Claim(key = "https://littil.org/littil_user_id", value = "0ea41f01-cead-4309-871c-c029c1fe19bf")
    })
    void throwUnexpectedRuntimeException() {
        Mockito.when(teacherService.findAll()).thenThrow(new RuntimeException("Completely Unexpected"));
        ErrorResponse errorResponse = given()
                .when()
                .get()
                .then()
                .statusCode(500)
                .extract().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorId()).isNotNull();
        assertThat(errorResponse.getErrors())
                .isNotNull()
                .hasSize(1)
                .contains(new ErrorResponse.ErrorMessage(ResourceBundle.getBundle("ValidationMessages").getString("System.error")));
    }
}