package ene.eneform.port.out.colours;

import ene.eneform.domain.colours.WikipediaImage;
import ene.eneform.port.ReadWriteRepository;

import java.util.List;

public interface WikipediaImageRepository extends ReadWriteRepository<WikipediaImage,String> {
    List<WikipediaImage> findByOwnerIn(List<String> owners);
}
