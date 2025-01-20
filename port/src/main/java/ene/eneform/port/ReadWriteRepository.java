package ene.eneform.port;

import java.util.List;
import java.util.Optional;

public interface ReadWriteRepository<T, ID> {

    <S extends T> S save(S entity);

    <S extends T> S saveAndFlush(S entity);

    Optional<T> findById(ID id);

    boolean existsById(ID id);

    List<T> findAllById(Iterable<ID> ids);

    List<T> findAll();

    void delete(T entity);

    void deleteById(ID id);

    void flush();
}
