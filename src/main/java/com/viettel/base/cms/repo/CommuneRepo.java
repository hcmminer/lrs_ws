package com.viettel.base.cms.repo;

import com.viettel.base.cms.model.Commune;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommuneRepo extends JpaRepository<Commune, Integer> {
}
