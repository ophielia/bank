package meg.bank.bus.imp;

import java.util.Date;

import meg.bank.bus.dao.BankTADao;
import meg.bank.util.imp.AbstractMappingHelper;
import meg.bank.util.imp.Placeholder;

public class SocGenMappingHelper extends AbstractMappingHelper {

	public void doManualMapping(Object mapped, Placeholder placeholder) {
		BankTADao bank = (BankTADao) mapped;
		bank.setHascat(new Boolean(false));
		bank.setImportdate(new Date());
		bank.setSource(new Integer(ImportManager.ImportClient.SocGen));
	}

}
