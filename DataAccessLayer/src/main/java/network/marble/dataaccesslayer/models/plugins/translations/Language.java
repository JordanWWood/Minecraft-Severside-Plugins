package network.marble.dataaccesslayer.models.plugins.translations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.base.BaseModel;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Language extends BaseModel<Language> {
    public Language(){
        super("plugins/translations/languages");
    }

    @Getter @Setter
    public String languageCode;

    @Getter @Setter
    public String name;

    @Getter @Setter
    public String skullUri;

    public Language getbyCode(String languageCode) throws APIException {
        return getFromURL(urlEndPoint+"/code/"+languageCode);
    }

    @Override
    public Class<?> getTypeClass() {
        return Language.class;
    }
}
