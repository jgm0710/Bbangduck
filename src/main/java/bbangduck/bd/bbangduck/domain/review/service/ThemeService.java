package bbangduck.bd.bbangduck.domain.review.service;

import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.exception.ManipulateDeletedThemeException;
import bbangduck.bd.bbangduck.domain.theme.exception.ThemeNotFoundException;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ThemeService {

  private final ThemeRepository themeRepository;

  public Theme getTheme(Long themeId) {
    Theme theme = themeRepository.findById(themeId).orElseThrow(ThemeNotFoundException::new);
    if (theme.isDeleteYN()) {
      throw new ManipulateDeletedThemeException();
    }
    return theme;
  }

}
