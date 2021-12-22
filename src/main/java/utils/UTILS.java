package utils;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UTILS {

    public static String writeHashMD5 (String stringToHash) throws NoSuchAlgorithmException {
        MessageDigest md1 = MessageDigest.getInstance("MD5");
        md1.update(stringToHash.getBytes());
        byte[] digest1 = md1.digest();
        return DatatypeConverter
                .printHexBinary(digest1).toUpperCase();

    }

}
