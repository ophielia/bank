package meg.bank.web.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import meg.bank.bus.ExpenseCriteria;
import meg.bank.bus.RuleAssignment;
import meg.bank.bus.dao.BankTADao;
import meg.bank.bus.dao.ExpenseDao;
import meg.bank.bus.imp.ImportManager;



public class AssignmentListModel implements Serializable {

	private static final long serialVersionUID = 1L;
 	

	
	private List<RuleAssignment> assignbycategory;
	List<Boolean> checked;
	
	
	public AssignmentListModel(List<RuleAssignment> ruleassignments) {
		super();
		setRuleAssignments(ruleassignments);
	}
	
	public List<RuleAssignment> getRuleAssignments() {
		return assignbycategory;
	}

	public void setRuleAssignments(List<RuleAssignment> ruleassignment) {
		this.assignbycategory = ruleassignment;
		if (ruleassignment != null) {
			// get total number of transactions
			int total = 0;
			for (RuleAssignment rule : ruleassignment) {
				total += rule.getTransactionCount();
			}
			checked = new ArrayList<Boolean>();
			for (int i = 0; i < total; i++) {
				checked.add(true);
			}
		}
	}
	
	public List<Boolean> getChecked() {
		return checked;
	}
	public void setChecked(List<Boolean> checked) {
		this.checked = checked;
	}
	
	public List<RuleAssignment> getCheckedRuleAssignments() {
		List<RuleAssignment> chkdassignments = new ArrayList<RuleAssignment>();

		// initialize counter
		int i = 0;
		// loop through existing assignments
		for (RuleAssignment assign : assignbycategory) {
			// initialize list of selectedtrans
			List<BankTADao> selectedtrans = new ArrayList<BankTADao>();
			// loop through transactions
			for (BankTADao banktrans : assign.getTransactions()) {
				// check counter against checked list
				boolean ischecked = this.checked.get(i);

				if (ischecked) {
					// if checked, add to selected trans
					selectedtrans.add(banktrans);
				}
				// after every transaction increment counter
				i++;
			}
			// end transaction loop

			// if selectedtrans has transactions, make new RuleAssignment
			if (selectedtrans.size() > 0) {
				// set category, and transactions
				RuleAssignment newassign = new RuleAssignment(
						assign.getCategory());
				newassign.setTransactions(selectedtrans);
				// add to chkdassignments
				chkdassignments.add(newassign);
			}

		}
		// end ruleassignment loop

		// return checkedassignments
		return chkdassignments;
	}

	
	
	

	
	


}
