package br.ujr.scorecard.gui.view.screen;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import br.ujr.components.gui.field.JDateChooser;
import br.ujr.components.gui.field.JTextFieldDateEditor;
import br.ujr.scorecard.gui.view.utils.AbstractDialog;
import br.ujr.scorecard.util.Util;

public class PeriodoFrame extends AbstractDialog implements KeyListener, ItemListener, ChangeListener  {

	private static final long serialVersionUID = 1L;
	
	private Date periodoDataInicial;
	private Date periodoDataFinal;

	private JDateChooser txtDataInicial;
	private JDateChooser txtDataFinal;
	private JComboBox cmbMeses;
	private JComboBox cmbAno;
	private JButton btnOk;
	private JButton btnCancelar;
	
	private JSpinner spinnerMes;
	private SpinnerModel spinnerModelMes;
	
	private JSpinner spinnerAno;
	private SpinnerModel spinnerModelAno;
	
	@SuppressWarnings("unused")
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	private boolean canceled = false;
	
	public PeriodoFrame(JFrame owner, Date periodoInicial, Date periodoFinal) {
		this(owner,periodoInicial,periodoFinal,false);
	}
	public PeriodoFrame(JFrame owner, Date periodoInicial, Date periodoFinal, boolean transparent) {
		super(owner, transparent);
		
		this.width = 300;
		this.height = 200;
		this.title = "Período do Movimento";
		
		this.periodoDataInicial = periodoInicial;
		this.periodoDataFinal = periodoFinal;
		
		this.createUI();
	}
	
	protected void createUI() {
		super.createUI();
		//this.setUndecorated(true);
		int posX = 15;
		int posY = 30;
		
		Object[] data = new Object[] {
				new MesItem(1,"JANEIRO"),	
				new MesItem(2,"FEVEREIRO"),
				new MesItem(3,"MARÇO"),
				new MesItem(4,"ABRIL"),
				new MesItem(5,"MAIO"),
				new MesItem(6,"JUNHO"),
				new MesItem(7,"JULHO"),
				new MesItem(8,"AGOSTO"),
				new MesItem(9,"SETEMBRO"),
				new MesItem(10,"OUTUBRO"),
				new MesItem(11,"NOVEMBRO"),
				new MesItem(12,"DEZEMBRO"),
		};
		this.cmbMeses = new JComboBox(data);
		int widthCmbMeses   = 125;
		int widthCmbAno     = 75;
		int widthSpinnerMes = 16;
		int widthSpinnerAno = 16;
		posX = (this.getWidth() - (widthCmbMeses + widthSpinnerMes + widthSpinnerAno + widthCmbAno + 20) ) / 2;
		this.cmbMeses.setBounds(posX, posY, widthCmbMeses, 20);
		panMain.add(this.cmbMeses);
		this.cmbMeses.addKeyListener(this);
		this.cmbMeses.addItemListener(this);
		this.cmbMeses.setActionCommand("MESES");
		this.cmbMeses.setFont(new Font("Arial",Font.BOLD,16));
		
		posX += (widthCmbMeses + 0);
		
		int anoInicial = (Calendar.getInstance().get(Calendar.YEAR) - 5);
		Object[] modelAno = new Object[10];
		for (int i = 0; i < modelAno.length; i++) {
			modelAno[i] = anoInicial++;
		}
		
		this.cmbAno = new JComboBox(modelAno);
		this.spinnerModelMes = new SpinnerListMeses(data,this.cmbAno);
		this.spinnerMes = new JSpinner(this.spinnerModelMes);
		this.spinnerMes.setName("MES");
		this.spinnerMes.setBounds(posX, posY, widthSpinnerMes, 20);
		this.spinnerMes.addChangeListener(this);
		panMain.add(this.spinnerMes);
				
		posX += (widthSpinnerMes + 10);
		this.cmbAno.setBounds(posX, posY, widthCmbAno, 20);
		this.cmbAno.setSelectedItem(2007);
		this.cmbAno.addKeyListener(this);
		this.cmbAno.addItemListener(this);
		this.cmbAno.setActionCommand("ANO");
		this.cmbAno.setFont(new Font("Arial",Font.BOLD,16));
		panMain.add(this.cmbAno);
		
		posX += (widthCmbAno + 0);
		
		this.spinnerModelAno = new SpinnerListModel(modelAno);
		this.spinnerAno = new JSpinner(this.spinnerModelAno);
		this.spinnerAno.setName("ANO");
		this.spinnerAno.setBounds(posX, posY, widthSpinnerAno, 20);
		this.spinnerAno.addChangeListener(this);
		panMain.add(this.spinnerAno);
		
		posX  = 10;
		posY += 45;
		JLabel lblDe = new JLabel("De");
		lblDe.setBounds(posX, posY, 20, 20);
		panMain.add(lblDe);
		
		txtDataInicial = new JDateChooser("dd/MM/yyyy","##/##/####",'_');
		txtDataInicial.setDate(this.periodoDataInicial);
		posX+= 23;
		txtDataInicial.setBounds(posX, posY, 110, 20);
		txtDataInicial.setFont(new Font("Courier New",Font.PLAIN,13));
		txtDataInicial.addKeyListener(this);
		panMain.add(txtDataInicial);

		JLabel lblAte = new JLabel("até");
		posX += 118;
		lblAte.setBounds(posX, posY, 20, 20);
		panMain.add(lblAte);
		
		txtDataFinal = new JDateChooser("dd/MM/yyyy","##/##/####",'_');
		txtDataFinal.setDate(this.periodoDataFinal);
		posX += 28;
		txtDataFinal.setBounds(posX, posY, 110, 20);
		txtDataFinal.setFont(new Font("Courier New",Font.PLAIN,13));
		txtDataFinal.addKeyListener(this);
		panMain.add(txtDataFinal);
		
		posY += 38;
		posX = (this.getWidth() - 102) / 2;
		this.btnOk = new JButton();
		this.btnOk.setBounds(posX, posY, 50, 45);
		this.btnOk.addActionListener(this);
		this.btnOk.setActionCommand("OK");
		this.btnOk.setIcon(new ImageIcon(Util.loadImage(this, "salvar.png")));
		
		posX = ((this.getWidth() - 102) / 2) + 52;
		this.btnCancelar = new JButton();
		this.btnCancelar.setBounds(posX, posY, 50, 45);
		this.btnCancelar.addActionListener(this);
		this.btnCancelar.setActionCommand("CANCELAR");
		this.btnCancelar.setIcon(new ImageIcon(Util.loadImage(this, "cancel.png")));
		
		panMain.add(this.btnOk);
		panMain.add(this.btnCancelar);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(this.periodoDataInicial);
		int mes = cal.get(Calendar.MONTH) + 1;
		int ano = cal.get(Calendar.YEAR);
		
		//this.cmbMeses.setSelectedIndex(Integer.parseInt(new SimpleDateFormat("MM").format(this.periodoDataInicial)) - 1);
		this.cmbMeses.setSelectedItem(new MesItem(mes));
		this.cmbAno.setSelectedItem(new Integer(ano));
		this.spinnerMes.setValue(new MesItem(mes));
		this.spinnerAno.setValue(new Integer(ano));
		this.addKeyListener(this);
	}
	
	private static class MesItem {
		private int    mes;
		private String nome;
		
		public MesItem(int mes) {
			this.mes  = mes;
		}
		public MesItem(int mes, String nome) {
			this.mes  = mes;
			this.nome = nome;
		}
		public String getNome() {
			return nome;
		}
		public int getMes() {
			return mes;
		}
		@Override
		public String toString() {
			return this.nome;
		}
		@Override
		public boolean equals(Object arg0) {
			if (arg0 instanceof MesItem) {
				MesItem that = (MesItem) arg0;
				if ( this.getMes() == that.getMes() ) return true;
			}
			return false;
		}
		@Override
		public int hashCode() {
			return this.getMes();
		}
		
	}
	
	public void keyPressed(KeyEvent evt) {
	}
	public void keyReleased(KeyEvent evt) {
		int keyCode = evt.getKeyCode();
		switch (keyCode) {
		case 27:
			this.canceled = true;
			this.dispose();
			break;
		case 10:
			if ( evt.getSource() instanceof JTextFieldDateEditor ) {
				this.commitValues();
			} else
			if ( evt.getSource() instanceof JComboBox ) {
				this.commitValues();
			}
			break;
		default:
			break;
		}
	}

	private void commitValues() {
		if ( this.checkDates() ) {
			this.periodoDataInicial = this.txtDataInicial.getDate();
			this.periodoDataFinal = this.txtDataFinal.getDate();
			this.dispose();
		}
	}
	
	private boolean checkDates() {
		if ( this.txtDataInicial.getDate() == null ) {
			JOptionPane.showMessageDialog(this,"Data inicial do período inválida !","Atenção",JOptionPane.ERROR_MESSAGE);
			this.txtDataInicial.requestFocus();
			return false;
		} 
		if ( this.txtDataFinal.getDate() == null ) {
			JOptionPane.showMessageDialog(this,"Data final do período inválida !","Atenção",JOptionPane.ERROR_MESSAGE);
			this.txtDataFinal.requestFocus();
			return false;
		}
		return true;
	}

	public void keyTyped(KeyEvent evt) {
	}

	public Date getPeriodoDataFinal() {
		return periodoDataFinal;
	}

	public Date getPeriodoDataInicial() {
		return periodoDataInicial;
	}
	
	public boolean isCanceled() {
		return canceled;
	}

	public void itemStateChanged(ItemEvent item) {
		if (item.getSource() instanceof JComboBox && item.getStateChange() == ItemEvent.SELECTED) {
			JComboBox combo  = (JComboBox) item.getSource();
			String    action = combo.getActionCommand();
			if ("MESES".equalsIgnoreCase(action)) {
				int ano = getSelecteYear();
				this.changeMonth((MesItem)item.getItem(), ano);
			} else
			if ("ANO".equalsIgnoreCase(action)) {
				this.changeYear(((Integer)item.getItem()).intValue());
			}
		}
	}

	private int getSelecteYear() {
		if (this.cmbAno != null && this.cmbAno.getSelectedItem() != null) {
			return ((Integer)this.cmbAno.getSelectedItem()).intValue();
		}
		return 0;
	}

	private void changeYear(int ano) {
		Calendar[] calendars = this.getActualCalendars();
		
		calendars[0].set(Calendar.YEAR,ano);
		calendars[1].set(Calendar.YEAR,ano);
		
		// Acertar dia início/fim do mês para o novo ano selecionado
		this.changeMonth((MesItem)this.cmbMeses.getSelectedItem(),ano);
	}
	
	private Calendar[] getActualCalendars() {
		Calendar[] calendars = new Calendar[2];
		calendars[0] = Calendar.getInstance();
		calendars[1] = Calendar.getInstance();
		
		calendars[0].setTime(this.periodoDataInicial);
		calendars[1].setTime(this.periodoDataFinal);
		
		return calendars;
	}
	private void changeMonth(MesItem mes, int ano) {
		Calendar[] calendars = this.getActualCalendars();
		
		//System.out.println( "Antes: " + sdf.format(calendars[0].getTime()) + " - " + sdf.format(calendars[1].getTime()));
		
		calendars[0].set(Calendar.DAY_OF_MONTH, 1 );
		calendars[1].set(Calendar.DAY_OF_MONTH, 1 );
		calendars[0].set(Calendar.MONTH, mes.getMes() - 1 );
		calendars[1].set(Calendar.MONTH, mes.getMes() - 1 );
		calendars[0].set(Calendar.YEAR, ano);
		calendars[1].set(Calendar.YEAR, ano);
		
		int firstDay = calendars[0].getActualMinimum(Calendar.DAY_OF_MONTH);
		int lastDay  = calendars[1].getActualMaximum(Calendar.DAY_OF_MONTH);
		
		calendars[0].set(Calendar.DAY_OF_MONTH,firstDay);
		calendars[1].set(Calendar.DAY_OF_MONTH,lastDay);
		
		//System.out.println( "Depois: " + sdf.format(calendars[0].getTime()) + " - " + sdf.format(calendars[1].getTime()));
		
		this.atualizarDatas(calendars);
	}

	private void atualizarDatas(Calendar[] calendars) {
		Calendar calendarInicial = calendars[0];
		Calendar calendarFinal   = calendars[1];
		
		this.periodoDataInicial = calendarInicial.getTime();
		this.txtDataInicial.setDate(this.periodoDataInicial);
		
		this.periodoDataFinal = calendarFinal.getTime();
		this.txtDataFinal.setDate(this.periodoDataFinal);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		if ( "OK".equals(e.getActionCommand()) ) {
			this.canceled = false;
			if (this.checkDates()) this.commitValues();
		} else
		if ("CANCELAR".equals(e.getActionCommand())) {
			this.canceled = true;
			this.dispose();
		}
	}

	public void stateChanged(ChangeEvent event) {
		if ( event.getSource() instanceof JSpinner ) {
			JSpinner spinner = (JSpinner)event.getSource();
			if ( spinner.getName().equals("MES") ) {
				MesItem mes = (MesItem)spinner.getValue();
				this.changeMonth(mes, this.getSelecteYear());
				this.cmbMeses.setSelectedItem(mes);
			} else
			if ( spinner.getName().equals("ANO") ) {
				int ano = ((Integer)spinner.getValue()).intValue();
				this.changeYear(ano);
				this.cmbAno.setSelectedItem(new Integer(ano));
			}
				
		}
	}
	
	public static class SpinnerListMeses extends SpinnerListModel {
		
		private JComboBox cmbAno;
		
		public SpinnerListMeses(Object[] data, JComboBox cmbAno) {
			super(data);
			this.cmbAno = cmbAno;
		}

		@Override
		public Object getNextValue() {
			if (super.getNextValue() == null) {
				int ano = ((Integer)cmbAno.getSelectedItem()).intValue() + 1;
				cmbAno.setSelectedItem(new Integer(ano));
				return new MesItem(1,"JANEIRO");
			}
			return super.getNextValue();
		}

		@Override
		public Object getPreviousValue() {
			if (super.getPreviousValue() == null) {
				int ano = ((Integer)cmbAno.getSelectedItem()).intValue() - 1;
				cmbAno.setSelectedItem(new Integer(ano));
				return new MesItem(12,"DEZEMBRO");
			}
			return super.getPreviousValue();
		}

	}


}
