package bbangduck.bd.bbangduck.domain.genre.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.NotFoundException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 장르 조회 요청 시 장르를 찾을 수 없는 경우 발생할 예외
 */
public class GenreNotFoundException extends NotFoundException {
    public GenreNotFoundException() {
        super(ResponseStatus.GENRE_NOT_FOUND);
    }

    // 장르 코드를 통해 조회했을 때 장르를 찾을 수 없는 경우 사용
    public GenreNotFoundException(String genreCode) {
        super(ResponseStatus.GENRE_NOT_FOUND, ResponseStatus.GENRE_NOT_FOUND.getMessage() + " GenreCode : " + genreCode);
    }
}
