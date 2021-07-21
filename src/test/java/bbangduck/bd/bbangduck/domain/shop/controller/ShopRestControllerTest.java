package bbangduck.bd.bbangduck.domain.shop.controller;

import bbangduck.bd.bbangduck.domain.shop.dto.controller.ShopFindByLocationRequestDto;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.domain.shop.entity.embeded.Location;
import bbangduck.bd.bbangduck.domain.shop.service.ShopApplicationService;
import bbangduck.bd.bbangduck.domain.shop.service.ShopFindApplicationService;
import bbangduck.bd.bbangduck.global.common.SliceResultResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ShopRestController.class)
@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
@ActiveProfiles("web-test")
class ShopRestControllerTest {

  protected MockMvc mockMvc;
  @Autowired
  protected ObjectMapper objectMapper;
  @MockBean
  ShopFindApplicationService shopFindApplicationService;
  @MockBean
  ShopApplicationService shopApplicationService;


  @BeforeEach
  public void setUp(WebApplicationContext webApplicationContext,
                    RestDocumentationContextProvider restDocumentation) {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .apply(documentationConfiguration(restDocumentation)).build();
  }

  @Test
  void findShopByNowLocation() throws Exception {
    int DISTANCE = 5;
    double latitude = 126.73;
    double longitude = 37.41;
    List<Shop> tempList = new ArrayList<>();
    long pageNum = 0;
    int amount = 10;
    for (int i = 0; i <= amount; i++) {
      double v = new Random().nextDouble() / 100;
      tempList.add(Shop.builder().name("shopName" + i).location(Location.builder().longitude(longitude + v).latitude(latitude + v).build()).build());
    }


    SliceResultResponseDto<Shop> result = SliceResultResponseDto.by(tempList, pageNum, amount);
    when(shopFindApplicationService.findAllByDistance(any(), anyInt(), anyInt(), anyLong())).thenReturn(
        result
    );

    ShopFindByLocationRequestDto request = ShopFindByLocationRequestDto.builder()
        .amount(amount)
        .latitude(latitude)
        .longitude(longitude)
        .pageNum(pageNum)
        .build();

    mockMvc.perform(RestDocumentationRequestBuilders.get("/api/shops/now-location")
        .param("amount", String.valueOf(request.getAmount()))
        .param("latitude", String.valueOf(request.getLatitude()))
        .param("longitude", String.valueOf(request.getLongitude()))
        .param("pageNum", String.valueOf(request.getPageNum())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("contents").isArray())
        .andExpect(jsonPath("nowPageNum").value(pageNum))
        .andExpect(jsonPath("requestAmount").value(amount))
        .andExpect(jsonPath("hasNextPage").value(true))
        .andDo(document(
            "get-shop-by-now-location",
            requestParameters(
                parameterWithName("amount").description("조회 요청 개수"),
                parameterWithName("latitude").description("위도 정보"),
                parameterWithName("longitude").description("경도 정보"),
                parameterWithName("pageNum").description("요청 페이지 번호")
            ),
            responseFields(
                fieldWithPath("contents").description("샵 리스트"),
                fieldWithPath("contents[].distance").description("현재위치와의 거리 (KM)"),
                fieldWithPath("contents[].latitude").description("샵 위도"),
                fieldWithPath("contents[].longitude").description("샵 경도"),
                fieldWithPath("contents[].title").description("샵 제목"),
                fieldWithPath("nowPageNum").description("현재 페이지 번호"),
                fieldWithPath("requestAmount").description("요청 개수"),
                fieldWithPath("hasNextPage").description("다음 페이지 존재여부")
            )
        ));
  }
}