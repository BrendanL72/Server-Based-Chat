package src;
public class A8 {

    public static int cipherKey(int secretKey, int rand)
    {
        String s = "" + secretKey + rand;

        s = s.hashCode() + "";

        int key = Integer.parseInt(s);

        return key;
    }

    public static String encryptString(String s, int cipherKey)
    {

        char[] ch = s.toCharArray();

        cipherKey = cipherKey % 17;

        for(int i = 0; i < ch.length; i++)
        {
            if(ch[i] != ' ')
                ch[i] = ch[i] += cipherKey;
        }

        String encrypted = String.valueOf(ch);

        return encrypted;
    }

    public static String decryptString(String s, int cipherKey)
    {
        char[] ch = s.toCharArray();

        cipherKey = cipherKey % 17;

        for(int i = 0; i < ch.length; i++)
        {
            if(ch[i] != ' ')
                ch[i] = ch[i] -= cipherKey;
        }

        String decrypted = String.valueOf(ch);

        return decrypted;
    }
}
