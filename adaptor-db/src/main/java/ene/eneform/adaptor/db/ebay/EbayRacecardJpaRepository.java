package ene.eneform.adaptor.db.ebay;

import ene.eneform.domain.ebay.EbayRacecard;
import ene.eneform.port.out.ebay.EbayRacecardRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface EbayRacecardJpaRepository extends EbayRacecardRepository, JpaRepository<EbayRacecard, Integer> {

        @Query(value = """

                select d1.* from
                          (
                          select a.ebay_article_id, i.ebay_item_id, ebay_article_type,
                          ebay_title, date(a.ebay_timestamp) as article_start_date, ebay_seller, 
                        ebay_nr_images,
                          ebay_condition,
                          i.ebay_start_date, i.ebay_end_date,
                          i.ebay_sale_type, i.ebay_status,
                          coalesce(i.ebay_end_price, i.ebay_start_price) as price, i.ebay_nr_bids, i.ebay_end_price, i.ebay_listing_nr,
                          ebay_course, ebay_meeting, ebay_meeting_date, ebay_year, ebay_nr_racecards, i.ebay_url
                          from
                          ebay_articles a inner join ebay_items i on a.ebay_article_id = i.ebay_article_id
                          inner join
                          ebay_racecards on a.ebay_article_id=ebay_racecard_id
                          where
                          (:title='' or
                           locate(lower(:title), lower(ebay_title)) > 0)
                          and (:country='' or
                           locate(lower(:country), lower(ebay_title)) > 0)
                          and (:course='' or
                              lower(:course) = lower(ebay_course))
                          and (:meeting='' or
                               lower(:meeting) = lower(ebay_meeting))
                          and (:dayOffset is null or
                              (:dayType = 'ebay_start_date'
                                  and date(ebay_start_date) = date_add(current_date, interval :dayOffset day))
                             or (:dayType = 'ebay_end_date'
                                  and date(ebay_end_date) = date_add(current_date, interval :dayOffset day))
                             or (:dayType = 'ebay_timestamp'
                                  and date(a.ebay_timestamp) = date_add(current_date, interval :dayOffset day))
                          )
                          and (:seller='' or
                              :seller = ebay_seller)
                          and (:status='' or
                              :status  = ebay_status)
                          and (:saleType='' or
                               :saleType  = ebay_sale_type)
                          and (:articleType='' or
                              :articleType = ebay_article_type)
                          and (:year is null or
                              :year = ebay_year)
                          ) d1 left outer join ebay_items x on d1.ebay_article_id = x.ebay_article_id and d1.ebay_end_date > x.ebay_end_date 
                              and x.ebay_status= d1.ebay_status and x.ebay_status='Unsold'
                          where
                          x.ebay_item_id is null
                """,
                nativeQuery = true)
        List<EbayRacecard> searchRacecards(String title, String course, String country, String meeting, Integer dayOffset, String dayType,
                                           String seller, String status, String saleType, String articleType, Integer year);

}
