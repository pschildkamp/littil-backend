package org.littil.api.auth;

// todo check whether this is needed
public record Role(String id, String role) {
    public final static String ADMIN = "admin";
    public final static String SCHOOL = "school";
    public final static String GUEST_TEACHER = "guestTeacher";
}