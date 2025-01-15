package ene.eneform.smartform2025.repositories;

import ene.eneform.smartform2025.entities.Entry;
import ene.eneform.smartform2025.entities.EntryId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EntryRepository extends CrudRepository<Entry, EntryId> {

    @Query("FROM Entry u JOIN FETCH u.race WHERE u.id.raceId = :raceId")
    List<Entry> findEntriesByRaceId(@Param("raceId") Integer raceId);
}
