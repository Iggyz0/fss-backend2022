package rs.ac.singidunum.fssbackend.service;

public interface IAutoMapperService {
    <T> T map(Object model, Class<T> entity);
}
