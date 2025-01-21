package ene.eneform.service.mero.service;

import ene.eneform.port.in.mero.ENEColoursEnvironmentInterface;
import ene.eneform.service.mero.config.ENEColoursEnvironment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;
@Component
@Getter
@RequiredArgsConstructor
public class ENEColoursEnvironmentPublic implements ENEColoursEnvironmentInterface, Serializable {

    private final ENEColoursEnvironment environmentHandler;
    @Override
    public void reset() {
        environmentHandler.reset();
    }
}
