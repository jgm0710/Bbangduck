package bbangduck.bd.bbangduck.domain.shop.service;

import bbangduck.bd.bbangduck.domain.shop.entity.Area;
import bbangduck.bd.bbangduck.domain.shop.repository.AreaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AreaServiceImpl implements AreaService{

    private final AreaRepository areaRepository;

    @Override
    public List<Area> findAll() {
        return this.areaRepository.findAll();
    }
}
