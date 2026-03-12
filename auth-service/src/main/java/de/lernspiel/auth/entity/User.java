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
    private int  id;

    @Column(unique=true, nullable=false) 
    private String username;

    @Column(unique=true, nullable=false)
    private String email;

    @Column(nullable=false)
    private String profilePicture;

    @Column(nullable=true) // Sollte natürlich false sein. Für Demozwecke wurde der Wert true gelassen.
    private String password;

    
    public User()  {}

    public User(String username, String email, String profilePicture, String password) {
        this.username = username;
        this.email = email;
        this.profilePicture =profilePicture;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
    public String getPassword() { 
        return password; 
    }

    public void setId(int id) {
        this.id=id;
    }

    public void setUsername(String username) {
        this.username=username;
    }

    public void setEmail(String email) {
        this.email=email;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture=profilePicture;
    }
    public void setPassword(String password) {
        this.password=password; 
    }
}
