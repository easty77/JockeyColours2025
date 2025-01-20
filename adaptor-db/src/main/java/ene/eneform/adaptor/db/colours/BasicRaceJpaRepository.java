package ene.eneform.adaptor.db.colours;

import ene.eneform.domain.colours.BasicRace;
import ene.eneform.domain.colours.BasicRaceId;
import ene.eneform.port.out.colours.BasicRaceRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface BasicRaceJpaRepository extends BasicRaceRepository,  JpaRepository<BasicRace, Integer> {
    @NativeQuery("""
            select * from
            (select arl_source, arl_race_id, meeting_date, course, race_type, distance_yards, going, race_name, group_race, handicap, num_runners, direction
            from additional_race_link
            inner join historic_races da on da.race_id=arl_race_id
            inner join daily_runners du on da.race_id=du.race_id and ru.name=:horseName and ru.bred=:bred
            where arl_source='SF'
            union
            select arl_source, arl_race_id, meeting_date, course, race_type, distance_yards, going, race_name, group_race, handicap, num_runners, direction 
            from additional_race_link 
            inner join additional_races aa on aa.race_id=arl_race_id and arl_source=ara_source
            inner join additional_runners au on aa.race_id=au.race_id and arl_source=aru_source and au.name=:horseName and au.bred=:bred 
            ) as t1 order by meeting_date desc
            """)
    List<BasicRace> findByHorseAndBred(String horseName, String bred);

    BasicRace findById(BasicRaceId basicRaceId);
}
