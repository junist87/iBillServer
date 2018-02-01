package com.ciaosgarage.iBill.beans.service.certifiationCenter;

import com.ciaosgarage.iBill.beans.service.account.AccountService;
import com.ciaosgarage.iBill.beans.service.account.InvalidPasswordException;
import com.ciaosgarage.iBill.beans.service.account.LockedAccountException;
import com.ciaosgarage.newDao.daoService.DaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class CertificationCenterImpl implements CertificationCenter{

    private final static int KeySize = 16;

    @Autowired
    AccountService accountService;

    @Autowired
    DaoService daoService;

    @Override
    public String getKey(String email, String passwd) throws InvalidPasswordException, EmptyResultDataAccessException {
        try {
            accountService.login(email, passwd);
        } catch (EmptyResultDataAccessException e) {
            throw e;
        } catch (InvalidPasswordException e) {
            throw e;
        } catch (LockedAccountException e) {
        }


        return null;
    }

    @Override
    public void removeKey(String key) {

    }


    private String getChar() {
        int decChar = (int) (Math.random() * 94) + 33;
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
