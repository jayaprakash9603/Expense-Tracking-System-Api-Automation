package com.jaya.payloads;

public class DashboardPreferencePayload {
    
    public static String createDefaultLayoutConfig() {
        return "{\"widgets\":[{\"id\":\"expenses\",\"position\":1},{\"id\":\"budget\",\"position\":2},{\"id\":\"analytics\",\"position\":3}],\"theme\":\"light\"}";
    }
    
    public static String createCustomLayoutConfig() {
        return "{\"widgets\":[{\"id\":\"budget\",\"position\":1},{\"id\":\"expenses\",\"position\":2}],\"theme\":\"dark\",\"showNotifications\":true}";
    }
    
    public static String createMinimalLayoutConfig() {
        return "{\"widgets\":[],\"theme\":\"default\"}";
    }
    
    public static String createComplexLayoutConfig() {
        return "{\"widgets\":[{\"id\":\"expenses\",\"position\":1,\"size\":\"large\"},{\"id\":\"budget\",\"position\":2,\"size\":\"medium\"},{\"id\":\"analytics\",\"position\":3,\"size\":\"small\"},{\"id\":\"notifications\",\"position\":4,\"size\":\"small\"}],\"theme\":\"dark\",\"layout\":\"grid\",\"columns\":3}";
    }
    
    public static String createInvalidLayoutConfig() {
        return "";
    }
    
    public static String createMalformedJsonConfig() {
        return "{invalid json}";
    }
}
