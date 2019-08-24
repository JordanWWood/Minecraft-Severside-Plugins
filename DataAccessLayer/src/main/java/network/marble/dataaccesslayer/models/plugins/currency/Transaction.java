package network.marble.dataaccesslayer.models.plugins.currency;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.base.BaseModel;

import java.util.List;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction extends BaseModel<Transaction> {
    public Transaction(){
        super("plugins/currency/transactions");
    }

    @Getter @Setter
    public UUID user_id;

    @Getter @Setter
    public UUID currency_id;

    @Getter @Setter
    public long amount;

    @Getter @Setter
    public long timestamp;

    @Getter @Setter
    public String description;

    public List<Transaction> GetForUser(UUID id) throws APIException {
        return getsFromURL(urlEndPoint+"/user/"+id.toString());
    }

    public List<Transaction> GetForUserBetween(UUID id, long from, long to) throws APIException {
        return getsFromURL(String.format("%s/user/%s/from/%s/to/%s",urlEndPoint, id, from, to));
    }

    public List<Transaction> GetBetween(long from, long to) throws APIException {
        return getsFromURL(String.format("%s/from/%s/to/%s",urlEndPoint, from, to));
    }

    @Override
    public Class<?> getTypeClass() {
        return Transaction.class;
    }
}
