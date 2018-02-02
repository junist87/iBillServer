package com.ciaosgarage.iBill.testTools;

import com.ciaosgarage.iBill.beans.service.account.AccountService;
import com.ciaosgarage.iBill.domain.Account;

import java.util.ArrayList;
import java.util.List;

public class VoSampler {
    public List<Account> insertRandomAccount(int length, AccountService accountService) {
        List<Account> list = new ArrayList<Account>();
        ValueSampler sampler = new ValueSampler();
        for (int i = 0; i < length; i++) {
            // 회원가입
            String email = sampler.getEmail();
            String passwd = sampler.getStringAllowedDuplicated(10);
            String nickname = sampler.getStringAllowedDuplicated(10);

            System.out.println((i + 1) + ") email = " + email + ", passwd = " + passwd + ", nickname = " + nickname);
            accountService.join(email, passwd, nickname);

            // 불러오기
            Account getAccount = accountService.getAccount(email);
            list.add(getAccount);
        }
        return list;
    }
}
