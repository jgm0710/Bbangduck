package bbangduck.bd.bbangduck.domain.shop.service;

import bbangduck.bd.bbangduck.domain.shop.entity.Franchise;
import bbangduck.bd.bbangduck.domain.shop.repository.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FranchiseService {
  private final FranchiseRepository franchiseRepository;

  public List<Franchise> findAll() {
    return franchiseRepository.findAll();
  }

  public Franchise getById(Long id) {
      return franchiseRepository.findById(id).orElseThrow();
  }
}
