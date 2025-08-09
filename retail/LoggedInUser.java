package retail;

public class LoggedInUser {
    private static int id;
    private static String name;
    private static String email;
    private static String phone;

    public static void setUser(int userId, String userName, String userEmail, String userPhone) {
        id = userId;
        name = userName;
        email = userEmail;
        phone = userPhone;
    }

    public static int getId() {
        return id;
    }

    public static String getName() {
        return name;
    }

    public static String getEmail() {
        return email;
    }

    public static String getPhone() {
        return phone;
    }
}
