package ene.eneform.adaptor.db.smartform2025;

import ene.eneform.domain.smartform2025.Entry;
import ene.eneform.domain.smartform2025.EntryId;
import ene.eneform.port.out.smartform2025.EntryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface EntryJpaRepository extends EntryRepository, JpaRepository<Entry, EntryId> {

    @Query("FROM Entry u JOIN FETCH u.race WHERE u.id.raceId = :raceId")
    List<Entry> findEntriesByRaceId(@Param("raceId") Integer raceId);
}
