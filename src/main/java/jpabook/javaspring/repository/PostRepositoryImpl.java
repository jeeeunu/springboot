package jpabook.javaspring.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jpabook.javaspring.domain.post.Post;
import jpabook.javaspring.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PostRepositoryImpl implements PostRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Post> findAllWithFilters(String title, String content, Long userId, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> query = cb.createQuery(Post.class);
        Root<Post> post = query.from(Post.class); // FROM Post p

        List<Predicate> predicates = new ArrayList<>();
        Join<Post, User> authorJoin = null; // author 조인이 필요한 경우에만 생성

        // 제목 조건
        if (StringUtils.hasText(title)) {
            predicates.add(cb.like(cb.lower(post.get("title")), "%" + title.toLowerCase() + "%"));
        }

        // 내용 조건
        if (StringUtils.hasText(content)) {
            predicates.add(cb.like(cb.lower(post.get("content")), "%" + content.toLowerCase() + "%"));
        }

        // 작성자 ID 조건 (필요 시 author 조인)
        if (userId != null) {
            authorJoin = post.join("author");
            predicates.add(cb.equal(authorJoin.get("id"), userId));
        }

        // where 절 적용
        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        // 정렬 처리
        if (pageable.getSort().isSorted()) {
            List<Order> orders = new ArrayList<>();

            // author.xxx 정렬이 필요한 경우 조인이 되어야 함
            boolean needsAuthorJoin = pageable.getSort().stream()
                    .anyMatch(order -> order.getProperty().startsWith("author."));
            if (needsAuthorJoin && authorJoin == null) {
                authorJoin = post.join("author");
            }

            final Join<Post, User> finalAuthorJoin = authorJoin;

            // 각 정렬 필드를 순회하며 Order 객체 생성
            pageable.getSort().forEach(sort -> {
                try {
                    Path<?> propertyPath;
                    if (sort.getProperty().startsWith("author.")) {
                        if (finalAuthorJoin == null) return; // author 조인 없으면 스킵
                        String nested = sort.getProperty().substring("author.".length());
                        propertyPath = finalAuthorJoin.get(nested);
                    } else {
                        propertyPath = post.get(sort.getProperty());
                    }

                    orders.add(sort.isAscending() ? cb.asc(propertyPath) : cb.desc(propertyPath));
                } catch (IllegalArgumentException e) {
                    // 잘못된 정렬 필드 무시
                    System.err.println("정렬 필드 오류: " + sort.getProperty());
                }
            });

            if (!orders.isEmpty()) {
                query.orderBy(orders);
            }
        } else {
            // 기본 정렬: createdAt 내림차순
            query.orderBy(cb.desc(post.get("createdAt")));
        }

        // 조회 쿼리 실행 (페이징 적용)
        TypedQuery<Post> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        List<Post> posts = typedQuery.getResultList();

        // count 쿼리 작성
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Post> countRoot = countQuery.from(Post.class);

        // count 쿼리용 author 조인
        if (userId != null) {
            countRoot.join("author");
        }

        // count 쿼리용 조건 재작성 (root가 다르기 때문에 다시 만들어야 함)
        List<Predicate> countPredicates = new ArrayList<>();

        if (StringUtils.hasText(title)) {
            countPredicates.add(cb.like(cb.lower(countRoot.get("title")), "%" + title.toLowerCase() + "%"));
        }

        if (StringUtils.hasText(content)) {
            countPredicates.add(cb.like(cb.lower(countRoot.get("content")), "%" + content.toLowerCase() + "%"));
        }

        if (userId != null) {
            Join<Post, User> countAuthorJoin = countRoot.join("author");
            countPredicates.add(cb.equal(countAuthorJoin.get("id"), userId));
        }

        if (!countPredicates.isEmpty()) {
            countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));
        }

        countQuery.select(cb.count(countRoot)); // select count(p)
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        // Page 객체로 결과 반환
        return new PageImpl<>(posts, pageable, total);
    }
}