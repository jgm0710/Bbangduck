package bbangduck.bd.bbangduck.domain.file;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileStorageRepository extends JpaRepository<FileStorage, Long> {

    Optional<FileStorage> findByFileName(String fileName);
}
