package repositorio;

import model.Enfermero;
import java.util.List;

public interface IRepositorio<object>{
    List<Object> getAll(String id);
    Object get(String id);
    void add(Object object);
    void update(Object object);
    void delete(Object object);
}
