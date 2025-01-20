package ene.eneform.port.out.colours;

import ene.eneform.domain.colours.UnregisteredColourSyntax;
import ene.eneform.port.ReadWriteRepository;


public interface UnregisteredColourSyntaxRepository extends ReadWriteRepository<UnregisteredColourSyntax, String> {
UnregisteredColourSyntax findByIdColoursAndIdOrganisationAndIdYear(String colours, String organisation, int year);
}
