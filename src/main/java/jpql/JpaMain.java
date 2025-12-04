package jpql;

import javax.persistence.*;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);

            // 반환 타입이 명확할 때 TypeQuery
            // TypedQuery<Member> query = em.createQuery("select m from Member m where m.username = :username", Member.class);
            Member result = em.createQuery("select m from Member m where m.username = :username", Member.class).setParameter("username", "member1").getSingleResult();
            System.out.println("result = " + result.getUsername());

            // 결과가 여러개일 때
            // 결과가 없으면 빈 리스트 반환
//            for (Member member1 : members) {
//                System.out.println("member1 = " + member1);
//            }

            // 결과가 하나일 때
            // 결과가 정확히 하나여야 함.
            // 결과가 없으면 NoResultException, 결과가 둘 이상이면 NoUniqueResultException
            // Member result = query.getSingleResult();
            // String Data JPA -> 결과가 없으면 null반환 하거나 optional 로 반환함.
            // System.out.println("result = " + result);

            TypedQuery<String> query2 = em.createQuery("select m.username from Member m", String.class);

            // 반환 타입이 명확하지 않을 때 Query
            Query query3 = em.createQuery("select m.username, m.age from Member m");

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
