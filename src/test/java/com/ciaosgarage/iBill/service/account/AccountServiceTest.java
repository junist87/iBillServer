package com.ciaosgarage.iBill.service.account;


import com.ciaosgarage.iBill.beans.service.account.*;
import com.ciaosgarage.iBill.context.IBillContext;
import com.ciaosgarage.iBill.context.TestContext;
import com.ciaosgarage.iBill.domain.Account;
import com.ciaosgarage.iBill.testTools.ValueSampler;
import com.ciaosgarage.newDao.daoService.DaoService;
import com.ciaosgarage.newDao.defaultVo.SeqTable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {IBillContext.class, TestContext.class})
public class AccountServiceTest {

    @Autowired
    AccountService accountService;

    @Autowired
    DaoService daoService;

    ValueSampler sampler;

    @Before
    public void setUp() {
        // 어카운트의 모든정보 삭제하기
        daoService.getDao().deleteAll(Account.class);

        // 키값 모두 삭제하기
        daoService.getDao().deleteAll(SeqTable.class);

        sampler = new ValueSampler();
    }

    @Test
    public void joinAndGetAccount() {
        int testSize = 20;
        insertRandomAccount(testSize);
    }


    private List<Account> insertRandomAccount(int length) {
        List<Account> list = new ArrayList<Account>();

        for (int i = 0; i < length; i++) {
            // 회원가입
            String email = sampler.getEmail();
            String passwd = sampler.getStringAllowedDuplicated(10);
            String nickname = sampler.getStringAllowedDuplicated(10);


            System.out.println((i + 1) + ") email = " + email + ", passwd = " + passwd + ", nickname = " + nickname);
            accountService.join(email, passwd, nickname);

            // 불러오기
            Account getAccount = accountService.getAccount(email);
            assertThat(email, is(getAccount.email));
            assertThat(passwd, is(getAccount.passwd));
            assertThat(nickname, is(getAccount.nickname));

            list.add(getAccount);
        }
        return list;

    }

    @Test
    public void edit() {
        int testSize = 20;
        List<Account> list = insertRandomAccount(testSize);

        for (int i = 0; i < testSize; i++) {
            int randIndex = (int) (Math.random() * 20);


            // 데이터를 수정한다.
            Account account = list.get(randIndex);
            account.passwd = sampler.getStringAllowedDuplicated(10);
            account.nickname = sampler.getStringAllowedDuplicated(10);
            accountService.edit(account.email, account.passwd, account.nickname);

            // 데이터를 비교한다
            Account getAccount = accountService.getAccount(account.email);
            assertThat(account.passwd, is(getAccount.passwd));
            assertThat(account.nickname, is(getAccount.nickname));
        }
    }

    @Test
    public void unlock() {
        int testSize = 20;

        // 인증 받지 않은 계정들
        List<Account> list = insertRandomAccount(testSize);

        for (int i = 0; i < testSize; i++) {
            int randIndex = (int) (Math.random() * testSize);

            // 임의의 계정을 가저온다
            Account account = list.get(randIndex);

            int randCase = (int) (Math.random() * 4) + 1;

            // 코드표
            /*
                1 = 언락 완료
                2 = 이메일 없음
                3 = 비밀번호 틀림
                4 = 인증키 틀림
             */

            switch (randCase) {
                case 1: //언락완료
                    System.out.println("1 언락하기) email : " + account.email);
                    assertThat(certification(account.email, account.passwd, account.certKey), is(1));
                    // 다시 락킹 상태로 바꾸기 -> 랜덤으로 계정을 추출하므로 다시 원상 복구한다.
                    account.certKey = accountService.lockAccount(account.email);
                    break;
                case 2:
                    // 틀린 이메일
                    System.out.println("2 틀린 이메일) email : " + account.email);
                    assertThat(certification(account.email + "!@#!", account.passwd, account.certKey), is(2));
                    break;
                case 3:
                    // 틀린 비밀번호
                    System.out.println("3 틀린 비밀번호) email : " + account.email);
                    assertThat(certification(account.email, account.passwd + "#!@", account.certKey), is(3));
                    break;
                case 4:
                    // 틀린 인증키
                    System.out.println("4 틀린 언락키) email : " + account.email);
                    assertThat(certification(account.email, account.passwd, account.certKey + "@#@"), is(4));
                    break;
                default:
                    // 케이스 선택이 잘못됨
                    System.out.println("케이스 선택 실패) randCase = " + randCase);
                    fail();

            }
        }
    }

    private int certification(String email, String passwd, String certKey) {
        try {
            accountService.unlockAccount(email, passwd, certKey);
            return 1;
        } catch (EmptyResultDataAccessException e) {
            return 2;
        } catch (InvalidPasswordException e) {
            return 3;
        } catch (InvalidCertKeyException e) {
            return 4;
        }
    }

    @Test
    public void lock() {
        int testSize = 20;

        // 인증 받지 않은 계정들
        List<Account> list = insertRandomAccount(testSize);

        for (int i = 0; i < testSize; i++) {
            int randIndex = (int) (Math.random() * testSize);

            Account account = list.get(randIndex);
            System.out.println("test " + (i + 1) + ") email = " + account.email);

            // 언락하기 -> 예외가 발생하면 테스트 실패
            accountService.unlockAccount(account.email, account.passwd, account.certKey);

            // 언락확인하기 -> 예외 발생하면 테스트 실패
            accountService.login(account.email, account.passwd);

            // 락하기 -> 예외 발생하면 테스트 실패
            account.certKey = accountService.lockAccount(account.email);

            // 락킹 확인하기
            try {
                accountService.login(account.email, account.passwd);
            } catch (EmptyResultDataAccessException e) {
                fail();
            } catch (InvalidPasswordException e) {
                fail();
            } catch (LockedAccountException e) {
                // 락 어카운트 예외만 허용
                continue;
            }
        }
    }

    @Test
    public void login() {
        int testSize = 20;

        // 인증 받지 않은 계정들
        List<Account> list = insertRandomAccount(testSize);

        for (int i = 0; i < testSize; i++) {
            int randIndex = (int) (Math.random() * testSize);

            Account account = list.get(randIndex);

            // 코드표
            /*
                0 = 로그인 완료
                1 = 이메일없음
                2 = 비밀번호 틀림
                3 = 잠긴 계정
             */

            int randCase = (int) (Math.random() * 4);

            switch (randCase) {
                case 0:
                    System.out.println("test " + (i + 1) + " : login ) email = " + account.email);
                    // 언락하기
                    accountService.unlockAccount(account.email, account.passwd, account.certKey);
                    assertThat(loginTest(account.email, account.passwd), is(0));
                    // 테스트 완료후 다시 락킹 해두기
                    account.certKey = accountService.lockAccount(account.email);
                    break;
                case 1:
                    System.out.println("test " + (i + 1) + " : invalidEmail ) email = " + account.email);

                    // 언락하기
                    accountService.unlockAccount(account.email, account.passwd, account.certKey);

                    assertThat(loginTest(account.email + "2323", account.passwd), is(1));

                    // 테스트 완료후 다시 락킹 해두기
                    account.certKey = accountService.lockAccount(account.email);
                    break;
                case 2:
                    System.out.println("test " + (i + 1) + " : invalidPasswd ) email = " + account.email);

                    // 언락하기
                    accountService.unlockAccount(account.email, account.passwd, account.certKey);

                    assertThat(loginTest(account.email, account.passwd + "@#!"), is(2));

                    // 테스트 완료후 다시 락킹 해두기
                    account.certKey = accountService.lockAccount(account.email);
                    break;
                case 3:
                    System.out.println("test " + (i + 1) + " : lockedAccount ) email = " + account.email);
                    assertThat(loginTest(account.email, account.passwd), is(3));
                    break;
                default:
                    System.out.println("test " + (i + 1) + " : Wrong randCase ) email = " + account.email);
                    fail();
            }


        }
    }

    private int loginTest(String email, String passwd) {
        try {
            accountService.login(email, passwd);
            return 0;
        } catch (EmptyResultDataAccessException e) {
            return 1;
        } catch (InvalidPasswordException e) {
            return 2;
        } catch (LockedAccountException e) {
            return 3;
        }
    }
}
