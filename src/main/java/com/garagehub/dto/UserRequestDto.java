package com.garagehub.dto;

import jakarta.validation.constraints.*;


public class UserRequestDto {
    @NotBlank(message = "Förnamn får inte vara tomt")
    private String firstName;
    @NotBlank(message = "Efternamn får inte vara tomt")
    private String lastName;
    @NotBlank(message = "E-post får inte vara tomt")
    @Email(message = "E-postadressen är ogiltig")
    private String email;

    public UserRequestDto() {}

    public UserRequestDto(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getFirstName() {return firstName;}
    public void setFirstName(String firstName) {this.firstName = firstName;}
    public String getLastName() {return lastName;}
    public void setLastName(String lastName) {this.lastName = lastName;}
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
}
