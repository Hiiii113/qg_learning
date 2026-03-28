package hiiii113;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 */
@SpringBootApplication
@MapperScan("hiiii113.mapper")
public class DormitoryApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(DormitoryApplication.class, args);
    }
}
