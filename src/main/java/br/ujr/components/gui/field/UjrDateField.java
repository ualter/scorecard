package br.ujr.components.gui.field;

import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.ujr.components.gui.Commons;

/**
 * Date Field
 * 
 * KeyStrokes:
 *  SPACE    = Today Date
 *  CTRL +  > = Previous Day 
 *  CTRL +  < = Next     Day
 *  SHIFT + > = Previous Month 
 *  SHIFT + < = Next     Month
 *  
 * @author Ualter
 *
 */
public class UjrDateField extends AbstractUjrField {
	
	private static final long serialVersionUID = 1L;
	
	public UjrDateField() {
	}
	
	public UjrDateField(int x, int y) {
		super();
		this.setBounds(x, y, 86, 20);
	}
	
	public void keyReleased(KeyEvent e) {
		//System.out.println("lastKeyCode "+ this.lastKeyCode + ", Modifiers " + e.getModifiers());
		if (this.lastKeyCode == 44 && this.getText().length() >= 10 && e.getModifiers() == 2) {
			this.goTo(-1);
		} else
		if (this.lastKeyCode == 46 && this.getText().length() >= 10 && e.getModifiers() == 2) {
			this.goTo(1);	
		} else
		if (this.lastKeyCode == 44 && this.getText().length() >= 10 && e.getModifiers() == 1) {
			this.goTo(-30);
		} else
		if (this.lastKeyCode == 46 && this.getText().length() >= 10 && e.getModifiers() == 1) {
			this.goTo(30);	
		} else	
		if ( this.lastKeyCode == KeyEvent.VK_BACK_SPACE && this.getLastCharacther() == '/') {
			int length = this.getText().length();
			if ( length > 3 ) {
				this.setText(this.getText().substring(0,5));
				this.select(3, 5);
			} else {
				this.setText(this.getText().substring(0,2));
				this.select(0, 2);
			}
		}
	}
	
	public void goTo(int days) {
		Date date = this.getValueAsDate();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, days);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		this.setText(sdf.format(cal.getTime()));
		this.select(0, this.getText().length());
	}
	
	public void keyTyped(KeyEvent e) {
		if ( this.lastKeyCode == KeyEvent.VK_SPACE && this.getText().length() == 0 ) {
			this.setText(this.getToday());
			this.select(0, this.getText().length());
			e.consume();
		} else
		if ( !isEditKey(this.lastKeyCode) && !Commons.isNumber(this.lastKeyChar) ) {
			e.consume();
		} else 
		if ( this.getText().length() == 10 && !isEditKey(this.lastKeyCode)
				&& (this.getSelectedText() == null || this.getSelectedText().length() < 10)) {
			e.consume();
		} else {
			this.format(e);
		}
	}
	
	public int[] getMembers() {
		if ( this.getText().length() == 10 ) {
			int day = Integer.parseInt(this.getText().substring(0,2));
			int month = Integer.parseInt(this.getText().substring(3,5));
			int year  = Integer.parseInt(this.getText().substring(7));
			return new int[] {day,month,year};
		} else {
			return null;
		}
	}
	public Date getValueAsDate() {
		if ( this.getText().length() == 10 ) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			try {
				return sdf.parse(this.getText().trim());
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}
	
	private String getToday() {
		DecimalFormat df2 = new DecimalFormat("00");
		DecimalFormat df4 = new DecimalFormat("0000");
		int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		int month = Calendar.getInstance().get(Calendar.MONTH);
		int year = Calendar.getInstance().get(Calendar.YEAR);
		return df2.format(day) + "/" + df2.format(month+1) + "/" + df4.format(year);
	}
	private void format(KeyEvent e) {
		if ( this.getSelectedText() == null ) {
			int size = this.getText().length();
			if ( size == 2 ) {
				if ( isValidDay() ) {
					if ( this.lastKeyCode != KeyEvent.VK_BACK_SPACE ) {
						this.setText(this.getText() + "/");
					} 
				} else {
					e.consume();
					System.out.println("Dia inválido!");
					this.select(0, 2);
				}
			} else
			if ( size == 5 ) {
				if ( isValidMonth() ) {
					if ( this.lastKeyCode != KeyEvent.VK_BACK_SPACE ) {
						this.setText(this.getText() + "/");
					}
				} else {
					e.consume();
					System.out.println("Mês inválido!");
					this.select(3, 5);
				}
			}
		}
	}
	
	private boolean isValidDay() {
		if ( this.getText().length() > 2) {
			int day = Integer.parseInt(this.getText().substring(0,2));
			if ( day >= 1 && day <= 31 ) {
				return true;
			}
			return false;
		} else {
			return true;
		}
	}
	private boolean isValidMonth() {
		if (this.getText().length() > 5) {
			int month = Integer.parseInt(this.getText().substring(3,5));
			if ( month >= 1 && month <= 12 ) {
				return true;
			}
			return false;
		} else {
			return true;
		}
	}

}
