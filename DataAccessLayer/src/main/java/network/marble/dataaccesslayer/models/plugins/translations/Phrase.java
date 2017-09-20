package network.marble.dataaccesslayer.models.plugins.translations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.base.BaseModel;
import okhttp3.Request;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Phrase extends BaseModel<Phrase> {
    public Phrase(){
        super("plugins/translations/phrases", "phrases", "phrase");
    }

    @Getter @Setter
    public String description;

    @Getter @Setter
    public String name;

    @Getter @Setter
    public HashMap<String, String> translations;

    public Phrase getbyName(String name) throws APIException {
        return getSingle(urlEndPoint+"/name/"+name);
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
