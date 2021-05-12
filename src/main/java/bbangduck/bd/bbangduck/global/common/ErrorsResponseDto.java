package bbangduck.bd.bbangduck.global.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 작성자 : 정구민 <br><br>
 *
 * ValidationHasErrorException Handler 에서 Errors 를
 * 해당 포멧에 맞게 응답하기 위해 구현
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorsResponseDto {

    private String objectName;

    private String code;

    private String defaultMessage;

    private String field;
}
