package com.ciaosgarage.iBill.beans.service.account;

import com.ciaosgarage.iBill.domain.Account;
import com.ciaosgarage.newDao.daoService.DaoService;
import com.ciaosgarage.newDao.sqlVo.requsetHandler.MySqlRequestHandler;
import com.ciaosgarage.newDao.sqlVo.requsetHandler.RequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {
    final static Integer CertKeyLength = 6;
    final static Integer CertLocked = 0;
    final static Integer CertUnlocked = 1;


    @Autowired
    DaoService daoService;

    public void join(String email, String password, String nickname) throws DuplicatedEmailException {
        // 중복된 이메일이면 예외 발생
        if (isDuplicatedEmail(email)) throw new DuplicatedEmailException("Email : " + email);

        Account account = new Account();
        account.email = email;
        account.passwd = password;
        account.nickname = nickname;
        account.cert = 0;   // 0: 인증안됨, 1: 인증됨
        account.certKey = getCertCode(CertKeyLength);

        daoService.pushVo(account);
    }

    public Account getAccount(String email) throws EmptyResultDataAccessException {
        RequestHandler requestHandler = new MySqlRequestHandler(Account.class);
        requestHandler.search("email", email);
        requestHandler.numberOf(1);

        // 불러오지 못하면 EmptyResultDataAccessException 발생
        return (Account) daoService.pullVo(requestHandler);

    }

    public void login(String email, String password) throws EmptyResultDataAccessException, InvalidPasswordException, LockedAccountException {
        // 불러오지 못하면 EmptyResultDataAccessException 발생
        Account account = getAccount(email);

        // 암호가 틀리다면
        if (!account.passwd.equals(password)) throw new InvalidPasswordException();

        // 계정이 잠겨있다면
        if (account.cert == CertLocked) throw new LockedAccountException();
    }

    public void edit(String email, String newPass, String nickname) throws EmptyResultDataAccessException, InvalidPasswordException {
        // 불러오지 못하면 EmptyResultDataAccessException 발생
        Account account = getAccount(email);

        // 변경 데이터 저장
        account.passwd = newPass;
        account.nickname = nickname;

        // 데이터베이스에 입력(수정)
        daoService.pushVo(account);
    }

    public void unlockAccount(String email, String password, String unlockKey) throws EmptyResultDataAccessException, InvalidPasswordException, InvalidCertKeyException {
        // 불러오지 못하면 EmptyResultDataAccessException 발생
        Account account = getAccount(email);

        // 암호가 틀리다면
        if (!account.passwd.equals(password)) throw new InvalidPasswordException();

        // 인증키 비교
        if (!account.certKey.equals(unlockKey)) throw new InvalidCertKeyException();


        // 인증완료 되었으므로 값을 저장한다
        account.cert = CertUnlocked;
        daoService.pushVo(account);
    }

    public String lockAccount(String email) throws EmptyResultDataAccessException {
        // 불러오지 못하면 EmptyResultDataAccessException 발생
        Account account = getAccount(email);

        account.cert = CertLocked;
        account.certKey = getCertCode(CertKeyLength);

        daoService.pushVo(account);
        return account.certKey;
    }

    public boolean isDuplicatedEmail(String email) {

        try {
            // 불러오지 못하면 EmptyResultDataAccessException 발생
            getAccount(email);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }


    private String getNumber() {
        //  0 부터 9 사이의 값이 나온다
        int decChar = (int) (Math.random() * 10) + 48;
        return String.valueOf((char) decChar);
    }

    private String getCertCode(int length) {
        StringBuffer chars = new StringBuffer();
        for (int i = 0; i < length; i++) {
            chars.append(getNumber());
        }
        return chars.toString();
    }
}
