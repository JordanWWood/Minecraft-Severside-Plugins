package network.marble.dataaccesslayer.entities.rabbit;

import com.rabbitmq.client.Channel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data
@AllArgsConstructor
public class RabbitResult {

    @Setter(AccessLevel.NONE)
    private Channel channel;

    @Setter(AccessLevel.NONE)
    private String queueName;
}
