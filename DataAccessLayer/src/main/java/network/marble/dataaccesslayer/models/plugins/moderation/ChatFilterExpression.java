package network.marble.dataaccesslayer.models.plugins.moderation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import network.marble.dataaccesslayer.models.base.BaseModel;

import java.util.UUID;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatFilterExpression extends BaseModel<ChatFilterExpression> {
    public ChatFilterExpression() {
        super("plugins/moderation/chatfilterexpressions");
    }

    @Getter @Setter
    public String name;

    @Getter @Setter
    public String expression;

    @Getter @Setter
    public UUID user_id;

    @Getter @Setter
    public long created_on;

    @Getter @Setter
    public boolean active;

    @Override
    public Class<?> getTypeClass() {
        return ChatFilterExpression.class;
    }
}
