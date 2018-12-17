package utils;

public final class Encryption {

  public static String encryptDecryptXOR(String rawString) {

    // If encryption is enabled in Config.
    //Selvom vi har implementeret kryptering, kan vi hurtigt slå det fra igen
    if (Config.getEncryption()) {

      // Nøglen er predefined og gemt i koden
      // TODO: Create a more complex code and store it somewhere better: FIX
      char[] key = Config.getEncryptionkey();

      // Stringbuilder enables you to play around with strings and make useful stuff
      StringBuilder thisIsEncrypted = new StringBuilder();

      // TODO: This is where the magic of XOR is happening. Are you able to explain what is going on?: FIX
      //Kører igennem tekststrengen. Man tager et bogstav af gangen, derfor char. Hvis værdien er større end 0, så vil den lægge én til.
      for (int i = 0; i < rawString.length(); i++) {
        //Tager det krypterede bogstav og ligger det ind i en streng
        //^ gør at det bliver til en bineær værdi
        thisIsEncrypted.append((char) (rawString.charAt(i) ^ key[i % key.length]));
      }

      // Vi returnerer den krypterede string
      return thisIsEncrypted.toString();

    } else {
      // Vi returnerer uden at have gjort noget
      return rawString;
    }
  }
}
