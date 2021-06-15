package bbangduck.bd.bbangduck.domain.shop.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "area_id")
    private Long id;

    @Column(name = "area_code")
    private String code;

    @Column(name = "area_name")
    private String name;


    public List<Area> areaMock() {
        List<Area> areaList = new ArrayList<>();
//        SU00	서울
//        SU01	홍대
//        SU02	강남
//        SU03	건대
//        KK00	경기
//        IC00	인천
//        SW00	수원
//        AS00	안산
//        BS00	부산
//        DK00	대구
//        US00	울산
//        DJ00	대전
//        KJ00	광주
//        KS00	경상
//        KL00	전라
//        CC00	충청
//        KW00	강원
//        JJ00	제주
        areaList.add(Area.builder().code("SU00").name("서울").build());
        areaList.add(Area.builder().code("SU01").name("홍대").build());
        areaList.add(Area.builder().code("SU02").name("강남").build());
        areaList.add(Area.builder().code("SU03").name("건대").build());
        areaList.add(Area.builder().code("KK00").name("경기").build());
        areaList.add(Area.builder().code("IC00").name("인천").build());
        areaList.add(Area.builder().code("SW00").name("수원").build());
        areaList.add(Area.builder().code("AS00").name("안산").build());
        areaList.add(Area.builder().code("BS00").name("부산").build());
        areaList.add(Area.builder().code("DK00").name("대구").build());
        areaList.add(Area.builder().code("US00").name("울산").build());
        areaList.add(Area.builder().code("DJ00").name("대전").build());
        areaList.add(Area.builder().code("KJ00").name("광주").build());
        areaList.add(Area.builder().code("KS00").name("경상").build());
        areaList.add(Area.builder().code("KL00").name("전라").build());
        areaList.add(Area.builder().code("CC00").name("충청").build());
        areaList.add(Area.builder().code("KW00").name("강원").build());
        areaList.add(Area.builder().code("JJ00").name("제주").build());


        return areaList;
    }

}
