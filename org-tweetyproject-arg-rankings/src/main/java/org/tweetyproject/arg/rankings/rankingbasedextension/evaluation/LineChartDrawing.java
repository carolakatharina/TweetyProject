package org.tweetyproject.arg.rankings.rankingbasedextension.evaluation;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class LineChartDrawing extends JFrame {

        private static final long serialVersionUID = 1L;


        public LineChartDrawing(String title, String x_axis, String y_axis, List<ThresholdEvaluationObject> data) throws IOException {
            super(title);

            // Create chart
            JFreeChart chart = ChartFactory.createLineChart(
                    title, // Chart title
                    x_axis, // X-Axis Label
                    y_axis, // Y-Axis Label
                    createDataset(data),
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );

            this.setName(title);

            ChartUtilities.saveChartAsPNG(new File(".\\org-tweetyproject-arg-rankings\\src\\main\\java\\org\\tweetyproject\\arg\\rankings\\rankingbasedextension\\evaluation\\results\\"+title+".png"), chart, 600, 400);

            //drawChart();

        }

       private DefaultCategoryDataset createDataset(List<ThresholdEvaluationObject> data) {
           DefaultCategoryDataset dataset = new DefaultCategoryDataset();
           for (var obj : data) {
               for (var i = 0; i < obj.getThresholds().size(); i++) {

                       dataset.addValue(obj.getPrinziplesFulfilled().get(i).size(), obj.getBezeichnung(), obj.getThresholds().get(i));



               }

           }
           return dataset;
       }


}
