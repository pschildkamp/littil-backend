package org.littil.api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class ErrorResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorId;
    private List<ErrorMessage> errors;

    public ErrorResponse(String errorId, ErrorMessage errorMessage) {
        this.errorId = errorId;
        this.errors = List.of(errorMessage);
    }

    public ErrorResponse(ErrorMessage errorMessage) {
        this(null, errorMessage);
    }

    public ErrorResponse(List<ErrorMessage> errors) {
        this.errorId = null;
        this.errors = errors;
    }

    @Getter
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorMessage {

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String path;
        private String message;

        public ErrorMessage(String message) {
            this.path = null;
            this.message = message;
        }
    }

}
