package ene.eneform.port.in.smartform2025;

import ene.eneform.port.out.smartform2025.model.Declaration;

import java.util.List;

public interface DeclarationServiceInterface {
    List<Declaration> findDeclarationsByRaceId(Integer raceId);
}
