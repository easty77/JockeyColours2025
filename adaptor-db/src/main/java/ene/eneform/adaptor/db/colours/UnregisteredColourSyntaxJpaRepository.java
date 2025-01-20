package ene.eneform.adaptor.db.colours;

import ene.eneform.domain.colours.UnregisteredColourSyntax;
import ene.eneform.port.out.colours.UnregisteredColourSyntaxRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface UnregisteredColourSyntaxJpaRepository extends UnregisteredColourSyntaxRepository, JpaRepository<UnregisteredColourSyntax, String> {
UnregisteredColourSyntax findByIdColoursAndIdOrganisationAndIdYear(String colours, String organisation, int year);
}
