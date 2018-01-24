package ng.com.addproducts;

/**
 * Created by manglide on 4/11/2017.
 */
public class User {
    private int id;
    private String email;
    private String password;
    private String date;

    public User() {}

    public User(String email,String password,String date) {
        this.email = email;
        this.password = password;
        this.date = date;
    }



    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getDate() {
        return date;
    }
}
