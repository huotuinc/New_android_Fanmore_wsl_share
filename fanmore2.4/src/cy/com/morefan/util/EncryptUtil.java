package cy.com.morefan.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

public class EncryptUtil
{

    private static class Holder
    {
        private static final EncryptUtil instance = new EncryptUtil();
    }

    private EncryptUtil()
    {

    }

    public static final EncryptUtil getInstance()
    {
        return Holder.instance;
    }

    public String encryptMd532(String source)
    {
        if (null == source || "".equals(source.trim()))
        {
            return null;
        } else
        {
            return new String(Hex.encodeHex(DigestUtils.md5(source)));
        }
    }
}
