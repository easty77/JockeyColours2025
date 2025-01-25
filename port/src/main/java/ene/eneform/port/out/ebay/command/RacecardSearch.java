package ene.eneform.port.out.ebay.command;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RacecardSearch {
    String title="";
    String country="";
    String course="";
    String meeting="";
    Integer dayOffset=null;
    String dayType="";
    Integer year=null;
    String status="";
    String seller="";
    String saleType="";
    String articleType="";
}
