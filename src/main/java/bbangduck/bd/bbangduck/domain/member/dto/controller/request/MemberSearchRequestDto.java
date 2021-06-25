package bbangduck.bd.bbangduck.domain.member.dto.controller.request;

import bbangduck.bd.bbangduck.domain.member.enumerate.MemberSearchKeywordType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 회원 검색 요청 Body Data 를 담을 Dto
 *
 * @author jgm
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberSearchRequestDto {

    @NotNull(message = "회원 검색 시 이메일을 통한 검색인지, 닉네임을 통한 검색인지에 대한 값을 기입해 주세요.")
    private MemberSearchKeywordType searchType;

    @NotBlank(message = "회원 검색 시 필요한 키워드를 기입해 주세요.")
    private String keyword;

}
