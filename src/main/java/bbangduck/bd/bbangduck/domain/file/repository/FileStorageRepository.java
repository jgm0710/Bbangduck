package bbangduck.bd.bbangduck.domain.file.repository;

import bbangduck.bd.bbangduck.domain.file.entity.FileStorage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 작성자 : 정구민 <br><br>
 *
 * FileStorage 에 저장된 파일 정보를 관리하기 위한 Dao
 */
public interface FileStorageRepository extends JpaRepository<FileStorage, Long> {

    Optional<FileStorage> findByFileName(String fileName);
}
