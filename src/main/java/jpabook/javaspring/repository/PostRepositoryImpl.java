package jpabook.javaspring.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jpabook.javaspring.domain.post.Post;
import jpabook.javaspring.domain.post.QPost;
import jpabook.javaspring.domain.user.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public PostRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Post> findAllWithFilters(String title, String content, Long userId, Pageable pageable) {
        QPost post = QPost.post;
        QUser author = QUser.user;

        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(title)) {
            builder.and(post.title.containsIgnoreCase(title));
        }

        if (StringUtils.hasText(content)) {
            builder.and(post.content.containsIgnoreCase(content));
        }

        if (userId != null) {
            builder.and(post.author.id.eq(userId));
        }

        List<Post> contentList = queryFactory
                .selectFrom(post)
                .leftJoin(post.author, author).fetchJoin()
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifiers(post, author, pageable))
                .fetch();

        long total = queryFactory
                .select(post.count())
                .from(post)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(contentList, pageable, total);
    }

    private com.querydsl.core.types.OrderSpecifier<?>[] getOrderSpecifiers(QPost post, QUser author, Pageable pageable) {
        List<com.querydsl.core.types.OrderSpecifier<?>> orders = new ArrayList<>();

        for (Sort.Order sortOrder : pageable.getSort()) {
            com.querydsl.core.types.Order direction = sortOrder.isAscending() ?
                    com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC;

            String property = sortOrder.getProperty();

            switch (property) {
                case "title" -> orders.add(new com.querydsl.core.types.OrderSpecifier<>(direction, post.title));
                case "content" -> orders.add(new com.querydsl.core.types.OrderSpecifier<>(direction, post.content));
                case "createdAt" -> orders.add(new com.querydsl.core.types.OrderSpecifier<>(direction, post.createdAt));
                case "updatedAt" -> orders.add(new com.querydsl.core.types.OrderSpecifier<>(direction, post.updatedAt));
                case "author.username" -> orders.add(new com.querydsl.core.types.OrderSpecifier<>(direction, author.username));
                case "author.name" -> orders.add(new com.querydsl.core.types.OrderSpecifier<>(direction, author.name));
                default -> orders.add(new com.querydsl.core.types.OrderSpecifier<>(direction, post.createdAt)); // fallback
            }
        }

        return orders.toArray(new com.querydsl.core.types.OrderSpecifier[0]);
    }
}
