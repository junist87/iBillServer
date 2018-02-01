package com.ciaosgarage.iBill.testTools;

import java.util.ArrayList;

public class VoSampler {
    ArrayList mailList = new ArrayList();
    ArrayList idList = new ArrayList();
    int index = 0;

    public String getEmail() {
        StringBuffer mail;
        do {
            mail = new StringBuffer();
            mail.append(getString(10));
            mail.append("@");
            mail.append(getString(10));
            mail.append(".");
            mail.append(getString(3));
        } while (mailList.contains(mail.toString()));

        mailList.add(mail.toString());
        return mail.toString();
    }

    public int getIndex() {
        index += 1;
        return index;
    }

    public String getStringDeniedDuplicated(int length) {
        String id;

        do {
            id = getString(length);

        } while (idList.contains(id));

        idList.add(id);
        return id;

    }

    public String getStringAllowedDuplicated(int length) {
        return getString(length);
    }

    private String getChar() {
        int decChar = (int) (Math.random() * 25) + 97;
        return String.valueOf((char) decChar);
    }

    private String getString(int length) {
        StringBuffer chars = new StringBuffer();
        for (int i = 0; i < length; i++) {
            chars.append(getChar());
        }
        return chars.toString();
    }
}
