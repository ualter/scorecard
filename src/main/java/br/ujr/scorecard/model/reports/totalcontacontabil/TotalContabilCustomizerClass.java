package br.ujr.scorecard.model.reports.totalcontacontabil;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;

import net.sf.jasperreports.engine.JRChart;
import net.sf.jasperreports.engine.JRChartCustomizer;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;

public class TotalContabilCustomizerClass implements JRChartCustomizer {

	public void customize(JFreeChart chart, JRChart jasperChart) {
		chart.setBackgroundPaint(Color.WHITE);
		
		CategoryPlot plot = (CategoryPlot)chart.getPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setDomainGridlinePaint(Color.GRAY);
		plot.setDomainGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.GRAY);
		
		if ( plot.getRenderer() instanceof LineAndShapeRenderer ) {
			LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
			renderer.setItemLabelFont(new Font("Courier New",Font.BOLD,8));
			//renderer.setSeriesPaint(0, Color.BLUE);
		} else
		if ( plot.getRenderer() instanceof BarRenderer ) {	
			BarRenderer renderer = (BarRenderer) plot.getRenderer();
			renderer.setDrawBarOutline(false);
			renderer.setItemLabelFont(new Font("Courier New",Font.BOLD,8));
			//renderer.setSeriesPaint(0, Color.BLUE);
		}
		
		/* NOT WORKING ON PDF	
		GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue,0.0f, 0.0f, new Color(0, 0, 64));
		GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.green,0.0f, 0.0f, new Color(0, 64, 0));
		GradientPaint gp2 = new GradientPaint(0.0f, 0.0f, Color.red,0.0f, 0.0f, new Color(64, 0, 0));
		renderer.setSeriesPaint(0, gp0);
		renderer.setSeriesPaint(1, gp1);
		renderer.setSeriesPaint(2, gp2);
		*/
		
		
		
		
	}

}
