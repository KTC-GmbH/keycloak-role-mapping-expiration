package de.ktc.keycloak.userroleexpiration.service.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.keycloak.models.RoleModel;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Diese Klasse bündelt zusätzliche Attribute einer Rolle, um diese im AccessToken
 * eines Benutzers anzuzeigen.
 * Im Speziellen wird dieses DTO verwendet, um die Attribute einer Rolle auszugeben
 * und zusätzlich wird hier auch der Gültigkeitszeitraum einer Benutzer-/Rollen-Zuordnung
 * angezeigt.
 */
public class RoleAttributesDto implements Serializable {
    @JsonProperty(value = "name", index = 0)
    private String name;
    @JsonProperty(value = "expirationDate", index = 1)
    private String expirationDate;
    @JsonProperty(value = "attributes", index = 2)
    private Map<String, String> attributes;

    public RoleAttributesDto() {
        // default public constructor
    }

    public RoleAttributesDto(RoleModel roleModel) {
        this.name = roleModel.getName();
        this.attributes = roleModel.getAttributes()
                .entrySet().stream()
                .map(e -> Map.entry(e.getKey(), getFirst(e.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static String getFirst(List<String> attr) {
        if (attr == null || attr.isEmpty()) {
            return null;
        }
        return attr.get(0);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
}
