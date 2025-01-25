package ene.eneform.domain.ebay;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name="ebay_items")
@Data
@Getter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor(access= AccessLevel.PROTECTED, force=true)
public class EbayRacecard {
    @Id
    @Column(name="ebay_item_id")
    private final Integer id;
    @Column(name="ebay_article_id")
    Integer articleId;
    @Column(name="ebay_title")
    String title;
    @Column(name="article_start_date")
    LocalDate articleStartDate;
    @Column(name="ebay_eller")
    String seller;
    @Column(name="ebay_nr_images")
    Integer imageCount;
    @Column(name="ebay_condition")
    String condition;
    @Column(name="ebay_start_date")
    LocalDate itemStartDate;
    @Column(name="ebay_end_date")
    LocalDate itemEndDate;
    @Column(name="ebay_sale_type")
    String saleType;
    @Column(name="ebay_status")
    String status;
    @Column(name="price")
    Double  price;
    @Column(name="ebay_end_price")
    Double priceEnd;
    @Column(name="ebay_nr_bids")
    Integer bidsCount;
    @Column(name="ebay_listing_nr")
    Integer listingNr;
    @Column(name="ebay_course")
    String course;
    @Column(name="ebay_meeting")
    String meeting;
    @Column(name="ebay_meeting_date")
    LocalDate meetingDate;
    @Column(name="ebay_year")
    Integer year;
    @Column(name="ebay_nr_racecards")
    Integer racecardCount;
    @Column(name="ebay_url")
    String url;
 }
