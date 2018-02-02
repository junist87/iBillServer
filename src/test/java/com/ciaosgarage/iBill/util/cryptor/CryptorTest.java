package com.ciaosgarage.iBill.util.cryptor;


import com.ciaosgarage.iBill.beans.util.cryptor.Cryptor;
import com.ciaosgarage.iBill.context.IBillContext;
import com.ciaosgarage.iBill.context.TestContext;
import com.ciaosgarage.iBill.testTools.ValueSampler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {IBillContext.class, TestContext.class})
public class CryptorTest {

    @Autowired
    Cryptor cryptor;


    ValueSampler sampler;

    @Before
    public void setUp() {
        sampler = new ValueSampler();
    }

    @Test
    public void test() {
        int testSize = 100;
        List<Unit> list = getRandomUnits(testSize);

        for (int i = 0; i < testSize; i++) {
            int randIndex =(int) (Math.random() * testSize);
            int randCase = (int) (Math.random() * 2);

            /*
                0 : 키 정확
                1 : 키 부정확
             */
            Unit targetUnit = list.get(randIndex);

            switch (randCase) {
                case 0:
                    // 올바른 키값 입력
                    cryptor.makeKey(targetUnit.key);
                    // 두 값이 틀리면 실패
                    assertThat(targetUnit.value, is(cryptor.decryption(targetUnit.encryptionValue)));
                    System.out.println("바른 키 value : " + targetUnit.value + ", decryptValue = " + cryptor.decryption(targetUnit.encryptionValue));
                    break;
                case 1:
                    // 잘못된 키값 입력
                    cryptor.makeKey(targetUnit.key.substring(0,13) + "!1s@#@");
                    // 두 값이 같으면 실패
                    if (targetUnit.value.equals(cryptor.decryption(targetUnit.encryptionValue))) fail();
                    System.out.println("잘못된 키 value : " + targetUnit.value + ", decryptValue = " + cryptor.decryption(targetUnit.encryptionValue));
                    break;
                default:
                    System.out.println("잘못된 randCase = " + randCase);
                    fail();

            }
        }


    }

    private List<Unit> getRandomUnits(int size) {
        List<Unit> list = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            Unit unit = new Unit();
            unit.key = sampler.getStringAllowedDuplicated(16);
            unit.value = sampler.getStringAllowedDuplicated(20);

            cryptor.makeKey(unit.key);
            unit.encryptionValue = cryptor.encryption(unit.value);

            list.add(unit);
        }
        return list;
    }

    class Unit {
        String key;
        String value;
        String encryptionValue;
    }
}
