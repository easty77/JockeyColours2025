package ene.eneform.adaptor.db.colours;

import ene.eneform.domain.colours.WikipediaImage;
import ene.eneform.port.out.colours.WikipediaImageRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface WikipediaImageJpaRepository extends WikipediaImageRepository, JpaRepository<WikipediaImage,String> {
    List<WikipediaImage> findByOwnerIn(List<String> owners);
}
