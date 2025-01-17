package org.littil.api.exception;

import org.littil.api.auth.provider.AuthProviderException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthProviderExceptionMapper implements ExceptionMapper<AuthProviderException> {

    @Override
    public Response toResponse(AuthProviderException e) {
        ErrorResponse.ErrorMessage errorMessage = new ErrorResponse.ErrorMessage(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(errorMessage);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
    }

}