package network.marble.dataaccesslayer.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.base.BaseModel;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GlobalVariable extends BaseModel<GlobalVariable> {
    public GlobalVariable(){
        super("globalvariables");
    }

    public String name;

    public String value;

    public GlobalVariable getByName(String name) throws APIException {
        return getFromURL(urlEndPoint+"/name/"+name);
    }

    public boolean update(String name, GlobalVariable data) throws APIException {
        return _update(urlEndPoint+"/name/"+name).success;
    }

    public boolean delete(String name) throws APIException {
        return _delete(urlEndPoint+"/name/"+name).success;
    }

    @Override
    public Class<?> getTypeClass() {
        return GlobalVariable.class;
    }
}
