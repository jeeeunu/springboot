package jpabook.javaspring.dto.post;

import jpabook.javaspring.domain.post.Post;
import jpabook.javaspring.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private UserDto author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long likeCount;

    public static PostDto fromEntity(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(UserDto.fromEntity(post.getAuthor()))
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .likeCount(0) // Default to 0, will be updated by service
                .build();
    }

    public static PostDto fromEntity(Post post, long likeCount) {
        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(UserDto.fromEntity(post.getAuthor()))
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .likeCount(likeCount)
                .build();
    }
}
