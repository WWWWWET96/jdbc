package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/*
* 트랜잭션 - 파라미터 연동 ,풀을 고려한 종료
* */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {
    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;
    public void accountTransfer(String fromId, String toId, int money) throws
            SQLException {
        Connection con = dataSource.getConnection();
        try {
            con.setAutoCommit(false); //자동커밋모드 off
            //트랜잭션 시작하려면 자동커밋모드를 꺼야함

//비즈니스 로직
            bizLogic(con, fromId, toId, money);
            con.commit(); //성공시 커밋
        } catch (Exception e) {
            con.rollback(); //실패시 롤백
            throw new IllegalStateException(e);
        } finally {
            release(con);
        }
    }
    private void bizLogic(Connection con, String fromId, String toId, int
            money) throws SQLException {
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);
        memberRepository.update(con, fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(con, toId, toMember.getMoney() + money);
    }
    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
    private void release(Connection con) {
        if (con != null) {
            try {
                con.setAutoCommit(true);
                /*커넥션 풀 고려해서 다시 자동모드로 변경 후 반환
                기본적으로 자동커밋모드이기 때문에 트랜잭션 시작할때, 즉 커넥션 사용할때는
                수동커밋모드로 진행하고, 모든게 끝나고 커넥션 반환할 때는(커넥션 풀이니까
                 커넥션 종료가 아닌 반환임)기본 셋팅인 자동커밋모드로 변환 후 커넥션 닫기기                *
                * */
                con.close();
            } catch (Exception e) {
                log.info("error", e);
            }
        }
    }
}