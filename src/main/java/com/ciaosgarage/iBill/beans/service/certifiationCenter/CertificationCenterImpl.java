package com.ciaosgarage.iBill.beans.service.certifiationCenter;

import com.ciaosgarage.iBill.beans.service.account.AccountService;
import com.ciaosgarage.iBill.beans.service.account.InvalidPasswordException;
import com.ciaosgarage.iBill.beans.service.account.LockedAccountException;
import com.ciaosgarage.iBill.domain.CertCenter;
import com.ciaosgarage.newDao.daoService.DaoService;
import com.ciaosgarage.newDao.sqlVo.requsetHandler.MySqlRequestHandler;
import com.ciaosgarage.newDao.sqlVo.requsetHandler.RequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

@Service
public class CertificationCenterImpl implements CertificationCenter {

    private static final int KeySize = 16;
    private static final int UsedKey = 1;
    private static final int NotUsedKey = 0;
    private static final int ExpireSeconds = 5;

    @Autowired
    AccountService accountService;

    @Autowired
    DaoService daoService;

    @Override
    public String getKey(String email, String passwd) throws InvalidPasswordException, EmptyResultDataAccessException {
        try {
            accountService.login(email, passwd);
        } catch (EmptyResultDataAccessException | InvalidPasswordException e) {
            throw e;
        } catch (LockedAccountException e) {
            // 예외를 발생하지 않고 키를 발급한다
        }

        // 기존 발급된 모든 키값을 정리한다.
        changeAllKeyUsed(email);

        // 새로운 키값을 받아 저장한다.
        String certKey = getString(KeySize);
        saveKey(email, certKey);

        // 발급된 키값 리턴
        return certKey;
    }

    private void saveKey(String email, String certKey) {
        CertCenter certCenter = new CertCenter();
        certCenter.email = email;
        certCenter.certKey = certKey;
        certCenter.usedKey = NotUsedKey;

        // 데이터 저장
        daoService.pushVo(certCenter);
    }

    private void changeAllKeyUsed(String email) {
        // 등록된 이메일로 발급된 모든 key 값을 사용된 값으로 변환한다.
        RequestHandler requestHandler = new MySqlRequestHandler(CertCenter.class);
        requestHandler.search("email", email);

        try {
            // 모든 데이터 불러와서 사용된 키로 전환
            List<CertCenter> list = daoService.pullVoList(requestHandler);
            for (CertCenter certCenter : list) {
                certCenter.usedKey = UsedKey;
                daoService.pushVo(certCenter);
            }

        } catch (EmptyResultDataAccessException e) {
            // 정보가 없어도 예외 처리 하지 않는다.
        }
    }

    @Override
    public void removeKey(String email, String certKey) throws EmptyResultDataAccessException, InvalidCertificationKeyException, ExpiredCertKeyException {
        // 키값이 등록되어 있는지 확인
        RequestHandler requestHandler = new MySqlRequestHandler(CertCenter.class);
        requestHandler.search("email", email);
        requestHandler.search("certKey", certKey);
        requestHandler.search("usedKey", NotUsedKey);

        try {
            // 값이 없다면 EmptyResultDataAccessException 발생
            List<CertCenter> list = daoService.pullVoList(requestHandler);

            // 발급된 키값이 1개 이상이면 잘못된 상황이므로 예외 발생
            if (list.size() != 1) throw new InvalidCertificationKeyException();

            // 기간만료된 키라면 ExpiredCertKeyException 발생
            this.compareTime(list.get(0));

            // 모든 예외가 발생하지 않으면 모든 키를 삭제하고 종료
            changeAllKeyUsed(email);
            return;
        } catch (EmptyResultDataAccessException | InvalidCertificationKeyException | ExpiredCertKeyException e) {
            // 모듬 예외는 throw 하고 모든 키 정보를 삭제한다.
            changeAllKeyUsed(email);
            throw e;
        }
    }

    // 시간확인
    private void compareTime(CertCenter certCenter) throws ExpiredCertKeyException {
        // 현재 시간(서버기준)을 가저온다
        Calendar cal = Calendar.getInstance();
        Timestamp now = new Timestamp(cal.getTime().getTime());

        // 인증키 기간 만료확인
        long timeShift = now.getTime() - certCenter.createDate.getTime();
        if (timeShift > (ExpireSeconds * 1000)) throw new ExpiredCertKeyException();
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
