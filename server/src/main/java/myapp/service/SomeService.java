package myapp.service;

import java.util.List;

import org.opendolphin.core.server.DTO;


public interface SomeService {
    DTO loadSomeEntity();

    void save(List<DTO> dtos);

}
