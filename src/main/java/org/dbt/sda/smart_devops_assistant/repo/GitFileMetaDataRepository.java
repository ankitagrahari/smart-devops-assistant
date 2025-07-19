package org.dbt.sda.smart_devops_assistant.repo;

import org.dbt.sda.smart_devops_assistant.dto.GitFileMetaDataDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GitFileMetaDataRepository extends JpaRepository<GitFileMetaDataDTO, Long> {
}
