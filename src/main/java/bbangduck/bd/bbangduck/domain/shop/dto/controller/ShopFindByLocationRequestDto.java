package bbangduck.bd.bbangduck.domain.shop.dto.controller;


import bbangduck.bd.bbangduck.global.common.PageRequest;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import static bbangduck.bd.bbangduck.global.common.constant.DefaultValue.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopFindByLocationRequestDto implements PageRequest {
  @Getter
  @Setter
  @NotNull
  private Double latitude;
  @Getter
  @Setter
  @NotNull
  private Double longitude;

  @Getter
  @Setter
  @Min(0)
  @NotNull
  private long pageNum = PAGE;

  @Getter
  @Setter
  @Min(0)
  @Max(100)
  @NotNull
  private int amount = CONTENT_COUNT;

}
