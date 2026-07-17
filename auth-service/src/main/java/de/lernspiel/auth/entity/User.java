package de.lernspiel.auth.entity;
import jakarta.persistence.*;

/*
 * Entityklasse für Hibernate. Erstellt automatisch Benutzerprofil in der DB und erlaubt einfaches direktes Handling ohne SQL
 */

@Entity
@Table(name="user")
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY) // JPA generiert ID selbstständig, indem die DB nach der nächsten freien ID sucht
    private Integer userID;

    @Column(nullable=false)
    private String password;

    @Column(nullable=false)
    private String type;

    @Column(nullable=true)
    private Integer classID;

    
    public User()  {}

    public User(String password, String type, Integer classID) {
        this.password = password;
        this.type = type;
        this.classID = classID;
    }

    public Integer getUserID() {
        return userID;
    }

    public String getType() {
        return type;
    }

    public Integer getClassID() {
        return classID;
    }
    public String getPassword() { 
        return password; 
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setClassID(Integer classID) {
        this.classID = classID;
    }
    public void setPassword(String password) {
        this.password=password; 
    }
}
