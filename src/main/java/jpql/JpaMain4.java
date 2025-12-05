package jpql;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain4 {
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
            member.setUsername("member");
            member.setAge(10);

            member.setTeam(team);

            em.persist(member);

            em.flush(); // DB에 반영
            em.clear(); // 영속성 컨텍스트 비움

            // inner join
            //String query = "select m from Member m join m.team t";

            // left outer join
            //String query = "select m from Member m left join m.team t";
            //String query = "select m from Member m left join m.team on t.name ='A'";

            // 연관관계가 없는 엔티티 외부 조인 (inner, left 둘 다 가능)
            // String query = "select m from Member m left join Team t on m.username = t.name";

            // 세타 조인
            String query = "select m from Member m, Team t where m.username = t.name";
            List<Member> result = em.createQuery(query, Member.class)
                    .getResultList();

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
