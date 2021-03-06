package bbangduck.bd.bbangduck.domain.search;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.enbeded.RefreshInfo;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRole;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRoomEscapeRecodesOpenStatus;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.domain.search.dto.MemberSearchDto;
import bbangduck.bd.bbangduck.domain.search.entity.MemberSearch;
import bbangduck.bd.bbangduck.domain.search.entity.enumerate.MemberSearchType;
import bbangduck.bd.bbangduck.domain.search.service.MemberSearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
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
 * Time: ?????? 12:54:26
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
@AutoConfigureMockMvc
//@RequiredArgsConstructor
@ActiveProfiles("test") // db ???????????? application-test.yml ????????? ???????????? ???????????? annotation
public class MemberSearchTest {

    @Autowired
//    @MockBean
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
                .description("?????????")
                .roomEscapeRecodesOpenStatus(MemberRoomEscapeRecodesOpenStatus.CLOSE)
                .refreshInfo(RefreshInfo.init(1000))
                .roles(Set.of(MemberRole.DEVELOP))
                .build();
        this.memberRepository.save(member);


        MemberSearch memberSearch = MemberSearch.builder()
                .searchTimes(LocalDateTime.now())
                .searchKeyword("?????? ?????????")
                .searchType(MemberSearchType.T01)
                .searchDate(LocalDate.now())
                .member(member)
                .build();

        MemberSearchDto of = MemberSearchDto.of(memberSearch);
        System.out.println("===");
        System.out.println(of.toString());
        System.out.println("===");
        this.memberSearchService.save(of);

        MemberSearch memberSearch1 = MemberSearch.builder()
                .searchTimes(LocalDateTime.now())
                .searchKeyword("?????? ???????????????")
                .searchType(MemberSearchType.T01)
                .searchDate(LocalDate.now().minusMonths(2))
                .member(member)
                .build();
        this.memberSearchService.save(MemberSearchDto.of(memberSearch1));

        this.entityManager.flush();
        this.entityManager.clear();

    }

    //    @Test
    public void searchSaveTest() throws Exception {

        List<MemberSearchDto> searchDtos = this.memberSearchService.findAll();

        searchDtos.forEach(System.out::println);

    }

    //    @Test
    public void searchTest() {


//                  T01("?????? ??????")
//                , S01("??? ??????")
//                , M01("?????? ??????")
//                , F01("?????? ??????")
//                , C01("???????????? ??????/??????")
//                , C02("???????????? ??????")
//                , C03("???????????? ??????")
//                , L01("?????? ??????");

        MemberSearchDto memberSearchDto = MemberSearchDto.builder().searchType(MemberSearchType.C01).searchKeyword("?????????").build();

        List<MemberSearchDto> searchDtos = this.memberSearchService.findByMemberSearchTypeAndKeyword(memberSearchDto);

        searchDtos.forEach(System.out::println);
    }

    @Test
    public void controllerSaveTest() throws Exception {
        MemberSearchDto memberSearchDto = MemberSearchDto.builder()
                .searchType(MemberSearchType.T01).searchKeyword("?????? ?????????").memberId(1L).id(1L).build();

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

//    @Test
    public void searchTopMonthListTest() {

        List<MemberSearchDto> all = this.memberSearchService.findAll();
        all.forEach(System.out::println);

        System.out.println("=================================================");

        List<MemberSearchDto.MemberSearchTopMonthDto> memberSearchTopMonthDtos = this.memberSearchService.searchTopMonthList();
        memberSearchTopMonthDtos.forEach(System.out::println);

        MatcherAssert.assertThat(memberSearchTopMonthDtos.size(), CoreMatchers.is(1));
    }


    @Test
     public void searchMyTop10SearchLog() throws Exception {
         String email = "otrodevym@gmail.com";
        List<MemberSearchDto> list = this.memberSearchService.findTop10ByMemberIdAndSearchDateLessThan(email,
                 LocalDate.now().minusMonths(1));

        list.stream().forEach(System.out::println);
     }


//    public static void main(String[] args) {
//        System.out.println(LocalDate.now().minusMonths(2));
//    }
}
