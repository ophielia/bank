package meg.bank.bus.imp;

import meg.bank.bus.imp.SocGenMapConfig;
import meg.bank.bus.imp.BanestoMapConfig;
import meg.bank.bus.imp.BanestoCreditMapConfig;
import meg.bank.util.imp.MapConfig;
import meg.bank.util.imp.MapConfigManager;




public class MapConfigManagerImpl implements MapConfigManager {

	
	/**
	 * This method is obviously not scaleable or modular, and should be changed
	 * so that a new client doesn't require changing the business logic java
	 * class. This should be configured, not coded. Until I have time, though,
	 * this gets the job done.
	 * 
	 * @param clientkey
	 * @return
	 */
	@Override
	public MapConfig getMapConfigForClient(int clientkey) {
		switch (clientkey) {
		case ImportManager.ImportClient.SocGen:
			return new SocGenMapConfig();
		case ImportManager.ImportClient.Banesto:
			return new BanestoMapConfig();
		case ImportManager.ImportClient.BanestoCredit:
			return new BanestoCreditMapConfig();			
		default:
			return null;
		}
	}

	
}
