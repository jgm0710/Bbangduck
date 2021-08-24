package bbangduck.bd.bbangduck.domain.shop.test.service;

import bbangduck.bd.bbangduck.domain.shop.repository.AreaRepository;
import bbangduck.bd.bbangduck.domain.shop.repository.FranchiseRepository;
import bbangduck.bd.bbangduck.domain.shop.repository.ShopRepository;
import bbangduck.bd.bbangduck.domain.shop.service.ShopImageService;
import bbangduck.bd.bbangduck.domain.shop.service.ShopService;
import bbangduck.bd.bbangduck.domain.shop.service.ShopServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

@DisplayName("Shop 관련 CRUD API 테스트")
public class ShopCRUDTestService {

    ShopService shopService;

    ShopRepository shopMockRepository = mock(ShopRepository.class);
    AreaRepository areaMockRepository = mock(AreaRepository.class);
    FranchiseRepository franchiseMockRepository = mock(FranchiseRepository.class);
    ShopImageService shopImageMockService = mock(ShopImageService.class);


    @BeforeEach
    public void shopCRUDTestServiceSetup() {
        shopService = new ShopServiceImpl(
                shopMockRepository,
                areaMockRepository,
                franchiseMockRepository,
                shopImageMockService
        );
    }


    @Test
    @DisplayName("샵 저장 - 기본 저장 기능")
    public void shopSaveTest() {
        //given
//        given(shopMockRepository.save())


    }


}
