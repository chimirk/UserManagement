package usermanagementimpl;

import database.ForgotPasswordTokensGateway;
import database.User;
import database.UserGateway;
import database.VerificationTokensGateway;
import emailsender.EmailSender;
import utils.UTILS;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.UUID;

public class UserManagement implements usermanagementinterface.UserManagement {
    @Override
    public boolean signUp(String username, String fullName, String email) throws Exception {
        String verificationToken = UUID.randomUUID().toString();


        if (UserGateway.isANewUser(username)) {
            //save the token to the sql database
            VerificationTokensGateway.saveToken(verificationToken.toString(), username);
            //save user info
            UserGateway.saveUser(username, email, fullName);
            //send verification email to user
            EmailSender emailSender = new EmailSender();
            emailSender.sendVerification(email, verificationToken, true);
            return true;

        } else {
            throw new UserManagementException("This username is already registered");
        }
    }

    @Override
    public boolean forgotPassword(String email) throws Exception {
        String forgotPasswordToken = UUID.randomUUID().toString();
        String username = null;

        //verify email
        if (UserGateway.isValidEmail(email)) {
            username = UserGateway.getUsernameFromEmail(email);
            //save the token to the sql database
            ForgotPasswordTokensGateway.saveToken(forgotPasswordToken.toString(), username);
            //deactivate account
            UserGateway.updateActivationStatus("N", username);
            //send verification email to user
            EmailSender emailSender = new EmailSender();
            emailSender.sendVerification(email, forgotPasswordToken, false);
            return true;
        } else {
            throw new UserManagementException("the provided email is not registered in the system.");
        }
    }

    @Override
    public boolean emailVerification(String token, String password, boolean isANEwUser) throws UserManagementException, NoSuchAlgorithmException {
        String username;
        if (isANEwUser) {
            username = VerificationTokensGateway.getUsernameFromToken(token);
        } else {
            username = ForgotPasswordTokensGateway.getUsernameFromToken(token);
        }

        if (password != null && username != null) {
            String passwordHashed = UTILS.writeHashMD5(password);

            UserGateway.savePassword(passwordHashed, username);
            UserGateway.updateActivationStatus("Y", username);

            //delete token
            if (isANEwUser) {
                VerificationTokensGateway.deleteToken(token);
            } else {
                ForgotPasswordTokensGateway.deleteToken(token);
            }
            return true;
        } else {
            if (password == null) {
                throw new UserManagementException("password is empty or null");

            } else {
                throw new UserManagementException("username is empty or null");

            }
        }
    }

    @Override
    public boolean changePassword(String username, String oldPassword, String newPassword) throws Exception {
        String oldPasswordHashed = "";
        String newPasswordHashed = "";
        oldPasswordHashed = UTILS.writeHashMD5(oldPassword);
        newPasswordHashed = UTILS.writeHashMD5(newPassword);

        if (oldPassword.equals(newPassword)) {
            throw new UserManagementException("the old password and the new password are the same. please use a different new password");
        }

        //verify if there is a match for username and old password
        if (UserGateway.isUserNameAndOldPasswordValid(username, oldPasswordHashed)) {
            //update new password
            UserGateway.savePassword(newPasswordHashed, username);
            return true;
        } else {
            throw new UserManagementException("Provided combination of username and old password does not exist in database");
        }

    }

    @Override
    public User userLogin(String username, String password) {
        User result = new User();
        ArrayList<User> users = UserGateway.getAllUsersInfo();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(username) && users.get(i).getPassword().equals(password)) {
                result.setUsername(users.get(i).getUsername());
                result.setPassword(users.get(i).getPassword());
                result.setEmail(users.get(i).getEmail());
                result.setFullName(users.get(i).getFullName());
                return result;
            }
        }
        return null;
    }

}
