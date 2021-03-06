package bbangduck.bd.bbangduck.domain.theme.service;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.theme.dto.service.ThemeGetPlayMemberListDto;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.entity.ThemePlayMember;
import bbangduck.bd.bbangduck.domain.theme.exception.ThemePlayMemberNotFoundException;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemePlayMemberQueryRepository;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemePlayMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ThemePlayMemberService {

    private final ThemePlayMemberRepository themePlayMemberRepository;

    private final ThemePlayMemberQueryRepository themePlayMemberQueryRepository;

    @Transactional
    public void playTheme(Theme theme, Member member) {
        ThemePlayMember themePlayMember = themePlayMemberRepository.findByThemeAndMember(theme, member).orElse(ThemePlayMember.init(theme, member));
        themePlayMember.refreshLastPlayDateTime();
        themePlayMemberRepository.save(themePlayMember);
    }

    @Transactional(readOnly = true)
    public ThemePlayMember getThemePlayMember(Long themeId, Long memberId) {
        return themePlayMemberQueryRepository.findByThemeIdAndMemberId(themeId, memberId).orElseThrow(ThemePlayMemberNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public long getThemePlayMembersCount(Long themeId) {
        return themePlayMemberQueryRepository.getThemePlayMembersCount(themeId);
    }

    @Transactional(readOnly = true)
    public List<ThemePlayMember> findThemePlayMemberList(Long themeId, ThemeGetPlayMemberListDto themeGetPlayMemberListDto) {
        return themePlayMemberQueryRepository.findListByThemeId(themeId, themeGetPlayMemberListDto);
    }

    @Transactional
    public void deleteThemePlayMember(ThemePlayMember themePlayMember) {
        themePlayMemberRepository.delete(themePlayMember);
    }

}
