package meg.bank.bus;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import meg.bank.bus.dao.BankTADao;
import meg.bank.bus.repo.BankTARepository;

public class BankTransactionService {

	
	private BankTARepository transRep;

	public void deleteBankTA(Long todelete) {
		BankTADao bankta = transRep.findOne(todelete);
		bankta.setDeleted(new Boolean(true));

		transRep.save(bankta);

	}
	
	public Date getFirstTransDate() {
		List result = null;
		Date mindate = null;

		// call transrep.getFirstTransDate
//				"select min(trans.transdate) from BankTADao as trans ");

		if (result != null && result.size() > 0) {
			// pull the id off of the result list
			mindate = (Date) result.get(0);

		}

		// check for null
		if (mindate == null) {
			Calendar cal = Calendar.getInstance();
			cal.set(1970, 10, 1);
			mindate = cal.getTime();
		}

		return mindate;
	}
	
}
