package models;

// This class is a blueprint for a User in our system.
// Think of it like a form — every user has these fields.

public class User {

    // These are the fields (information) every user has
    private int id;           // unique number for each user (database gives this)
    private String name;      // full name e.g. "Meron Tadesse"
    private String email;     // email e.g. "meron@gmail.com"
    private String password;  // their password (we'll encrypt this later)
    private String role;      // what kind of user: "driver", "officer", "admin"

    // CONSTRUCTOR — this runs when we create a new User
    // Like filling out the form for the first time
    public User(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // GETTERS — ways to read each field
    public int getId()           { return id; }
    public String getName()      { return name; }
    public String getEmail()     { return email; }
    public String getPassword()  { return password; }
    public String getRole()      { return role; }

    // SETTERS — ways to update each field
    public void setId(int id)              { this.id = id; }
    public void setName(String name)       { this.name = name; }
    public void setEmail(String email)     { this.email = email; }
    public void setPassword(String p)      { this.password = p; }
    public void setRole(String role)       { this.role = role; }

    // This lets us print a User nicely e.g. System.out.println(user)
    @Override
    public String toString() {
        return "User[id=" + id + ", name=" + name + ", role=" + role + "]";
    }
}