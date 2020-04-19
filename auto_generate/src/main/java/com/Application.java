package com;

import com.demo.dao.NewTableDao;
import com.demo.entity.NewTableExample;
import io.swagger.annotations.Example;
import org.apache.ibatis.session.SqlSession;
import org.apache.rocketmq.common.message.Message;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

/**
 * @Classname Application
 * @Description
 * @Date 2020/3/30 22:54
 * @Created by lyf
 */
@SpringBootApplication(scanBasePackages = "com.demo")
@MapperScan(basePackages = "com.demo.entity.mapper")
public class Application {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(Application.class,args);
        SqlSession sqlSessionTemplate = (SqlSession) run.getBean("sqlSessionTemplate");
        NewTableDao newTableDao = run.getBean(NewTableDao.class);
        NewTableExample newTableExample = new NewTableExample();
        newTableExample.createCriteria().andIdEqualTo(1L);
        List list = newTableDao.selectByExample(newTableExample);
        System.out.println(list);
    }
}
