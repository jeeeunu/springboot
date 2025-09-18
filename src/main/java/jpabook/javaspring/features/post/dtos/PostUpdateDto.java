package jpabook.javaspring.features.post.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdateDto {

    @NotBlank(message = "제목은 필수 입력 항목입니다")
    @Size(min = 3, max = 100, message = "제목은 최소 3자, 최대 100자까지 입력해 주세요.")
    private String title;

    @NotBlank(message = "내용은 필수 입력 항목입니다")
    @Size(min = 10, message = "내용은 최소 10자 이상 입력해 주세요.")
    private String content;
}
