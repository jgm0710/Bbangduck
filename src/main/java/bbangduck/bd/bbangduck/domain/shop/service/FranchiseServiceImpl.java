package bbangduck.bd.bbangduck.domain.shop.service;

import bbangduck.bd.bbangduck.domain.shop.entity.Franchise;
import bbangduck.bd.bbangduck.domain.shop.repository.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class FranchiseServiceImpl implements FranchiseService{
    private final FranchiseRepository franchiseRepository;
    @Override
    public List<Franchise> findAll() {
        return franchiseRepository.findAll();
    }
}
