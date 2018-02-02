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
     * @throws InvalidPasswordException       패스워드 불일치 하였을 때 발생하는 예외
     * @throws EmptyResultDataAccessException 어카운트 정보에 이메일 정보가 없으면 발생하는 예외
     */
    String getKey(String email, String passwd) throws InvalidPasswordException, EmptyResultDataAccessException;

    /**
     * 발급된 키를 삭제한다
     *
     * @param email   유저 이메일
     * @param certKey 삭제할 키값
     * @throws EmptyResultDataAccessException   이메일 오류 또는 키값 오류 또는 사용된 키값이라면 발생하는 예외
     * @throws InvalidCertificationKeyException 키 값이 올바르지 않으면 발생하는 예외
     * @throws ExpiredCertKeyException          기한이 만료된 키라면 발생하는 예외
     */
    void removeKey(String email, String certKey) throws EmptyResultDataAccessException, InvalidCertificationKeyException, ExpiredCertKeyException;

}
