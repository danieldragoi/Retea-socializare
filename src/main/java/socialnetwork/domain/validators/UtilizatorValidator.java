package socialnetwork.domain.validators;

import socialnetwork.domain.Utilizator;

public class UtilizatorValidator implements Validator<Utilizator> {
    @Override
    public void validate(Utilizator entity) throws ValidationException {
        if(entity.getLastName().length() == 0)
            throw new ValidationException("Nume gresit!");
        if(entity.getFirstName().length() == 0)
            throw new ValidationException("Prenume gresit!");

    }
}
