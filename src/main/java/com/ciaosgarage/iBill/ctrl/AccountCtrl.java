package com.ciaosgarage.iBill.ctrl;

import com.ciaosgarage.iBill.beans.service.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountCtrl {

    @Autowired
    AccountService accountService;

//    @RequestMapping(value = "/login", method = RequestMethod.POST)
//    public RPAccount login(@RequestBody RQAccount account) {
//
//
//    }
}
