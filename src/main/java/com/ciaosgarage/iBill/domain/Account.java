package com.ciaosgarage.iBill.domain;

import com.ciaosgarage.newDao.vo.CryptOption;
import com.ciaosgarage.newDao.vo.DbColumn;
import com.ciaosgarage.newDao.vo.RwType;
import com.ciaosgarage.newDao.vo.Vo;

import java.sql.Timestamp;

public class Account extends Vo{
    @DbColumn(cryptOption = CryptOption.ON)
    public String email;
    @DbColumn(cryptOption = CryptOption.ON)
    public String passwd;
    @DbColumn(rwType = RwType.READONLY)
    public Timestamp createDate;
    @DbColumn(cryptOption = CryptOption.ON)
    public String nickname;
    @DbColumn(cryptOption = CryptOption.ON)
    public String certKey;
    @DbColumn
    public Integer cert;

}
