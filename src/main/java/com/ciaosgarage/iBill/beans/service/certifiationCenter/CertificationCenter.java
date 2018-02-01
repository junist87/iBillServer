package com.ciaosgarage.iBill.beans.service.certifiationCenter;

import com.ciaosgarage.iBill.beans.service.account.InvalidPasswordException;
import com.ciaosgarage.iBill.beans.service.account.LockedAccountException;
import org.springframework.dao.EmptyResultDataAccessException;

/**
 * 인증센터
 * 서버와 클라이언트 연결시 인증 토큰을 생성하는 클래스
 */
public interface CertificationCenter {
    /**
     * Gets key.
     *
     * @param email  토큰을 요청하는 계정 이메일
     * @param passwd 토큰을 요청하는 계정 패스워드
     * @return 발급된 토큰
     * @throws InvalidPasswordException       패스워드 불일치
     * @throws EmptyResultDataAccessException 없는 이메일
     */
    String getKey(String email, String passwd) throws InvalidPasswordException, EmptyResultDataAccessException;

    /**
     * 발급된 키를 삭제한다
     *
     * @param key 삭제할 키값
     */
    void removeKey(String key);

}
