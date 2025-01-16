package ene.eneform.service.colours.repository;

import ene.eneform.service.colours.domain.UnregisteredColourSyntax;
import org.springframework.data.repository.CrudRepository;

public interface UnregisteredColourSyntaxRepository extends CrudRepository<UnregisteredColourSyntax, String> {
UnregisteredColourSyntax findByIdColoursAndIdOrganisationAndIdYear(String colours, String organisation, int year);
}
