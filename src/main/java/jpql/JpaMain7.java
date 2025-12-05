package jpql;

import javax.persistence.*;
import java.util.List;

public class JpaMain7 {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("관리자");
            member.setAge(10);
            member.setType(MemberType.ADMIN);
            member.setTeam(team);

            Member member2 = new Member();
            member.setUsername("관리자2");
            member.setAge(10);
            member.setType(MemberType.ADMIN);
            member.setTeam(team);

            em.persist(member);
            em.persist(member2);

            em.flush(); // DB에 반영
            em.clear(); // 영속성 컨텍스트 비움

            // 문자열 합치기 concat('a','b') 이나 ||
            String query = "select 'a' || 'b' from Member m";

            // 문자열 자르기 substring
            String query2 = "select substring(m.username, 2, 3) from Member m";

            // 문자열 인덱스 반환
            String query3 = "select locate('de','abcdegf') from Member m"; // 4

            // 컬렉션의 size
            String query4 = "select size(t.members) from Team t";

            // 컬렉션이 @OrderColumn을 사용하는 LIST 타입일 때만 쓰는
            // LIST 타입 컬렉션의 위치 값 (잘 안씀)
            String query5 = "select index(t.members) from Team t";

            // 사용자 정의 함수를 등록하고 방언에 추가한 후 사용가능
            String query6 = "select function('group_concat', m.username) from Member m ";

            List<String> result = em.createQuery(query6, String.class).getResultList();
            for (String s : result) {
                System.out.println("s = " + s);
            }

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
