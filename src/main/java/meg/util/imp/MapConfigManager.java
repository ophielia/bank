package meg.util.imp;

import meg.bank.bus.BanestoCreditMapConfig;
import meg.bank.bus.BanestoMapConfig;
import meg.bank.bus.SocGenMapConfig;
import meg.bank.bus.ImportManager.ImportClient;

public class MapConfigManager {

	
	/**
	 * This method is obviously not scaleable or modular, and should be changed
	 * so that a new client doesn't require changing the business logic java
	 * class. This should be configured, not coded. Until I have time, though,
	 * this gets the job done.
	 * 
	 * @param clientkey
	 * @return
	 */
	public MapConfig getMapConfigForClient(int clientkey) {
		switch (clientkey) {
		case ImportClient.SocGen:
			return new SocGenMapConfig();
		case ImportClient.Banesto:
			return new BanestoMapConfig();
		case ImportClient.BanestoCredit:
			return new BanestoCreditMapConfig();			
		default:
			return null;
		}
	}

	
}
