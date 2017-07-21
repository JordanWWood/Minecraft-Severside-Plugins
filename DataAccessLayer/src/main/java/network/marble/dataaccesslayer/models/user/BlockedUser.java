package network.marble.dataaccesslayer.models.user;

import java.util.UUID;

public class BlockedUser {
	UUID user_id;
	long blocked_at;

	@Override
	public String toString() {
		return "BlockedUser{" +
				"user_id=" + user_id +
				", blocked_at=" + blocked_at +
				'}';
	}
}
