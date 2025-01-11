package ene.eneform.smartform2025.repositories;

import ene.eneform.smartform2025.entities.DailyRace;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AdditionalRaceMonthItemRepository extends CrudRepository<DailyRace.AdditionalRaceMonthItem, String> {

        @Query(value = """
                select Date, Course, Name,Race_Type,Wikipedia, fr_wikipedia,Winner,DOW,GS,Year,Country,Track, Furlongs, Grade,Age, Sex, Month,Class, Hcap,SE 
                from
                ( 
                select d.meeting_date as Date, ard_course as Course, ard_name as Name,
                ard_race_type as Race_Type,
                en.arw_wikipedia_ref as Wikipedia, fr.arw_wikipedia_ref as fr_wikipedia,
                coalesce(name, cast(d.race_id as char)) as Winner,
                case when ard_dow = dayofweek(d.meeting_date) then dayname(d.meeting_date) else concat(dayname(d.meeting_date), ard_dow) end as DOW,
                ard_GS_ref as GS,
                ard_start_year as Year,
                ard_country as Country,
                ard_track_type as Track, cast(ard_distance_yards/220.0 as decimal(3,1)) as Furlongs, ard_group_race as Grade,\s
                ard_age_range as Age, ard_sex as Sex, ard_month as Month,
                ard_class as Class, ard_handicap as "Hcap",
                ard_se_key as "SE"
                from
                ((((((
                daily_races d inner join additional_race_link on d.race_id=arl_race_id and arl_source='SF')
                left outer join historic_races h on d.race_id=h.race_id)
                left outer join historic_runners hru on h.race_id=hru.race_id and coalesce(hru.amended_position, hru.finish_position, 0)=1)
                inner join additional_race_data on arl_name = ard_name)
                left outer join additional_race_wikipedia en on ard_name=en.arw_name and en.arw_language='en')
                left outer join additional_race_wikipedia fr on ard_name=fr.arw_name and fr.arw_language='fr')
                where
                    (
                    month(d.meeting_date) = month(date_sub(current_date, interval :monthOffset month))
                        and year(d.meeting_date)=year(current_date)
                    )   
                    or
                    (
                    year(d.meeting_date) = year(current_date) - 1 and month(d.meeting_date) = 13 - :monthOffset
                    )
                group by ard_name, ard_country, ard_course, ard_race_type, ard_track_type
                union
                select d.meeting_date as Date, ard_course as Course, ard_name as Name,
                ard_race_type as Race_Type,
                en.arw_wikipedia_ref as Wikipedia, fr.arw_wikipedia_ref as fr_wikipedia,
                coalesce(name, cast(d.race_id as char)) as Winner,
                case when ard_dow = dayofweek(d.meeting_date) then dayname(d.meeting_date) else concat(dayname(d.meeting_date), ard_dow) end as DOW,
                ard_GS_ref as GS,
                ard_start_year as Year,
                ard_country as Country,
                ard_track_type as Track, cast(ard_distance_yards/220 as unsigned) as Furlongs, ard_group_race as Grade,\s
                ard_age_range as Age, ard_sex as Sex, ard_month as Month,
                ard_class as Class, ard_handicap as "Hcap",
                ard_se_key as "SE"
                from
                (((((
                additional_races d inner join additional_race_link on d.race_id=arl_race_id and arl_source=ara_source)
                left outer join additional_runners hru on d.race_id=hru.race_id and hru.finish_position=1)
                inner join additional_race_data on arl_name = ard_name)
                left outer join additional_race_wikipedia en on ard_name=en.arw_name and en.arw_language='en')
                left outer join additional_race_wikipedia fr on ard_name=fr.arw_name and fr.arw_language='fr')
                where
                    (
                    month(d.meeting_date) = month(date_sub(current_date, interval :monthOffset month))
                        and year(d.meeting_date)=year(current_date)
                    ) 
                    or
                    (
                    year(d.meeting_date) = year(current_date) - 1 and month(d.meeting_date) = 13 - :monthOffset
                    )
                group by ard_name, ard_country, ard_course, ard_race_type, ard_track_type
                )
                """,
                nativeQuery = true)
        List<DailyRace.AdditionalRaceMonthItem> findCurrentMonthRaces(int monthOffset);

}
