package com.garagehub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequestDto {

    @NotBlank(message = "Förnamn får inte vara tomt")
    private String firstName;
    @NotBlank(message = "Efternamn får inte vara tomt")
    private String lastName;
    @NotBlank(message = "E-post får inte vara tom")
    @Email(message = "E-postadressen är ogiltig")
    private String email;
    @NotBlank(message = "Lösenord får inte vara tomt")
    @Size(min = 8, message = "Lösenordet måste vara minst 8 tecken långt")
    private String password;

    public RegisterRequestDto() {}

    public RegisterRequestDto(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

}
