package jpql;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain6 {
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

            em.persist(member);

            em.flush(); // DB에 반영
            em.clear(); // 영속성 컨텍스트 비움

            // case문
            String query = "select " +
                                    "case when m.age <= 10 then '학생요금'" +
                                    "     when m.age >= 60 then '경로요금'"+
                                    "      else '일반요금' " +
                                     "end " +
                            "from Member m";

            List<String> result = em.createQuery(query, String.class).getResultList();
            for (String s : result) {
                System.out.println("s = " + s);
            }

            // COALESCE : 하나씩 조회해서 null이 아니면 반환
            String query2 = "select coalesce(m.username, '이름 없는 회원') from Member m ";

            List<String> result2 = em.createQuery(query2, String.class).getResultList();
            for (String s2 : result2) {
                System.out.println("s2 = " + s2);
            }

            // NULLIF : 두 값이 같으면 NULL 반환, 다르면 첫 번째 값 반환
            String query3 = "select nullif(m.username, '관리자') from Member m";

            List<String> result3 = em.createQuery(query3, String.class).getResultList();
            for (String s3 : result3) {
                System.out.println("s3 = " + s3);
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
