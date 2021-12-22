package usermanagementimpl;

import database.ForgotPasswordTokensGateway;
import database.UserGateway;
import database.VerificationTokensGateway;
import org.junit.jupiter.api.Test;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserManagementTest {
    UserManagement userManagement = new UserManagement();

    @Test
    void signUp() throws Exception {
        String userName = "user22";
        String email = "test22@gmail.com";
        String fullName = "test";

        assertTrue(userManagement.signUp(userName, fullName, email));

        UserGateway.deleteUserByUsername(userName);
        VerificationTokensGateway.deleteAllTokens();
    }

    @Test
    void shouldThrowExceptionIfUserIsAlreadyRegistered() {
        String userName = "user22";
        String email = "test22@gmail.com";
        String fullName = "test";
        UserGateway.saveUser(userName, email, fullName);

        assertThrows(UserManagementException.class, () -> userManagement.signUp(userName, fullName, email), "This username is already registered");

        UserGateway.deleteUserByUsername(userName);
        VerificationTokensGateway.deleteAllTokens();
    }

    @Test
    void forgotPassword() throws Exception {
        String userName = "user22";
        String email = "test22@gmail.com";
        String fullName = "test";
        UserGateway.saveUser(userName, email, fullName);

        assertTrue(userManagement.forgotPassword(email));

        UserGateway.deleteUserByUsername(userName);
        ForgotPasswordTokensGateway.deleteAllTokens();
    }

    @Test
    void shouldThrowExceptionIfProvidedEmailDoesNotExistsInDatabase() {
        String userName = "user22";
        String email = "test22@gmail.com";
        String fullName = "test";
        UserGateway.saveUser(userName, email, fullName);
        String badEmail = "test@gmail.com";

        assertThrows(UserManagementException.class,
                () -> userManagement.forgotPassword(badEmail),
                "the provided email is not registered in the system.");

        UserGateway.deleteUserByUsername(userName);
        ForgotPasswordTokensGateway.deleteAllTokens();

    }

    @Test
    void emailVerification() throws UserManagementException, NoSuchAlgorithmException {
        String token = "123-123";
        String userName = "test4";
        String email = "test5@gmail.com";
        String fullName = "test5 full name";
        String password = "123";

        //user should be already registered in the users table, just missing password and status
        UserGateway.saveUser(userName, email, fullName);

        //new user
        VerificationTokensGateway.saveToken(token, userName);
        assertTrue(userManagement.emailVerification(token, password, true));

        //user registered but forgot password
        ForgotPasswordTokensGateway.saveToken(token, userName);
        assertTrue(userManagement.emailVerification(token, password, false));

        UserGateway.deleteUserByUsername(userName);
        VerificationTokensGateway.deleteAllTokens();
        ForgotPasswordTokensGateway.deleteAllTokens();
    }

    @Test
    void shouldThrowExceptionIfNoSuchUserNameInTokensDatabase() {
        //this token does not exist in verification or forgot password tokens database
        String token = "123-123-fail-test";
        String password = "123";

        //new user
        assertThrows(UserManagementException.class, () -> userManagement.emailVerification(token, password, true));
        //user registered but forgot password
        assertThrows(UserManagementException.class, () -> userManagement.emailVerification(token, password, false));

        VerificationTokensGateway.deleteAllTokens();
        ForgotPasswordTokensGateway.deleteAllTokens();
    }

    @Test
    void changePassword() throws Exception {
        //user should be already registered
        String userName = "changepass";
        String email = "test7@gmail.com";
        String fullName = "test seven";
        String oldPassword = "test7old";
        String newPassword = "test7new";

        String oldPasswordHashed;
        MessageDigest md1 = MessageDigest.getInstance("MD5");
        md1.update(oldPassword.getBytes());
        byte[] digest1 = md1.digest();
        oldPasswordHashed = DatatypeConverter
                .printHexBinary(digest1).toUpperCase();

        UserGateway.saveUser(userName, email, fullName);
        UserGateway.savePassword(oldPasswordHashed, userName);
        UserGateway.updateActivationStatus("Y", userName);
        assertTrue(userManagement.changePassword(userName, oldPassword, newPassword));

        UserGateway.deleteUserByUsername(userName);
    }

    @Test
    void shouldThrowExceptionIfNoMatchForUserNameAndPassword() {
        //user should be already registered
        String userName = "chpass2";
        String email = "test8@gmail.com";
        String fullName = "test seven";
        String oldPassword = "test7old";
        String newPassword = "test7new";

        UserGateway.saveUser(userName, email, fullName);
        //provided password is not hashed, so it will through exception because there will be no match
        UserGateway.savePassword(oldPassword, userName);
        UserGateway.updateActivationStatus("Y", userName);

        assertThrows(UserManagementException.class, () ->userManagement.changePassword(userName, oldPassword, newPassword), "Provided combination of username and old password does not exist in database");

        UserGateway.deleteUserByUsername(userName);
    }

    @Test
    void userLogin() {
    }
}