package bbangduck.bd.bbangduck.domain.shop.controller;

import bbangduck.bd.bbangduck.domain.shop.dto.controller.FranchiseCreateDto;
import bbangduck.bd.bbangduck.domain.shop.entity.Franchise;
import bbangduck.bd.bbangduck.domain.shop.service.FranchiseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/franchise")
@RequiredArgsConstructor
public class FranchiseController {

  private final FranchiseService franchiseService;

  @GetMapping
  public ResponseEntity<List<Franchise>> franchiseFindList() {
    List<Franchise> list = this.franchiseService.findAll();

    return ResponseEntity.ok().body(list);
  }

  @PostMapping
  public ResponseEntity<Franchise> createFranchise(@RequestBody FranchiseCreateDto franchiseCreateDto) {
    return null;
  }
}
