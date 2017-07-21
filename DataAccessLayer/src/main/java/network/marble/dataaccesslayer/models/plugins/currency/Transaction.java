package network.marble.dataaccesslayer.models.plugins.currency;


import lombok.Getter;
import lombok.Setter;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.base.BaseModel;
import okhttp3.Request;

import java.util.List;
import java.util.UUID;

public class Transaction extends BaseModel<Transaction> {
    public Transaction(){
        super("plugins/currency/transactions", "transactions", "transaction");
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
        return getMultiple(urlEndPoint+"/user/"+id.toString());
    }

    public List<Transaction> GetForUserBetween(UUID id, long from, long to) throws APIException {
        return getMultiple(String.format("%s/user/%s/from/%s/to/%s",urlEndPoint, id, from, to));
    }

    public List<Transaction> GetBetween(long from, long to) throws APIException {
        return getMultiple(String.format("%s/from/%s/to/%s",urlEndPoint, from, to));
    }

    @Override
    public Class<?> getTypeClass() {
        return Transaction.class;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "user_id=" + user_id +
                ", currency_id=" + currency_id +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                ", description='" + description + '\'' +
                "} " + super.toString();
    }
}
