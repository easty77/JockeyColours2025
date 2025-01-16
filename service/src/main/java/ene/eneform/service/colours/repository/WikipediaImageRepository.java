package ene.eneform.service.colours.repository;

import ene.eneform.service.colours.domain.WikipediaImage;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WikipediaImageRepository extends CrudRepository<WikipediaImage,String> {
    List<WikipediaImage> findByOwnerIn(List<String> owners);
}
