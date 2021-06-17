package bbangduck.bd.bbangduck.domain.member.dto.controller.response;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberPlayInclination;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRoomEscapeRecodesOpenStatus;
import bbangduck.bd.bbangduck.domain.review.dto.entity.ReviewRecodesCountsDto;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

import static bbangduck.bd.bbangduck.global.common.NullCheckUtils.existsList;

/**
 * 작성자 : Gumin Jeong
 * <p>
 * 작성 일자 : 2021-06-17
 * <p>
 * 회원 프로필 조회 응답 Body Data 를 담을 Dto
 * 다른 회원의 프로필을 조회한 경우 해당 Dto 를 통해 응답된다.
 */
@Data
public class MemberProfileResponseDto {

    private Long memberId;

    private MemberProfileImageResponseDto profileImage;

    private String nickname;

    private String description;

    private MemberRoomEscapeStatusResponseDto roomEscapeStatus;

    private MemberRoomEscapeRecodesOpenStatus roomEscapeRecodesOpenStatus;

    private List<MemberPlayInclinationResponseDto> playInclinations;

    public static MemberProfileResponseDto convert(Member member, ReviewRecodesCountsDto reviewRecodesCountsDto, List<MemberPlayInclination> memberPlayInclinations) {
        return new MemberProfileResponseDto(member, reviewRecodesCountsDto, memberPlayInclinations);
    }

    protected MemberProfileResponseDto(Member member, ReviewRecodesCountsDto reviewRecodesCountsDto, List<MemberPlayInclination> memberPlayInclinations) {
        this.memberId = member.getId();
        this.profileImage = MemberProfileImageResponseDto.convert(member.getProfileImage());
        this.nickname = member.getNickname();
        this.description = member.getDescription();
        this.roomEscapeStatus = MemberRoomEscapeStatusResponseDto.convert(reviewRecodesCountsDto);
        this.roomEscapeRecodesOpenStatus = member.getRoomEscapeRecodesOpenStatus();
        this.playInclinations = convertMemberPlayInclinationsToResponseDtos(memberPlayInclinations);
    }

    private List<MemberPlayInclinationResponseDto> convertMemberPlayInclinationsToResponseDtos(List<MemberPlayInclination> memberPlayInclinations) {
        if (existsList(memberPlayInclinations)) {
            return memberPlayInclinations.stream()
                        .map(MemberPlayInclinationResponseDto::convert).collect(Collectors.toList());
        }
        return null;
    }
}
