package ene.eneform.service.colours.service;

import ene.eneform.domain.colours.AdditionalRaceLink;
import ene.eneform.domain.colours.BasicRaceId;
import ene.eneform.port.out.colours.AdditionalRaceLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdditionalRaceLinkService {
    private final AdditionalRaceLinkRepository arlRepository;

    public void insertAdditionalRaceLink(String source, Integer raceId, String raceName, Integer year) {
        arlRepository.findById(new BasicRaceId(source, raceId)).ifPresentOrElse(
                arl -> {
                        AdditionalRaceLink save = arl.onUpdate(raceName, year);
                    arlRepository.save(save);

                },
                () -> {
                    AdditionalRaceLink arl = AdditionalRaceLink.onCreate(source, raceId, raceName, year);
                    arlRepository.save(arl);
                }
        );
    }
    List<AdditionalRaceLink> findByRaceName(String raceName) {
        return arlRepository.findByRaceName(raceName);
    }
    List<AdditionalRaceLink> findByRaceNameAndYear(String raceName, int year) {
        return arlRepository.findByRaceNameAndYear(raceName, year);
    }
    AdditionalRaceLink findLatestByRaceName(String raceName) {
        return arlRepository.findFirstByRaceNameOrderByYearDesc(raceName);
    }
}
