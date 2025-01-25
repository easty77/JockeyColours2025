package ene.eneform.port.out.ebay;

import ene.eneform.domain.ebay.EbayRacecard;
import ene.eneform.port.ReadWriteRepository;

import java.util.List;

public interface EbayRacecardRepository extends ReadWriteRepository<EbayRacecard, Integer> {
        List<EbayRacecard> searchRacecards(String title, String course, String country, String meeting, Integer dayOffset, String dayType,
                                           String seller, String status, String saleType, String articleType, Integer year);

}
