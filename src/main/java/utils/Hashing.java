package utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.bouncycastle.util.encoders.Hex;

public final class Hashing {
  public String salt = "Katrine";

  // TODO: You should add a salt and make this secure : FIX
  public static String md5(String rawString) {
    try {

      // We load the hashing algoritm we wish to use.
      MessageDigest md = MessageDigest.getInstance("MD5");
    //  rawString = rawString + "jfd";

      // Konverterer til en byte array
      byte[] byteArray = md.digest(rawString.getBytes());

      // Initialiserer en string buffer
      StringBuffer sb = new StringBuffer();

      // Løber igennem byteArray, et element af gangen og tilføjer værdien til vores Strengbuffer
      for (int i = 0; i < byteArray.length; ++i) {
        sb.append(Integer.toHexString((byteArray[i] & 0xFF) | 0x100).substring(1, 3));
      }

      //Konverterer tilbage til en single string og returnerer
      return sb.toString();

    } catch (java.security.NoSuchAlgorithmException e) {

      //Hvis noget går galt, udskrives denne besked
      System.out.println("Kunne ikke hashe string");
    }

    return null;
  }

  // TODO: You should add a salt and make this secure : FIX
  public static String sha(String rawString) {
    try {
      // Vi loader den hashede algoritme, som vi vil bruge
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      rawString = rawString + Config.getSaltKey();

      // Vi konverterer til byte array
      byte[] hash = digest.digest(rawString.getBytes(StandardCharsets.UTF_8));

      //Vi opretter den hashede string
      String sha256hex = new String(Hex.encode(hash));

      // Og returnerer den
      return sha256hex;

    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return rawString;
  }

  public String hashWithSalt (String str) {
    String salt = str + this.salt;
    return sha(salt);
  }
}