package ene.eneform.adaptor.db.smartform2025;

import ene.eneform.domain.smartform2025.PreviousRace;
import ene.eneform.port.out.smartform2025.PreviousRaceRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface PreviousRaceJpaRepository extends PreviousRaceRepository, JpaRepository<PreviousRace, Integer> {

        @Query(value = """
    select 
        hra.race_id,
    hra.race_type,
    year(hra.meeting_date) as y,  month(hra.meeting_date) as m,  day(hra.meeting_date) as d, hra.course, cast(hra.distance_yards/220 as unsigned) as furlongs, 
    concat(case when hra.min_age is not null then cast(hra.min_age as char) else '' end, '-', case when hra.max_age is not null then cast(hra.max_age as char) else '' end) as age, 
    group_race, previous_race_id, pry.year, race_name, cast(hra.added_money/1000 as unsigned) as money,
    name, jockey_name, trainer_name, owner_name, case when dra.race_id is null then 0 else 1 end as daily, arl_name
    from 
    ((((
    historic_races hra inner join historic_runners hru on hra.race_id = hru.race_id
    and coalesce(amended_position, finish_position)=1
    and (:winner='' or
    :winner = name))
    left outer join daily_races dra on hra.race_id = dra.race_id)
    left outer join previous_race_year pry on hra.race_id=pry.race_id)
    left outer join additional_race_link on arl_source='SF' and arl_race_id=hra.race_id)
    where
    1=1
    and (:course='' or
    :course = hra.course)
   and (:grade='' or
   (
    (:grade='1' and group_race=1) or
    (:grade='2' and group_race=2) or
    (:grade='3' and group_race=3) or
    (:grade not in ('1', '2','3') and group_race is not null)
   ))
    order by year(hra.meeting_date)
    """,
                nativeQuery = true)
        List<PreviousRace> searchPreviousRaces(String title, String course, String raceType, String winner,
                                               Integer year, Integer quarter, Integer month,
                                               Integer month1, Integer month2, String half,
                                               String grade, Integer age,
                                           Integer money, Integer furlongs);

}
