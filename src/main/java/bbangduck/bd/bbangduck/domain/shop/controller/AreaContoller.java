package bbangduck.bd.bbangduck.domain.shop.controller;

import bbangduck.bd.bbangduck.domain.shop.entity.Area;
import bbangduck.bd.bbangduck.domain.shop.service.AreaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/area")
@RequiredArgsConstructor
public class AreaContoller {
    private final AreaService areaService;

    @GetMapping("/")
    public ResponseEntity<List<Area>> findAreaList() {
        List<Area> list = this.areaService.findAll();
        return ResponseEntity.ok().body(list);
    }
}
