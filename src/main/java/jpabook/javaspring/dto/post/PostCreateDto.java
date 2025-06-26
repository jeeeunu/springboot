package jpabook.javaspring.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jpabook.javaspring.domain.post.Post;
import jpabook.javaspring.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCreateDto {

    @NotBlank(message = "제목은 필수 입력 항목입니다")
    @Size(min = 3, max = 100, message = "제목은 3자 이상 100자 이하여야 합니다")
    private String title;

    @NotBlank(message = "내용은 필수 입력 항목입니다")
    @Size(min = 10, message = "내용은 최소 10자 이상이어야 합니다")
    private String content;

    public Post toEntity(User author) {
        return Post.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }
}
