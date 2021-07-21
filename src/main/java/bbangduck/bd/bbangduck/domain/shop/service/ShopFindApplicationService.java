package bbangduck.bd.bbangduck.domain.shop.service;

import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.domain.shop.entity.embeded.Location;
import bbangduck.bd.bbangduck.global.common.SliceResultResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ShopFindApplicationService {

  private final ShopFindService shopFindService;
  private final ShopService shopService;


  public SliceResultResponseDto<Shop> findAllByDistance(Location location, Integer distance, @Min(0) Integer amount, @Min(0) @NotNull Long pageNum) {
    List<Shop> list = shopFindService.findAllByKmDistance(location, distance, amount + 1, pageNum * amount);
    return SliceResultResponseDto.by(list, pageNum, amount);
  }

  public Shop getShop(Long shopId) {
    return shopService.getById(shopId);
  }
}
