package br.ujr.scorecard.gui.view.screen.cellrenderer;

import java.awt.Color;
import java.awt.Font;

public class RendererRules {
	
	private PredicateRenderer predicatedRenderer = null;
	
	public RendererRules(PredicateRenderer predicateRenderer) {
		this.predicatedRenderer = predicateRenderer;
	}
	
	public PredicateRenderer getPredicatedRenderer() {
		return predicatedRenderer;
	}

	public void setPredicatedRenderer(PredicateRenderer predicatedRenderer) {
		this.predicatedRenderer = predicatedRenderer;
	}
	
	public static class Format {
		
		private Font font;
		private Color foreground;
		private Color background;
		
		public Font getFont() {
			return font;
		}
		public void setFont(Font font) {
			this.font = font;
		}
		public Color getForeground() {
			return foreground;
		}
		public void setForeground(Color foreground) {
			this.foreground = foreground;
		}
		public Color getBackground() {
			return background;
		}
		public void setBackground(Color background) {
			this.background = background;
		}
	}

}
