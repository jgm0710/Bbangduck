package bbangduck.bd.bbangduck.domain.shop.test.controller;

import bbangduck.bd.bbangduck.domain.shop.controller.ShopController;
import bbangduck.bd.bbangduck.domain.shop.dto.ShopDto;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.domain.shop.entity.embeded.Location;
import bbangduck.bd.bbangduck.domain.shop.repository.ShopRepository;
import bbangduck.bd.bbangduck.domain.shop.service.ShopService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.only;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@DisplayName("shop CRUD controller Test")
@ExtendWith(MockitoExtension.class)
public class ShopCRUDTestController {

    @InjectMocks
    private ShopController shopController;

    @Mock
    private ShopService shopService;

    @Mock
    private ShopRepository shopRepository;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;


    @BeforeEach
    public void setUP() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(shopController).build();
        this.objectMapper = new ObjectMapper();
    }


    @Test
    @DisplayName("샵 저장 테스트")
    public void shopSaveSuccess() throws Exception {
        //given
        final ShopDto.Save shopDtoSave = shopDtoSave();

        //when
        final ResultActions resultActions = this.mockMvc.perform(
                MockMvcRequestBuilders.post("/shop/").contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(shopDtoSave))
        );

        //then
        final MvcResult mvcResult =
                resultActions.andExpect(MockMvcResultMatchers.status().isOk()).andDo(print()).andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("샵 아이디 조회 테스트")
    public void shopFindIdSuccess() throws Exception {
        //given
        final Shop shop = shopFindId();
//        doReturn(shop).when(shopService).findById(ArgumentMatchers.anyLong()); // mockito
        given(shopService.findById(shop.getId())).willReturn(shop); // BDD mockito

        //when
        final Shop shopResult = shopService.findById(1l);
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/shop/{shopId}", shop.getId()));

        //then
        assertThat(shopResult.getName()).as("check Id : %s", shopResult.getId()).isEqualTo("용미니네");
        resultActions.andExpect(MockMvcResultMatchers.status().isOk()).andDo(print());
//        verify(shopService).findById(1l);

//        ArgumentCaptor argumentCaptor = ArgumentCaptor.forClass(String.class); // 호출 인자 보관
        then(shopService).should(atLeast(2)).findById(1l);

    }

    private Shop shopFindId() {
        return Shop.builder()
//                .shopImage()
//                .franchise()
//                .area()
                .location(new Location(123.4, 567.8))
                .address("서울시 용산구")
                .deleteYN(false)
                .shopUrl("www.naver.om")
                .id(1l)
                .shopInfo("재미난 방탈출")
                .name("용미니네")
                .build();
    }

    private ShopDto.Save shopDtoSave() {
        return ShopDto.Save.builder()
                .address("서울시 용산구")
                .areaId(1l)
                .franchiseId(1l)
                .lat(123.4)
                .lon(123.5)
                .ShopImageId(1l)
                .shopUrl("www.naver.om")
                .name("용미니네")
                .build();

    }
}
