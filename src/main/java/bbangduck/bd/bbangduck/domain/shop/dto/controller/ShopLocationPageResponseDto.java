package bbangduck.bd.bbangduck.domain.shop.dto.controller;

import lombok.Getter;

import java.util.List;

public class ShopLocationPageResponseDto {
  @Getter
  private final List<ShopLocationResponseDto> contents;
  @Getter
  private final int page;
  @Getter
  private final boolean nextPage;
  @Getter
  private final int requestContentCount;

  public ShopLocationPageResponseDto(List<ShopLocationResponseDto> contents, int page, boolean nextPage, int requestContentCount) {
    this.contents = contents;
    this.page = page;
    this.nextPage = nextPage;
    this.requestContentCount = requestContentCount;
  }
}
