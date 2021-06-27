package bbangduck.bd.bbangduck.domain.shop.dto.controller;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import static bbangduck.bd.bbangduck.global.common.constant.DefaultValue.*;

public class ShopFindByLocationRequestDto {
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
  private Long page = PAGE;

  @Getter
  @Setter
  @Min(0)
  @Max(100)
  @NotNull
  private Integer contentCount = CONTENT_COUNT;

}
