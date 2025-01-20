package ene.eneform.service.colours.service;

import ene.eneform.domain.colours.UnregisteredColourSyntax;
import ene.eneform.port.out.colours.UnregisteredColourSyntaxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnregisteredColourSyntaxService {
    private final UnregisteredColourSyntaxRepository ucsRepository;

    UnregisteredColourSyntax findByColours(String colours) {
        return ucsRepository.findByIdColoursAndIdOrganisationAndIdYear(colours, "UK", 0);
    }
}
