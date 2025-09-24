package org.bank.domain;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Client {
    private long id;
    private String username;
    private String passwordHash;
    private String fullName;
    private String nationalId;
    private BigDecimal monthlyIncome;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private String Role;


    public Client(long id,
                  String username,
                  String passwordHash,
                  String fullName,
                  String nationalId,
                  BigDecimal monthlyIncome,
                  String email,
                  String phone,
                  LocalDate birthDate,
                  String Role,
                  boolean active) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.nationalId = nationalId;
        this.monthlyIncome = monthlyIncome;
        this.email = email;
        this.phone = phone;
        this.birthDate = birthDate;
        this.Role = Role;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(BigDecimal monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }
}