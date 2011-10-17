/**
 * 
 */
package m.cameron.android.tipcalculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author mark.cameron
 *
 */
public class TipManager {
	private BigDecimal tipPercent;
	private BigDecimal totalBeforeTip;
	private BigDecimal numberOfPeople;
	private BigDecimal tipDecimal;
	private BigDecimal tipAmount;
	private BigDecimal tipAmountPerPerson;
	private BigDecimal totalWithTip;
	private BigDecimal totalWithTipPerPerson;
	
	public BigDecimal getTipAmount() {
		return tipAmount;
	}

	public BigDecimal getTipAmountPerPerson() {
		return tipAmountPerPerson;
	}

	public BigDecimal getTotalWithTip() {
		return totalWithTip;
	}

	public BigDecimal getTotalWithTipPerPerson() {
		return totalWithTipPerPerson;
	}

	private void setTipDecimal() {
		final BigDecimal HUNDRED = new BigDecimal("100");
		
		this.tipDecimal = this.tipPercent.divide(HUNDRED, 2, RoundingMode.HALF_UP);
	}

	public BigDecimal getTipPercent() {
		return tipPercent;
	}
	
	public void setTipPercent(String tipPercent) {
		this.tipPercent = new BigDecimal(tipPercent);
		this.setTipDecimal();
	}
	
	public BigDecimal getTotalBeforeTip() {
		return totalBeforeTip;
	}
	
	public void setTotalBeforeTip(String totalBeforeTip) {
		this.totalBeforeTip = new BigDecimal(totalBeforeTip);
	}
	
	public BigDecimal getNumberOfPeople() {
		return numberOfPeople;
	}
	
	public void setNumberOfPeople(String numberOfPeople) {
		this.numberOfPeople = new BigDecimal(numberOfPeople);
	}
	
	private boolean valuesIsNotNull() {
		if (this.totalBeforeTip.equals(null)) {
			return false;
		}
		if (this.tipPercent.equals(null)) {
			return false;
		}
		if (this.numberOfPeople.equals(null)) {
			return false;
		}
		
		return true;
	}
	
	public void calculateTip() {
		if (this.valuesIsNotNull()) {
			tipAmount = totalBeforeTip.multiply(tipDecimal);
	    	tipAmountPerPerson = tipAmount.divide(numberOfPeople, 2, RoundingMode.HALF_UP);
	    	totalWithTip = tipAmount.add(totalBeforeTip);
	    	totalWithTipPerPerson = totalWithTip.divide(numberOfPeople, 2, RoundingMode.HALF_UP);			
		}
	}
}