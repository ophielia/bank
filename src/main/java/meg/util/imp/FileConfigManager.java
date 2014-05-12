package meg.util.imp;

import meg.bank.bus.BanestoCreditFileConfig;
import meg.bank.bus.BanestoFileConfig;
import meg.bank.bus.SocGenFileConfig;
import meg.bank.bus.ImportManager.ImportClient;

public class FileConfigManager {

	/**
	 * This method is obviously not scaleable or modular, and should be changed
	 * so that a new client doesn't require changing the business logic java
	 * class. This should be configured, not coded. Until I have time, though,
	 * this gets the job done.
	 * 
	 * @param clientkey
	 * @return
	 */
	public FileConfig getFileConfigForClient(int clientkey) {
		switch (clientkey) {
		case ImportClient.SocGen:
			return new SocGenFileConfig();
		case ImportClient.Banesto:
			return new BanestoFileConfig();
		case ImportClient.BanestoCredit:
			return new BanestoCreditFileConfig();			
		default:
			return null;
		}
	}


}
