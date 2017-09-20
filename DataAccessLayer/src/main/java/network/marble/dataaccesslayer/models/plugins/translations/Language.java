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

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Language extends BaseModel<Language> {
    public Language(){
        super("plugins/translations/languages", "languages", "language");
    }

    @Getter @Setter
    public String languageCode;

    @Getter @Setter
    public String name;

    @Getter @Setter
    public String skullUsername;

    public Language getbyCode(String languageCode) throws APIException {
        return getSingle(urlEndPoint+"/code/"+languageCode);
    }

    @Override
    public Class<?> getTypeClass() {
        return Language.class;
    }
}
