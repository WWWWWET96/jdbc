package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 *JDBC-ConnectioinParam
 */
@Slf4j
public class MemberRepositoryV2 {

    private final DataSource dataSource;

    public MemberRepositoryV2(DataSource dataSource) {
        this.dataSource = dataSource;
    }


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

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();//select문 실행
            if(rs.next()){ //selectQuery결과 담고있는 rs에서 값 꺼내기
                //next()통해 실제 데이터 있는 곳부터 호출되도록
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            }else{//select결과가 없으면
                throw new NoSuchElementException("member not found memberId="+memberId);
            }

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }finally { //사용한 순 역순으로 해제
            close(con, pstmt, rs);
        }
    }
    public Member findById(Connection con,String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();//select문 실행
            if(rs.next()){ //selectQuery결과 담고있는 rs에서 값 꺼내기
                //next()통해 실제 데이터 있는 곳부터 호출되도록
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            }else{//select결과가 없으면
                throw new NoSuchElementException("member not found memberId="+memberId);
            }

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }finally { //사용한 순 역순으로 해제
            //주의 connection여기서 닫으면 안됨!
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
        }
    }
    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);//sql에 대한 파라미터 바인딩(첫 번째 ?)
            pstmt.setString(2,memberId);//파라미터 바인딩(두 번째 ?)
            pstmt.executeUpdate();//실행
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }finally {
            close(con,pstmt,null); //리소스 정리
        }
    }
    public void update(Connection con,String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        PreparedStatement pstmt = null;

        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);//sql에 대한 파라미터 바인딩(첫 번째 ?)
            pstmt.setString(2,memberId);//파라미터 바인딩(두 번째 ?)
            pstmt.executeUpdate();//실행
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }finally {
            //주의 connection여기서 닫으면 안됨!
            JdbcUtils.closeStatement(pstmt);
        }
    }
    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);//sql에 대한 파라미터 바인딩(첫 번째 ?)
            pstmt.executeUpdate();//실행
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }finally {
            close(con,pstmt,null); //리소스 정리
        }

    }
    private void close(Connection con, Statement stmt, ResultSet rs){
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con);
    }
    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("get connection={}, class={}",con, con.getClass());
        return con;
    }
}
