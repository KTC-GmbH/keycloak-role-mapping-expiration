package de.ktc.userrolemapping.expiration.persistence;

import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;

import java.util.Collections;
import java.util.List;

public class UserRoleExpirationEntityProvider implements JpaEntityProvider {

    @Override
    public List<Class<?>> getEntities() {
        return Collections.singletonList(UserRoleExpirationEntity.class);
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
