package bbangduck.bd.bbangduck.domain.shop.service;

import bbangduck.bd.bbangduck.domain.shop.dto.service.ShopCreateCommand;
import bbangduck.bd.bbangduck.domain.shop.entity.Franchise;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ShopApplicationService {

  private final ShopService shopService;
  private final FranchiseService franchiseService;

  public Shop saveShop(CreateDto createDto) {
    Franchise franchise = franchiseService.getById(createDto.franchiseId);
    return shopService.save(createDto.toCreateCommandWith(franchise));
  }

  public void delete(Long shopId) {
    shopService.delete(shopId);
  }

  public static class CreateDto {
    private final double latitude;
    private final double longitude;
    private final Long franchiseId;
    private final Long fileStorageId;
    private final String fileName;
    private final String description;
    private final String name;
    private final String address;
    private final String shopUrl;

    @Builder
    public CreateDto(double latitude, double longitude, Long franchiseId, Long fileStorageId, String fileName, String description, String name, String address, String shopUrl) {
      this.latitude = latitude;
      this.longitude = longitude;
      this.franchiseId = franchiseId;
      this.fileStorageId = fileStorageId;
      this.fileName = fileName;
      this.description = description;
      this.name = name;
      this.address = address;
      this.shopUrl = shopUrl;
    }


    public ShopCreateCommand toCreateCommandWith(Franchise franchise) {
      return ShopCreateCommand.builder()
          .latitude(latitude)
          .longitude(longitude)
          .franchise(franchise)
          .description(description)
          .name(name)
          .fileStorageId(fileStorageId)
          .fileName(fileName)
          .address(address)
          .shopUrl(shopUrl)
          .build();
    }
  }

}
