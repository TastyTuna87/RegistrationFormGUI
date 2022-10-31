import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class RegistrationForm extends JDialog {
    private JTextField tfName;
    private JTextField tfEmail;
    private JTextField tfPhone;
    private JPasswordField pfPassword;
    private JPasswordField pfConfirmPassword;
    private JButton btnRegister;
    private JButton btnCancel;
    private JPanel regPanel;

    public User user;


    public RegistrationForm(JFrame parent) {
        super(parent);
        setTitle("Create new account");
        setContentPane(regPanel);
        setMinimumSize(new Dimension(450, 470));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnRegister.addActionListener(actionEvent -> regUser());
        btnCancel.addActionListener(actionEvent -> dispose());
        setVisible(true);
    }

    private void regUser() {
        String name = tfName.getText();
        String email = tfEmail.getText();
        String phone = tfPhone.getText();
        String password = String.valueOf(pfPassword.getPassword());
        String confirmPassword = String.valueOf(pfConfirmPassword.getPassword());


        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill all the fields!",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Confirm password does not match with password!",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        user = addUserToDB(name, email, phone, password);
        if (user != null) {
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to register new user",
                    "Try again!",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private User addUserToDB(String name, String email, String phone, String password) {
        final String DB_URL = "jdbc:mysql://localhost:3306/registration";
        final String USERNAME = "root";
        final String PASSWORD = "Test123";

        try {
            Connection con = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            System.out.println("Connection Successful");
            Statement stm = con.createStatement();

            String sql = "INSERT INTO users (name, email, phone, password) " +
                    "VALUES (?, ?, ?, ?)";

            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, phone);
            preparedStatement.setString(4, password);

            int addedRows = preparedStatement.executeUpdate();
            if (addedRows > 0) {
                user = new User();
                user.name = name;
                user.email = email;
                user.phone = phone;
                user.password = password;
            }
            stm.close();
            con.close();
        } catch (SQLException ex) {
            ex.getMessage();
        }

        return user;
    }

    public static void main(String[] args) {
        RegistrationForm myForm = new RegistrationForm(null);
        User user = myForm.user;
        if (user != null) {
            System.out.println("Successful registration of: " + user.name);
        } else {
            System.out.println("Registration canceled");
        }
    }


}
