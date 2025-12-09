package jpql;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.Collection;
import java.util.List;

public class JpaMain8 {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team teamA = new Team();
            teamA.setName("팀A");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("팀B");
            em.persist(teamB);

            Member member1 = new Member();
            member1.setUsername("회원1");
            member1.setTeam(teamA);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.setTeam(teamA);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.setTeam(teamB);
            em.persist(member3);

            em.flush();
            em.clear();

            String query = "select m From Member m";
            List<Member> result = em.createQuery(query, Member.class).getResultList();

            for (Member member : result) {
                System.out.println("member = " + member.getUsername() + ", " + member.getTeam().getName());
                // 회원1, 팀A(SQL)
                // 회원2, 팀A(1차캐시)
                // 회원3, 팀B(SQL)
                // 팀이 다 다르면 100명이면 member 조회 1 + 팀 조인 조회 100번 함 => N+1문제
            }

            // N+1문제를 해결하려면?
            // fetch 조인으로 회원과 팀을 함께 조회해서 지연로딩 X
            String query2 = "select m From Member m join fetch m.team";
            List<Member> result2 = em.createQuery(query2, Member.class).getResultList();

            for (Member member : result2) {
                System.out.println("member = " + member.getUsername() + ", " + member.getTeam().getName());
            }

            // 컬렉션 fetch join
            // 일대다 연관관계에서는 DB에는 쿼리를 날리면 팀A에 두명이라 결과가 증가함(중복 결과) 다대일은 괜찮음.
            // 해결 : Distinct로 중복 제거
            // JPQL의 distinct는 sql에 distinct를 추가 + 애플리케이션에서 엔티티 중복 제거의 기능
            String query3 = "select distinct t From Team t join fetch t.members";
            List<Team> result3 = em.createQuery(query3, Team.class).getResultList();

            for (Team team : result3) {
                System.out.println("team = " + team.getName() +" | members = " +team.getMembers().size());
                for (Member member : team.getMembers()) {
                    System.out.println("-> member = " + member);
                }
            }

            // JPQL은 결과를 반환할 때 연관관계를 고려하지 않는다.
            // SELECT 절에 지정한 엔티티만 조회할 뿐이다.
            // 일반 조인의 경우 팀 엔티티만 조회하고, 회원 엔티티는 조회하지 않는다.

            // 페치 조인을 사용할 때만 연관된 엔티티도 함께 조회한다.
            // (즉시 로딩)페치 조인은 객체 그래프를 SQL 한 번에 조회하는 개념이다.

            // 페치 조인 대상에는 별칭을 줄 수 없다.
            // 둘 이상의 컬렉션은 페치 조인 할 수 없다.
            // 컬렉션을 페치 조인하면 페이징API를 사용할 수 없다. (일대일, 다대일 같은 단일 값 연관 필드들은 페이징가능)
            // 하이버네이트는 경고 로그를 남기고 메모리에서 페이징(매우 위험)
            // BatchSize를 사용
            // <property name="hibernate.default_batch_fetch_size" value="100" />

            // 모든 것을 페치 조인으로 해결할 수는 없다.
            // 페치 조인은 객체 그래프를 유지할 때 사용하면 효과적이다.
            // 여러 테이블을 조인하여 엔티티가 가진 모양이 아닌 전혀 다른 결과를 내야 한다면 일반 조인을 사용한다.
            // 필요한 데이터들만 조회하여 DTO로 반환하는 것이 효과적

            // Named 쿼리
            em.createNamedQuery("Member.findByUsername", Member.class)
                    .setParameter("username", "회원1")
                    .getResultList();

            // 벌크 연산
            // FLUSH 자동 호출
            int resultCount = em.createQuery("update Member m set m.age = 20")
                    .executeUpdate();

            em.clear();
            System.out.println("resultCount = " + resultCount);

            Member findMember = em.find(Member.class, member1.getId());
            System.out.println("findMember = " + findMember);

            // 벌크 연산은 영속성 컨텍스트를 무시하고 DB에 직접 쿼리
            // 벌크 연산을 먼저 실행
            // 벌크 연산 수행 후 영속성 컨텍스트 초기화

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }
}
