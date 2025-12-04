package jpql;

import javax.persistence.*;
import java.util.List;

public class JpaMain2 {
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

            em.flush(); // DB에 반영
            em.clear(); // 영속성 컨텍스트 비움

            List<Member> result = em.createQuery("select m from Member m", Member.class)
                    .getResultList();

            // 특정 엔티티로 받는것도 가능 - join 하는 쿼리가 나감
            // 쿼리문도 join 하는걸로 써줘야함 (직접 명시하는게 좋다 : 명시적 조인)
            // List<Team> result = em.createQuery("select t from Member m join m.team t", Team.class)
            //                    .getResultList();

            // 임베디드 타입 프로젝션
            // from Address 할 수 없고 어디엔티티에 소속되어있는지를 써줘야 함. (값 타입의 한계)
            // em.createQuery("select o.address from Order o", Address.class)
            // .getResultList();

            // 스칼라 타입 프로젝션
            List resultList = em.createQuery("select distinct m.username, m.age from Member m")
                    .getResultList();

            // 1. 타입을 못 정하니까 Object로 돌려줘야함
            Object o = resultList.get(0);
            Object[] resulto = (Object[]) o;
            System.out.println("username = " + resulto[0]);
            System.out.println("age = " + resulto[1]);

            // 스칼라 타입 프로젝션
            List<Object[]> resultList2 = em.createQuery("select m.username, m.age from Member m")
                    .getResultList();

            // 2. Object[] 로 조회
            Object[] result2 = resultList2.get(0);
            System.out.println("username = " + result2[0]);
            System.out.println("age = " + resulto[1]);

            // 스칼라 타입 프로젝션
            List<MemberDTO> resultList3 = em.createQuery("select new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
                    .getResultList();

            // 3. new 명령어로 조회
            // 패키지 명을 포함한 전체 클래스 명 입력
            // 순서와 타입이 일치하는 생성자 필요
            MemberDTO result3 = resultList3.get(0);
            System.out.println("username = " + result3.getUsername());
            System.out.println("age = " + result3.getAge());

            Member findMember = result.get(0);
            findMember.setAge(20);

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
