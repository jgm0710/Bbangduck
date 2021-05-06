package bbangduck.bd.bbangduck.global.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
