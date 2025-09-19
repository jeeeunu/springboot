package jpabook.javaspring.features.admin.repositories;

import jpabook.javaspring.features.admin.domains.Admin;
import jpabook.javaspring.features.post.domains.Post;
import jpabook.javaspring.features.post.repositories.PostRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Page<Admin> findAll(Pageable pageable);
    Optional<Admin> findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);

    List<Admin> loginId(String loginId);
}
