package com.ciaosgarage.iBill.beans.util.cryptor;

public interface Cryptor {
    String encryption(String str);

    String decryption(String str);

    void makeKey(String key);
}
