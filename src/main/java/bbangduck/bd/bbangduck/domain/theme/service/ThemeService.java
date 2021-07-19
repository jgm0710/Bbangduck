package bbangduck.bd.bbangduck.domain.theme.service;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.theme.dto.service.ThemeGetListDto;
import bbangduck.bd.bbangduck.domain.theme.dto.service.ThemeGetPlayMemberListDto;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.exception.ManipulateDeletedThemeException;
import bbangduck.bd.bbangduck.domain.theme.exception.ThemeNotFoundException;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeQueryRepository;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeRepository;
import bbangduck.bd.bbangduck.global.common.CriteriaDto;
import com.querydsl.core.QueryResults;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 테마와 관련된 비즈니스 로직을 구현한 Service
 *
 * @author jgm
 */
@Service
@RequiredArgsConstructor
public class ThemeService {

    private final ThemeRepository themeRepository;

    private final ThemeQueryRepository themeQueryRepository;

    public QueryResults<Theme> getThemeList(CriteriaDto criteriaDto, ThemeGetListDto themeGetListDto) {
        return themeQueryRepository.findList(criteriaDto, themeGetListDto);
    }

    public Theme getTheme(Long themeId) {
        Theme findTheme = themeRepository.findById(themeId).orElseThrow(ThemeNotFoundException::new);
        if (findTheme.isDeleteYN()) {
            throw new ManipulateDeletedThemeException();
        }
        return findTheme;
    }

    public void increaseThemeRating(Theme theme, int rating) {
        theme.increaseTotalRating(rating);
        theme.increaseTotalEvaluatedCount();
    }

    public void decreaseThemeRating(Theme theme, int rating) {
        theme.decreaseTotalRating(rating);
        theme.decreaseTotalEvaluatedCount();
    }

    public void updateThemeRating(Theme theme, int decreaseRating, int increaseRating) {
        decreaseThemeRating(theme, decreaseRating);
        increaseThemeRating(theme, increaseRating);
    }

    public List<Member> findThemePlayMemberList(Long themeId, ThemeGetPlayMemberListDto themeGetPlayMemberListDto) {
        List<Member> themePlayMemberList = themeQueryRepository.findThemePlayMemberList(themeId, themeGetPlayMemberListDto);
        List<Member> results = themePlayMemberList.stream()
                .distinct()
                .collect(Collectors.toList());

        return results.subList(0, Math.min(results.size(), themeGetPlayMemberListDto.getAmount()));
    }

    public long getThemePlayMembersCount(Long themeId) {
        return themeQueryRepository.getThemePlayMembersCount(themeId);
    }

}
