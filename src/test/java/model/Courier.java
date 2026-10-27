package model;

public class Courier {
    private Long id;
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private String middleName;
    private String phone;

    public Courier() {
    }

    // Getters and Setters for all fields required for Jackson serialization
}