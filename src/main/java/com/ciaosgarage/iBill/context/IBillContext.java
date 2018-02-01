package com.ciaosgarage.iBill.context;

import com.ciaosgarage.newDao.NewDao;
import com.ciaosgarage.newDao.daoService.DaoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

@Configuration
public class IBillContext {

    @Bean
    public DaoService daoService() {
        NewDao newDao = new NewDao(dataSource());
        return newDao.getDaoService();
    }

    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
        dataSource.setUrl("jdbc:mysql://junist.synology.me:3307/iBill" + "?characterEncoding=UTF-8");
        dataSource.setUsername("ciaolee87");
        dataSource.setPassword("june2002");
        return dataSource;
    }
}
