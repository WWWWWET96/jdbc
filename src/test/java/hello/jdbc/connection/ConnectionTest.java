package hello.jdbc.connection;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class ConnectionTest {

    @Test
    void driverManager() throws SQLException {
        Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        log.info("coonection={}, cloass={}", con1, con1.getClass());
        log.info("coonection={}, cloass={}", con2, con2.getClass());
    }

    @Test
    void dataSourceDriverManager() throws SQLException {
    //DriverManagerDataSource: DriverManager 사용하기 때문에 항상 새로운 커넥션을 획득함
        DriverManagerDataSource datasource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        useDataSource(datasource);
    }

    private void useDataSource(DataSource datasource) throws SQLException {
        Connection con1 = datasource.getConnection();
        Connection con2 = datasource.getConnection();
        log.info("coonection={}, cloass={}", con1, con1.getClass());
        log.info("coonection={}, cloass={}", con2, con2.getClass());
    }

    @Test
    void dataSourceConnectionPool() throws SQLException, InterruptedException {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("MyPool");

        useDataSource(dataSource);
        Thread.sleep(1000);
        /*커넥션 풀에서 커넥션 생성 시간 대기
        * 애플리케이션 실행할 때 커넥션 풀 채울때가지 대기하면 애플리케이션 실행시간 너무 늦어짐
        * 그렇기때문에 별도의 쓰레드 사용해서 커넥션 풀을 채워야 함함        * */
    }
}
