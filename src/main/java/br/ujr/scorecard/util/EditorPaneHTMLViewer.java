package br.ujr.scorecard.util;

import javax.swing.JScrollPane;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.io.IOException;
import java.awt.Dimension;

public class EditorPaneHTMLViewer extends JEditorPane {
	private JScrollPane createScrollPane() {
		JScrollPane editorScrollPane = new JScrollPane(this);
		editorScrollPane.setPreferredSize(new Dimension(700, 500));
		return editorScrollPane;
	}

	private void loadStartingPage() {
		setEditable(false);
		try {
			setPage("http://www.java.net/");
		} catch (IOException e) {
			System.err.println("can't connect/");
		}
	}

	private void createAndShowGUI() {
		JFrame frame = new JFrame("EditorPaneHTMLViewer/");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JScrollPane content = createScrollPane();
		loadStartingPage();
		frame.add(content);
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		//Schedule a job for the event-dispatching thread:
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				(new EditorPaneHTMLViewer()).createAndShowGUI();
			}
		});
	}
}