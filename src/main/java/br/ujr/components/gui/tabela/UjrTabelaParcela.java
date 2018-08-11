package br.ujr.components.gui.tabela;

import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.Constructor;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import br.ujr.components.gui.field.UjrCurrencyField;
import br.ujr.components.gui.field.UjrDateField;

/**
 * Table Cell for Parcelas
 * 
 * ENTER     -> edit cell
 * CTRL + C  -> copy cell content
 * CTRL + V  -> paste cell content
 * 
 * @author Ualter Junior
 */
public class UjrTabelaParcela extends JTable {

	protected static final long serialVersionUID = 1L;
	protected Component componentBefore = null;
	protected Component componentAfter = null;

	public UjrTabelaParcela(Component componentBefore, Component componentAfter) {
		this.componentBefore = componentBefore;
		this.componentAfter  = componentAfter;
		this.setUpKeyStrokes();
		this.setUpApperance();
	}
	
	public UjrTabelaParcela(TableModel dm, Component componentBefore, Component componentAfter) {
		super(dm);
		this.componentBefore = componentBefore;
		this.componentAfter  = componentAfter;
		this.setUp();
	}
	
	public Component getComponentAfter() {
		return componentAfter;
	}

	public Component getComponentBefore() {
		return componentBefore;
	}

	public void setUp() {
		this.setUpKeyStrokes();
		this.setUpApperance();
		this.setUpColumns();
	}

	protected void setUpColumns() {
		if (this.getColumnModel().getColumnCount() > 0) {
			TableColumn vencimentoColumn = this.getColumnModel().getColumn(0);
			TableColumn valorColumn      = this.getColumnModel().getColumn(1);

			vencimentoColumn.setPreferredWidth(20);
			valorColumn.setPreferredWidth(22);
			valorColumn.setPreferredWidth(50);

			DefaultCellEditor vencimentoEditor = new DefaultCellEditor(new UjrDateField());
			DefaultCellEditor valorEditor      = new DefaultCellEditor(new UjrCurrencyField());

			DefaultTableCellRenderer vencimentoRenderer = new DefaultTableCellRenderer();
			DefaultTableCellRenderer valorRenderer      = new DefaultTableCellRenderer();

			vencimentoRenderer.setToolTipText("Data de Vencimento no formato DD/MM/YYYY");
			vencimentoRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);

			valorRenderer.setToolTipText("Valor da parcela");
			valorRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);

			vencimentoColumn.setCellRenderer(vencimentoRenderer);
			vencimentoColumn.setCellEditor(vencimentoEditor);

			valorColumn.setCellRenderer(valorRenderer);
			valorColumn.setCellEditor(valorEditor);
		}
	}

	private void setUpApperance() {
		this.setFont(new Font("Courier New",Font.PLAIN,13));
	}
	
	private void setUpKeyStrokes() {
		this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0), "startEditing");
		
		KeyStroke shiftTab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB,ActionEvent.SHIFT_MASK,false);
		KeyStroke copy     = KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.CTRL_MASK,false);
		KeyStroke paste    = KeyStroke.getKeyStroke(KeyEvent.VK_V,ActionEvent.CTRL_MASK,false);
		
		Action selectPreviousColumnCellAction = (Action)this.getActionMap().get("selectPreviousColumnCell");
		this.getActionMap().put("selectPreviousColumnCell", new UjrTabelaShiftTabActionDecorator(this,selectPreviousColumnCellAction));
		
		Action selectNextColumnCell = (Action)this.getActionMap().get("selectNextColumnCell");
		this.getActionMap().put("selectNextColumnCell", new UjrTabelaTabActionDecorator(this,selectNextColumnCell));
		
		this.getInputMap().put(copy, "copyCell");
		this.getActionMap().put("copyCell", new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				UjrTabelaParcela table = (UjrTabelaParcela)evt.getSource();
				int column = table.getSelectedColumn();
				int row    = table.getSelectedRow();
				Clipboard system = Toolkit.getDefaultToolkit().getSystemClipboard();
				String str = table.getValueAt(row, column).toString();
				StringSelection stringSelection = new StringSelection(str);
				system.setContents(stringSelection, stringSelection);
			}
		});
		this.getInputMap().put(paste, "pasteCell");
		this.getActionMap().put("pasteCell", new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				UjrTabelaParcela table = (UjrTabelaParcela)evt.getSource();
				int column = table.getSelectedColumn();
				int row    = table.getSelectedRow();
				Clipboard system = Toolkit.getDefaultToolkit().getSystemClipboard();
				try {
					String str = (String)(system.getContents(this).getTransferData(DataFlavor.stringFlavor));
					Class clazz = table.getValueAt(row, column).getClass();
					if ( clazz == String.class ) {
						table.setValueAt(str, row, column);
					} else {
					     try {
							Constructor constructor = clazz.getConstructor(new Class[]{String.class});
							Object obj = constructor.newInstance(new Object[]{str});
							table.setValueAt(obj, row, column);
						} catch (NoSuchMethodException e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
}
