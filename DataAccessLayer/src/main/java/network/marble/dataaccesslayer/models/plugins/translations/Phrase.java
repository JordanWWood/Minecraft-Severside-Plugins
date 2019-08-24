package network.marble.dataaccesslayer.models.plugins.translations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.base.BaseModel;
import okhttp3.Request;

import java.util.HashMap;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Phrase extends BaseModel<Phrase> {
    public Phrase(){
        super("plugins/translations/phrases");
    }

    @Getter @Setter
    public String description;

    @Getter @Setter
    public String name;

    @Getter @Setter
    public String preview;

    @Getter @Setter
    public HashMap<String, String> translations;

    public Phrase getbyName(String name) throws APIException {
        return getFromURL(urlEndPoint+"/name/"+name);
    }

    public String getTranslation(String name, String languageCode) throws APIException {
        Request r = context.getRequest(String.format("%s/translation/%s/%s",urlEndPoint, name, languageCode));
        return context.executeRequest(r);
    }

    @Override
    public Class<?> getTypeClass() {
        return Phrase.class;
    }
}
