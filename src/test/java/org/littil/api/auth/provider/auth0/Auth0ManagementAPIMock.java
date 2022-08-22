package org.littil.api.auth.provider.auth0;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import io.quarkus.test.Mock;
import org.mockito.Mockito;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
@Mock
class Auth0ManagementAPIMock extends Auth0ManagementAPI {

    @Override
    @Produces
    public ManagementAPI produceManagementAPI() throws Auth0Exception {
        return Mockito.mock(ManagementAPI.class);
    }
}