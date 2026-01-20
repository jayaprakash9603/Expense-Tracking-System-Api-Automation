package com.jaya.payloads;

import com.jaya.pojo.RoleRequest;

import java.util.UUID;

public class RolePayload {
    
    public static RoleRequest createDefaultRoleRequest() {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        return new RoleRequest(
                "TEST_ROLE_" + uniqueId,
                "Test role created for automation testing"
        );
    }
    
    public static RoleRequest createRoleRequest(String name, String description) {
        return new RoleRequest(name, description);
    }
    
    public static RoleRequest createInvalidRoleRequest() {
        return new RoleRequest(null, null);
    }
    
    public static RoleRequest createRoleRequestWithEmptyName() {
        return new RoleRequest("", "Description without name");
    }
    
    public static RoleRequest createRoleRequestWithSpecialCharacters() {
        return new RoleRequest("TEST@ROLE#123", "Role with special characters");
    }
    
    public static RoleRequest createUpdateRoleRequest(String name, String description) {
        return new RoleRequest(name, description);
    }
}
