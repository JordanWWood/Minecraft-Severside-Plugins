package network.marble.dataaccesslayer.models.plugins.moderation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import network.marble.dataaccesslayer.base.DataAccessLayer;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.base.BaseModel;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rank extends BaseModel<Rank> {
    public Rank(){
        super("plugins/moderation/ranks");
    }

    @Getter @Setter
    public String name;

    @Getter @Setter
    public String prefix;

    @Getter @Setter
    public String suffix;

    @Getter @Setter
    public UUID parent_id;

    @Getter @Setter
    public boolean isPriority;

    @Getter @Setter
    public List<String> permissions;

    public Rank getFull(UUID id) throws APIException {
        return getFromURL(urlEndPoint+"/"+id.toString()+"/full");
    }

    public int getVoteWeight() {
        Rank fullRank;
        int weight = 1;

        try {
            fullRank = getFull(id);
        } catch (APIException e) {
            DataAccessLayer.instance.logger.warning("Error getting the full rank from ID " + id.toString());
            return weight;
        }

        List<String> relevantPermissions = fullRank.getPermissions().stream().filter(s -> s.toLowerCase().startsWith("mg.voteweight.")).collect(Collectors.toList());

        if (!relevantPermissions.isEmpty()) {
            for (String relevantPermission : relevantPermissions) {
                try {
                    int currentWeight = Integer.parseInt(relevantPermission.replace("mg.voteweight.", ""));

                    if (currentWeight > weight) {
                        weight = currentWeight;
                    }
                } catch (NumberFormatException e) {
                    DataAccessLayer.instance.logger.warning("Error parsing the permission's vote weight.\nPermission node: " + relevantPermission);
                }
            }
        }

        return weight;
    }

    @Override
    public Class<?> getTypeClass() {
        return Rank.class;
    }
}
