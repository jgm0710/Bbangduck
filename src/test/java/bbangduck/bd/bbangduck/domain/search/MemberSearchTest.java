package bbangduck.bd.bbangduck.domain.search;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.enbeded.RefreshInfo;
import bbangduck.bd.bbangduck.domain.member.entity.enumerate.MemberRole;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.domain.search.dto.MemberSearchDto;
import bbangduck.bd.bbangduck.domain.search.entity.MemberSearch;
import bbangduck.bd.bbangduck.domain.search.entity.enumerate.MemberSearchType;
import bbangduck.bd.bbangduck.domain.search.service.MemberSearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


/**
 * Project : bbangduck
 * Create by IntelliJ IDEA
 * User: otrodevym
 * Date: 2021/6/1/0001
 * Time: 오후 12:54:26
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
@AutoConfigureMockMvc
//@RequiredArgsConstructor
public class MemberSearchTest {

//    @Autowired
    @MockBean
    private MemberSearchService memberSearchService;

//    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MemberRepository memberRepository;

//    @Autowired
    @MockBean
    private MemberService memberService;

    @Autowired
    private WebApplicationContext ctx;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();

        Member member = Member.builder()
                .email("otrodevym@gmail.com")
                .password("1234")
                .nickname("developer")
                .description("개발자")
                .roomEscapeRecordsOpenYN(false)
                .refreshInfo(RefreshInfo.init(1000))
                .roles(Set.of(MemberRole.DEVELOP))
                .build();
        this.memberRepository.save(member);

        this.entityManager.flush();
        this.entityManager.clear();

        MemberSearch memberSearch = MemberSearch.builder()
                .searchTimes(LocalDateTime.now())
                .searchKeyword("빵덕 찾아줭")
                .searchType(MemberSearchType.T01)
                .searchDate(LocalDate.now())
                .member(member)
                .build();
        this.memberSearchService.save(MemberSearchDto.of(memberSearch));

    }

    //    @Test
    public void searchSaveTest() throws Exception {

        List<MemberSearchDto> searchDtos = this.memberSearchService.findAll();

        searchDtos.forEach(System.out::println);

    }

    //    @Test
    public void searchTest() {


//                  T01("테마 검색")
//                , S01("샵 검색")
//                , M01("회원 검색")
//                , F01("친구 검색")
//                , C01("커뮤니티 제목/내용")
//                , C02("커뮤니티 제목")
//                , C03("커뮤니티 내용")
//                , L01("지역 검색");

        MemberSearchDto memberSearchDto = MemberSearchDto.builder().searchType(MemberSearchType.C01).searchKeyword("테스트").build();

        List<MemberSearchDto> searchDtos = this.memberSearchService.findByMemberSearchTypeAndKeyword(memberSearchDto);

        searchDtos.forEach(System.out::println);
    }

    @Test
    public void controllerSaveTest() throws Exception {
        MemberSearchDto memberSearchDto = MemberSearchDto.builder()
                .searchType(MemberSearchType.T01).searchKeyword("빵덕 찾아줭").memberId(1L).id(1L).build();




        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(memberSearchDto);

        System.out.println(json.toString());
        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/member/search/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
}