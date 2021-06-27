package bbangduck.bd.bbangduck.domain.shop;

import bbangduck.bd.bbangduck.domain.shop.dto.AreaDto;
import bbangduck.bd.bbangduck.domain.shop.dto.FranchiseDto;
import bbangduck.bd.bbangduck.domain.shop.dto.ShopDto;
import bbangduck.bd.bbangduck.domain.shop.dto.ShopImageDto;
import bbangduck.bd.bbangduck.domain.shop.entity.ShopImage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.transaction.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
//@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ShopControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
//    public void setup(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
    public void setup(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
//                .apply(springSecurity())
//                .alwaysDo(print())
//                .apply(documentationConfiguration(restDocumentation)
//                        .operationPreprocessors()
//                        .withRequestDefaults(prettyPrint())
//                        .withResponseDefaults(prettyPrint()))
                .build();
    }


    //    @Test
    public void shopFindByIdTest() throws Exception {

//        this.mockMvc.perform(MockMvcRequestBuilders.get("/shop/1")).andDo(print());
    }


    @Test
    public void shopSaveTest() throws Exception {
        ShopDto shopDto = ShopDto.builder()
                .address("서울시")
                .shopInfo("재미")
                .areaId(1l)
                .franchiseId(1l)
                .build();
//        String shopDtoString = objectMapper.writeValueAsString(shopDto);

        ShopImageDto shopImageDto = ShopImageDto.builder()
                .fileName("미니 사진")
                .fileStorageId(1l)
                .build();
//        String shopImageDtoString = objectMapper.writeValueAsString(shopImageDto);

        Map<String, Object> map = new HashMap<>();

        map.put("ShopDto", shopDto);
        map.put("ShopImageDto", shopImageDto);
        String content = objectMapper.writeValueAsString(shopDto);

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/shop/")
                .param("ShopDto", objectMapper.writeValueAsString(shopDto))
                .param("ShopImageDto", objectMapper.writeValueAsString(shopImageDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)

        )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
