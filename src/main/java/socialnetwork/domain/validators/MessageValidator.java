package socialnetwork.domain.validators;

import socialnetwork.domain.Message;


public class MessageValidator implements Validator<Message>{

    @Override
    public void validate(Message mesaj) throws ValidationException {
        if(mesaj.getFrom() == null )
            throw new ValidationException("Utilizator from invalid!");
        if(mesaj.getTo() == null)
            throw new ValidationException("Utlizatori to invalid!");
        if(mesaj.getMessage() == " "  || mesaj.getMessage() == null || mesaj.getMessage() == "")
            throw new ValidationException("Text invalid!");

    }
}
