package jpabook.javaspring.features.post.repositories;

import jpabook.javaspring.features.post.domains.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 동적 쿼리를 처리하기 위한 Post 엔티티의 커스텀 리포지토리 인터페이스
 */
public interface PostRepositoryCustom {

    /**
     * 동적 필터링으로 게시글 찾기
     * 
     * @param title 선택적 제목 필터 (포함)
     * @param content 선택적 내용 필터 (포함)
     * @param userId 선택적 사용자 ID 필터
     * @param pageable 페이지네이션 정보
     * @return 조건에 맞는 Post 엔티티의 페이지
     */
    Page<Post> findAllWithFilters(String title, String content, Long userId, Pageable pageable);
}
