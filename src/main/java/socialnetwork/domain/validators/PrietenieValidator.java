package socialnetwork.domain.validators;

import socialnetwork.domain.Prietenie;

public class PrietenieValidator implements Validator<Prietenie>{

    @Override
    public void validate(Prietenie entity) throws ValidationException {
        if(entity.getId1()<0)
            throw new ValidationException("Id1 invalid!");
        if(entity.getId2()<0)
            throw new ValidationException("Id2 invalid!");
        if(entity.getId2() == entity.getId1())
            throw new ValidationException("Id1 egal cu Id2!");
    }
}
