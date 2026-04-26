import java.sql.*;

public class sql_migrate {
    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection c = DriverManager.getConnection(
            "jdbc:mysql://38.181.25.197:3306/guanwang?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai",
            "guanwang", "RTYDCmFzR7WGib65");
        Statement s = c.createStatement();

        try {
            s.executeUpdate("ALTER TABLE users ADD COLUMN invite_code VARCHAR(16) DEFAULT NULL COMMENT '邀请码' AFTER status");
            System.out.println("OK: Added invite_code");
        } catch (SQLException e) {
            System.out.println("SKIP invite_code: " + e.getMessage());
        }

        try {
            s.executeUpdate("ALTER TABLE users ADD COLUMN invited_by INT DEFAULT NULL COMMENT '邀请人用户ID' AFTER invite_code");
            System.out.println("OK: Added invited_by");
        } catch (SQLException e) {
            System.out.println("SKIP invited_by: " + e.getMessage());
        }

        try {
            s.executeUpdate("ALTER TABLE users ADD UNIQUE INDEX uk_invite_code (invite_code)");
            System.out.println("OK: Added unique index");
        } catch (SQLException e) {
            System.out.println("SKIP index: " + e.getMessage());
        }

        // Verify
        ResultSet rs = s.executeQuery("DESCRIBE users");
        System.out.println("\n--- users table columns ---");
        while (rs.next()) {
            System.out.println(rs.getString("Field") + " | " + rs.getString("Type") + " | " + rs.getString("Null") + " | " + rs.getString("Default"));
        }

        s.close(); c.close();
    }
}
