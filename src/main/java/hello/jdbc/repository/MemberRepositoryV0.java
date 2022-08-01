package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.net.http.HttpConnectTimeoutException;
import java.sql.*;

/**
 *JDBC- DriverManager 사용
 */
@Slf4j
public class MemberRepositoryV0 {
    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values(?,?)";

        Connection con = null;
        PreparedStatement pstmt = null;


        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());//sql에 대한 파라미터 바인딩(첫 번째 ?)
            pstmt.setInt(2,member.getMoney());//파라미터 바인딩(두 번째 ?)
            pstmt.executeUpdate();//실행
            return member;

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
           }finally {
            close(con,pstmt,null); //리소스 정리
        }
    }
    private void close(Connection con, Statement stmt, ResultSet rs){
        /** stmt.close()에서 error나면 con.close()실행 안되니까 try-catch로 감싸서 호출*/
        if(rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }
        if(stmt != null){
            try {
                stmt.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }
        if(con != null){
            try {
                con.close();//외부리소스를 쓰는건데, 안 닫아주면 계속 유지되니까 닫아야함
            } catch (SQLException e) {
                log.info("error", e);
            }
        }
    }
    private Connection getConnection() { //함수로 빼두고 사용
        return DBConnectionUtil.getConnection();
    }
}
