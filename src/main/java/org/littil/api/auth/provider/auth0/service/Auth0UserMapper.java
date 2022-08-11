package org.littil.api.auth.provider.auth0.service;

import com.auth0.json.mgmt.Role;
import com.auth0.json.mgmt.users.User;
import org.littil.api.auth.provider.Provider;
import org.littil.api.auth.service.AuthUser;
import org.mapstruct.Mapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
//@Mapper(componentModel = "cdi")
public class Auth0UserMapper {

	@Inject
	RoleMapper roleMapper;

	User toProviderEntity(AuthUser littleUser, String tempPassword) {
		User auth0User = new User("Username-Password-Authentication");
		auth0User.setEmail(littleUser.getEmailAddress());
		// todo move password gen to service
		auth0User.setPassword(tempPassword.toCharArray());
		return auth0User;
	}

	public AuthUser toDomain(User user) {
		AuthUser user1 = new AuthUser();
		user1.setEmailAddress(user.getEmail());
		user1.setId(user.getId());
		// todo check how to retrieve roles from auth0 user
		Set<Role> roles = (Set<Role>) user.getValues().get("roles");
		if (roles != null) {
			user1.setRoles( roles
					.stream()
					.map(roleMapper::toEntity)
					.collect(Collectors.toSet()));
		}
		user1.setAuthProvider(Provider.AUTH0);
		return user1;
	}
}
