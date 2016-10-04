package meg.bank.bus.imp;

import meg.bank.bus.imp.BanestoCreditFileConfig;
import meg.bank.bus.imp.BanestoFileConfig;
import meg.bank.bus.imp.SocGenFileConfig;
import meg.bank.util.imp.FileConfig;
import meg.bank.util.imp.FileConfigManager;



public class FileConfigManagerImpl implements FileConfigManager {

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
	public FileConfig getFileConfigForClient(int clientkey) {
		switch (clientkey) {
		case ImportManager.ImportClient.SocGen:
			return new SocGenFileConfig();
		case ImportManager.ImportClient.Banesto:
			return new BanestoFileConfig();
		case ImportManager.ImportClient.BanestoCredit:
			return new BanestoCreditFileConfig();			
		default:
			return null;
		}
	}


}
