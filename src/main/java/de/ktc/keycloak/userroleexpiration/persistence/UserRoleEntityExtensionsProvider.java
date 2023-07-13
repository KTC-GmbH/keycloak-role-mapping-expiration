package de.ktc.keycloak.userroleexpiration.persistence;

import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;

import java.util.Arrays;
import java.util.List;

public class UserRoleEntityExtensionsProvider implements JpaEntityProvider {

    @Override
    public List<Class<?>> getEntities() {
        return Arrays.asList(UserRoleExpirationEntity.class, UserRoleResellerIDEntity.class, UserClientHasAuthEntity.class, RoleExpirationDurationEntity.class);
    }

    @Override
    public String getChangelogLocation() {
        return "META-INF/changelog.xml";
    }

    @Override
    public void close() {
        // do nothing
    }

    @Override
    public String getFactoryId() {
        return UserRoleExpirationEntityProviderFactory.ID;
    }

}
