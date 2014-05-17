package meg.bank.bus;

import meg.bank.bus.dao.BankTADao;
import meg.bank.bus.repo.BankTARepository;

public class BankTransactionService {

	
	private BankTARepository transRep;

	public void deleteBankTA(Long todelete) {
		BankTADao bankta = transRep.findOne(todelete);
		bankta.setDeleted(new Boolean(true));

		transRep.save(bankta);

	}
	
}
