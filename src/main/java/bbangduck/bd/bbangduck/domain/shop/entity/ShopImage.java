package bbangduck.bd.bbangduck.domain.shop.entity;

import lombok.Builder;

import javax.persistence.Embeddable;

@Embeddable
public class ShopImage {
  private Long fileStorageId;

  private String fileName;


  public String getFileName() {
    return fileName;
  }

  @Builder
  public ShopImage(Long fileStorageId, String fileName) {
    this.fileStorageId = fileStorageId;
    this.fileName = fileName;
  }

  public ShopImage() {

  }

  public static ShopImage of(Long fileStorageId, String fileName) {
    return ShopImage.builder()
        .fileName(fileName)
        .fileStorageId(fileStorageId)
        .build();
  }
}
