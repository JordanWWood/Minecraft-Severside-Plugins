package network.marble.dataaccesslayer.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.base.BaseModel;
import okhttp3.Request;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GlobalVariable extends BaseModel<GlobalVariable> {
    public GlobalVariable(){
        super("globalvariables", "globalvariables", "globalvariable");
    }

    public String name;

    public String value;

    public GlobalVariable getByName(String name) throws APIException {
        return getSingle(urlEndPoint+"/name/"+name);
    }

    public boolean update(String name, GlobalVariable data) throws APIException {
        return update(urlEndPoint+"/name/"+name);
    }

    public boolean delete(String name) throws APIException {
        return delete(urlEndPoint+"/name/"+name);
    }

    @Override
    public Class<?> getTypeClass() {
        return GlobalVariable.class;
    }
}
