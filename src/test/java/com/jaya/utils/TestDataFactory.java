package com.jaya.utils;

import com.jaya.pojo.LoginRequest;
import com.jaya.pojo.SignupRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

public class TestDataFactory {
    
    private static final Random random = new Random();
    private static final String[] FIRST_NAMES = {"John", "Jane", "Mike", "Sarah", "David", "Emily", "Chris", "Lisa"};
    private static final String[] LAST_NAMES = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Davis", "Miller", "Wilson"};
    private static final String[] GENDERS = {"male", "female"};
    private static final String DEFAULT_PASSWORD = "Test@123";
    
    private TestDataFactory() {}
    
    public static String generateUniqueEmail() {
        return "testuser_" + System.currentTimeMillis() + "_" + random.nextInt(1000) + "@example.com";
    }
    
    public static String generateUniqueEmail(String domain) {
        return "testuser_" + System.currentTimeMillis() + "_" + random.nextInt(1000) + "@" + domain;
    }
    
    public static SignupRequest createUniqueSignupRequest() {
        SignupRequest request = new SignupRequest();
        request.setFirstName(getRandomFirstName());
        request.setLastName(getRandomLastName());
        request.setEmail(generateUniqueEmail());
        request.setPassword(DEFAULT_PASSWORD);
        request.setGender(getRandomGender());
        return request;
    }
    
    public static SignupRequest createSignupRequest(String email) {
        SignupRequest request = createUniqueSignupRequest();
        request.setEmail(email);
        return request;
    }
    
    public static SignupRequest createSignupRequest(String firstName, String lastName, 
            String email, String password, String gender) {
        SignupRequest request = new SignupRequest();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setEmail(email);
        request.setPassword(password);
        request.setGender(gender);
        return request;
    }
    
    public static LoginRequest createLoginRequest(String email, String password) {
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);
        return request;
    }
    
    public static SignupRequest createInvalidSignupRequest() {
        SignupRequest request = new SignupRequest();
        request.setFirstName("");
        request.setLastName("");
        request.setEmail("invalid-email");
        request.setPassword("123");
        request.setGender("unknown");
        return request;
    }
    
    public static double generateRandomAmount(double min, double max) {
        return Math.round((min + (max - min) * random.nextDouble()) * 100.0) / 100.0;
    }
    
    public static String generateExpenseDescription() {
        String[] descriptions = {
            "Grocery shopping", "Office supplies", "Transportation", 
            "Restaurant dinner", "Coffee", "Utilities", "Subscription",
            "Entertainment", "Healthcare", "Education"
        };
        return descriptions[random.nextInt(descriptions.length)] + " - " + UUID.randomUUID().toString().substring(0, 8);
    }
    
    public static String getTodayDate() {
        return LocalDate.now().format(DateTimeFormatter.ISO_DATE);
    }
    
    public static String getPastDate(int daysAgo) {
        return LocalDate.now().minusDays(daysAgo).format(DateTimeFormatter.ISO_DATE);
    }
    
    public static String getFutureDate(int daysAhead) {
        return LocalDate.now().plusDays(daysAhead).format(DateTimeFormatter.ISO_DATE);
    }
    
    public static String getFirstDayOfMonth() {
        return LocalDate.now().withDayOfMonth(1).format(DateTimeFormatter.ISO_DATE);
    }
    
    public static String getLastDayOfMonth() {
        LocalDate now = LocalDate.now();
        return now.withDayOfMonth(now.lengthOfMonth()).format(DateTimeFormatter.ISO_DATE);
    }
    
    public static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }
    
    public static String generateShortId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
    
    public static int generateRandomId(int max) {
        return random.nextInt(max) + 1;
    }
    
    private static String getRandomFirstName() {
        return FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
    }
    
    private static String getRandomLastName() {
        return LAST_NAMES[random.nextInt(LAST_NAMES.length)];
    }
    
    private static String getRandomGender() {
        return GENDERS[random.nextInt(GENDERS.length)];
    }
    
    public static String getDefaultPassword() {
        return DEFAULT_PASSWORD;
    }
}
