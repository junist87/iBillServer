package com.ciaosgarage.iBill.beans.service.account;

import com.ciaosgarage.iBill.domain.Account;
import org.springframework.dao.EmptyResultDataAccessException;

public interface AccountService {

    /**
     * 새로운 계정을 만든다.
     *
     * @param email    이메일
     * @param password 비밀번호
     * @param nickname 닉네임
     * @throws DuplicatedEmailException 중복된 메일주소가 있을때 발생하는 예외
     * @throws CannotSendEmailException the cannot send email exception
     */
    void join(String email, String password, String nickname) throws DuplicatedEmailException, CannotSendEmailException;

    /**
     * 어카운트 정보를 가져온다
     *
     * @param email 가저올 데이터의  이메일 주소
     * @return 가저온 어카운트 정보
     * @throws EmptyResultDataAccessException 가저올 데이터가 없을때 발생하는 예외
     */
    Account getAccount(String email) throws EmptyResultDataAccessException;

    /**
     * 로그인 판단하는 메소드
     * LOGIN : 로그인 가능
     * WORNGPASSWORD : 비밀번호 틀림
     * LOCK : 잠긴 어카운트
     * INVALIDACCOUNT : 잘못된 이메일
     *
     * @param email    이메일 주소
     * @param password 비밀번호
     */
    void login(String email, String password) throws EmptyResultDataAccessException, InvalidPasswordException, LockedAccountException;

    /**
     * 어카운트 정보를 수정한다
     *
     * @param email       타깃 이메일 주소
     * @param newPass 새로운 비밀번호
     * @param nickname    닉네임
     * @throws EmptyResultDataAccessException 타깃 이메일이 없을때 발생하는 예외
     * @throws InvalidPasswordException       이전 비밀번호가 틀렸을때 발생하는 예외
     */
    void edit(String email, String newPass, String nickname) throws EmptyResultDataAccessException, InvalidPasswordException;

    /**
     * 어카운트 잠김을 해제한다
     *
     * @param email     타깃 이메일 주소
     * @param password  비밀번호
     * @param unlockKey 언락 키
     * @throws EmptyResultDataAccessException   타깃 이메일이 존재하지 않을때 발생하는 예외
     * @throws InvalidPasswordException         비밀번호가 틀렸을때 발생하는 예외
     * @throws InvalidCertKeyException 언락키가 틀렸을때 발생하는 예외
     */
    void unlockAccount(String email, String password, String unlockKey) throws EmptyResultDataAccessException, InvalidPasswordException, InvalidCertKeyException;

    /**
     * 계정을 잠근다
     *
     * @param email 타깃 이메일 주소
     * @return 언락 키
     * @throws EmptyResultDataAccessException 타깃 이메일이 존재하지 않을때 발생하는 예외
     * @throws CannotSendEmailException       the cannot send email exception
     */
    String lockAccount(String email) throws EmptyResultDataAccessException, CannotSendEmailException;

    /**
     * 중복된 이메일이 있는지 알려주는 메소드
     *
     * @param email the email
     * @return the boolean
     */
    boolean isDuplicatedEmail(String email);

}
