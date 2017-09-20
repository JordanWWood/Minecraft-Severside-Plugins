package network.marble.moderationslave.tasks;

import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.plugins.moderation.ChatFilterExpression;
import network.marble.moderationslave.listeners.PlayerListener;

public class FilterUpdater implements Runnable{

	@Override
	public void run() {
		try {
			PlayerListener.regexStrings.clear();
			new ChatFilterExpression().get().forEach(f -> PlayerListener.regexStrings.add(f.expression));
		} catch (APIException e) {
			e.printStackTrace();
		}
	}
}
