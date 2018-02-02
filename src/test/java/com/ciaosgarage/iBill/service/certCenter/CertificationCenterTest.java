package com.ciaosgarage.iBill.service.certCenter;

import com.ciaosgarage.iBill.beans.service.account.AccountService;
import com.ciaosgarage.iBill.beans.service.account.InvalidPasswordException;
import com.ciaosgarage.iBill.beans.service.certifiationCenter.CertificationCenter;
import com.ciaosgarage.iBill.beans.service.certifiationCenter.ExpiredCertKeyException;
import com.ciaosgarage.iBill.beans.service.certifiationCenter.InvalidCertificationKeyException;
import com.ciaosgarage.iBill.context.IBillContext;
import com.ciaosgarage.iBill.context.TestContext;
import com.ciaosgarage.iBill.domain.Account;
import com.ciaosgarage.iBill.domain.CertCenter;
import com.ciaosgarage.iBill.testTools.ValueSampler;
import com.ciaosgarage.iBill.testTools.VoSampler;
import com.ciaosgarage.newDao.daoService.DaoService;
import com.ciaosgarage.newDao.defaultVo.SeqTable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {IBillContext.class, TestContext.class})
public class CertificationCenterTest {

    @Autowired
    AccountService accountService;

    @Autowired
    CertificationCenter certificationCenter;

    @Autowired
    DaoService daoService;

    ValueSampler sampler;
    VoSampler voSampler;

    @Before
    public void setUp() {
        // 어카운트의 모든정보 삭제하기
        daoService.getDao().deleteAll(Account.class);
        daoService.getDao().deleteAll(CertCenter.class);
        daoService.getDao().deleteAll(SeqTable.class);

        sampler = new ValueSampler();
        voSampler = new VoSampler();
    }

    @Test
    public void getKey() {
        int testSize = 20;
        List<Account> accountList = voSampler.insertRandomAccount(testSize, accountService);

        for (int i = 0; i < testSize; i++) {
            int randIndex = (int) (Math.random() * testSize);
            int randCase = (int) (Math.random() * 3);

            /*
                발생되는 상황 케이스 번호
                0 : 정상발급
                1 : 등록된 이메일 없음
                2 : 비밀번호 틀림
             */

            Account account = accountList.get(randIndex);


            switch (randCase) {
                case 0:
                    // 키 정상발급
                    String certKey = getKey(account.email, account.passwd, 0);
                    // 다음 테스트를 위해서 키 사용
                    certificationCenter.removeKey(account.email, certKey);
                    break;
                case 1:
                    // 잘못된 이메일
                    getKey(account.email + "DFA", account.passwd, 1);
                    break;

                case 2:
                    // 비밀번호 오류
                    getKey(account.email, account.passwd + "AD", 2);
                    break;
                default:
                    System.out.println("잘못된 randCase = " + randCase);
                    fail();
            }
        }
    }

    private String getKey(String email, String passwd, int expected) {
        String certKey = null;
        Integer result;
        System.out.print("test ) Email = " + email + ", password = " + passwd + ", expected = " + expected);
        try {
            // 정상 발급
            certKey = certificationCenter.getKey(email, passwd);
            result = 0;
        } catch (InvalidPasswordException e) {
            // 틀린 비밀번호
            result = 2;
        } catch (EmptyResultDataAccessException e) {
            // 잘못된 이메일
            result = 1;
        }

        System.out.println(", result = " + result);
        assertThat(expected, is(result));
        return certKey;
    }


    @Test
    public void keyTest() {
        int testSize = 30;
        List<Account> accountList = voSampler.insertRandomAccount(testSize, accountService);

        for (int i = 0; i < testSize; i++) {
            int randIndex = (int) (Math.random() * testSize);
            int randCase = (int) (Math.random() * 5);

            /*
                발생되는 상황 케이스 번호
                0 : 정상처리
                1 : 잘못된 키
                2 : 키 없음
                3 : 잘못된 이메일
                4 : 시간초과
             */

            /*
                result Expected Value
                0) 정상처리
                1) 이메일, 인증키, 사용된키 오류
                2) 중복된 키 -> 발생하지 않는다
                3) 사용시간이 만료된 키 오류
             */

            Account account = accountList.get(randIndex);
            String certKey = certificationCenter.getKey(account.email, account.passwd);
            System.out.print("test ) email = " + account.email + ", randCase = " + randCase+ ", ");
            switch (randCase) {
                case 0:
                    // 정상처리하기
                    System.out.println("정상처리");
                    testRemoveKey(account.email, certKey, 0);
                    break;
                case 1:
                    // 잘못된키
                    System.out.println("잘못된 키");
                    testRemoveKey(account.email, certKey + "@#1", 1);
                    break;
                case 2:
                    System.out.println("이메일 없음");
                    // 이메일 없음
                    testRemoveKey(account.email + "A341D", certKey, 1);

                    // 테스트 완료하면 키 삭제하기
                    testRemoveKey(account.email, certKey, 0);
                    break;
                case 3:
                    // 사용된 키
                    // 키 사용
                    System.out.println("키 중복 사용");
                    testRemoveKey(account.email, certKey, 0);

                    // 키 중복사용
                    testRemoveKey(account.email, certKey, 1);
                    break;
                case 4:
                    System.out.println("키 시간초과");
                    // 시간초과
                    try {
                        Thread.sleep(6000); // 시스템 시간을 잠깐 멈춘다
                        testRemoveKey(account.email, certKey, 3);
                    } catch (InterruptedException e) {
                        fail();
                    }
                    break;
                default:
                    System.out.println("잘못된 randCase = " + randCase);


            }

        }
    }

    private void testRemoveKey(String email, String certKey, int expected) {
        Integer result = 0;

        try {
            certificationCenter.removeKey(email, certKey);
        } catch (EmptyResultDataAccessException e) {
            result = 1;
        } catch (InvalidCertificationKeyException e) {
            result = 2;
        } catch (ExpiredCertKeyException e) {
            result = 3;
        }

        assertThat(expected, is(result));
    }

}
