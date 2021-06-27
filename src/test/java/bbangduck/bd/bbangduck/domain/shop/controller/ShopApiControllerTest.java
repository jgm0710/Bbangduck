package bbangduck.bd.bbangduck.domain.shop.controller;

import bbangduck.bd.bbangduck.common.BaseControllerTest;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.domain.shop.entity.embeded.Location;
import bbangduck.bd.bbangduck.domain.shop.repository.ShopQueryRepository;
import bbangduck.bd.bbangduck.global.common.util.DistanceUtil;
import bbangduck.bd.bbangduck.global.config.properties.SecurityJwtProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Shop 엔드포인트 테스트 및 문서화")
@SpringBootTest
public class ShopApiControllerTest extends BaseControllerTest {

  @MockBean
  ShopQueryRepository shopQueryRepository;



  @Autowired
  SecurityJwtProperties securityJwtProperties;

  @Test
  void getShopsByNowLocationTest() throws Exception {

    int DISTANCE = 5;
    double latitude = 126.73;
    double longitude = 37.41;
    double topLatitude = DistanceUtil.calculateLatitudeDistance(latitude, DISTANCE);
    double bottomLatitude = DistanceUtil.calculateLatitudeDistance(latitude, -DISTANCE);
    double leftLongitude = DistanceUtil.calculateLongitudeDistance(longitude, DISTANCE);
    double rightLongitude = DistanceUtil.calculateLongitudeDistance(longitude, -DISTANCE);
    List<Shop> tempList = new ArrayList<>();

    for (int i = 0; i < 20; i++) {
      tempList.add(Shop.builder()
          .name("ShopName" + i)
          .location(Location.builder()
              .longitude(longitude)
              .latitude(latitude).build())
          .build());
    }

    when(shopQueryRepository.findByRangeLocation(anyDouble(),anyDouble(),anyDouble(),anyDouble())).thenReturn(tempList);


    mockMvc.perform(RestDocumentationRequestBuilders.get("/api/shops/now-location")
        .param("latitude", String.valueOf(latitude))
        .param("longitude", String.valueOf(longitude)))
        .andExpect(status().isOk())
        .andDo(document(
            "get-shop-by-now-location",
            requestParameters(
                parameterWithName("latitude").description("위도"),
                parameterWithName("longitude").description("경도")
            ),
            responseFields(
                fieldWithPath("page").description("페이지 번호"),
                fieldWithPath("nextPage").description("다음 페이지"),
                fieldWithPath("requestContentCount").description("요청 카운트 개수"),
                fieldWithPath("contents").description("샵 리스트"),
                fieldWithPath("contents[].distance").description("거리"),
                fieldWithPath("contents[].title").description("샵 이름"),
                fieldWithPath("contents[].latitude").description("샵 위도"),
                fieldWithPath("contents[].longitude").description("샵 경도")
            )
        ));

  }

}
