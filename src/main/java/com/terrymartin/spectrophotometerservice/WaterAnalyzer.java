package com.terrymartin.spectrophotometerservice;

import org.encog.ConsoleStatusReportable;
import org.encog.Encog;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.versatile.NormalizationHelper;
import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.data.versatile.columns.ColumnDefinition;
import org.encog.ml.data.versatile.columns.ColumnType;
import org.encog.ml.data.versatile.sources.CSVDataSource;
import org.encog.ml.data.versatile.sources.VersatileDataSource;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.model.EncogModel;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;
import org.encog.util.simple.EncogUtility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WaterAnalyzer {

    private MLRegression bestMethod;
    private NormalizationHelper helper;
    private String[] line;
    private MLData input;

    public static void main(String[] args) throws IOException, InterruptedException {
        WaterAnalyzer analyzer = new WaterAnalyzer();
        analyzer.setup();
    }

    public void setup() {
        try {
            // Download the data that we will attempt to model.
            File trainingFile = new File("training-data.csv");

            // Define the format of the data file.
            // This area will change, depending on the columns and
            // format of the file that you are trying to model.
            VersatileDataSource source = new CSVDataSource(trainingFile, false,
                    CSVFormat.DECIMAL_POINT);
            VersatileMLDataSet data = new VersatileMLDataSet(source);
            data.defineSourceColumn("400", 0, ColumnType.continuous);
            data.defineSourceColumn("410", 1, ColumnType.continuous);
            data.defineSourceColumn("420", 2, ColumnType.continuous);
            data.defineSourceColumn("430", 3, ColumnType.continuous);
            data.defineSourceColumn("440", 4, ColumnType.continuous);
            data.defineSourceColumn("450", 5, ColumnType.continuous);
            data.defineSourceColumn("460", 6, ColumnType.continuous);
            data.defineSourceColumn("470", 7, ColumnType.continuous);
            data.defineSourceColumn("480", 8, ColumnType.continuous);
            data.defineSourceColumn("490", 9, ColumnType.continuous);
            data.defineSourceColumn("500", 10, ColumnType.continuous);
            data.defineSourceColumn("510", 11, ColumnType.continuous);
            data.defineSourceColumn("520", 12, ColumnType.continuous);
            data.defineSourceColumn("530", 13, ColumnType.continuous);
            data.defineSourceColumn("540", 14, ColumnType.continuous);
            data.defineSourceColumn("550", 15, ColumnType.continuous);
            data.defineSourceColumn("560", 16, ColumnType.continuous);
            data.defineSourceColumn("570", 17, ColumnType.continuous);
            data.defineSourceColumn("580", 18, ColumnType.continuous);
            data.defineSourceColumn("590", 19, ColumnType.continuous);
            data.defineSourceColumn("600", 20, ColumnType.continuous);
            data.defineSourceColumn("610", 21, ColumnType.continuous);
            data.defineSourceColumn("620", 22, ColumnType.continuous);
            data.defineSourceColumn("630", 23, ColumnType.continuous);
            data.defineSourceColumn("640", 24, ColumnType.continuous);
            data.defineSourceColumn("650", 25, ColumnType.continuous);
            data.defineSourceColumn("660", 26, ColumnType.continuous);
            data.defineSourceColumn("670", 27, ColumnType.continuous);
            data.defineSourceColumn("680", 28, ColumnType.continuous);
            data.defineSourceColumn("690", 29, ColumnType.continuous);

            // Define the column that we are trying to predict.
            ColumnDefinition outputColumn = data.defineSourceColumn("compound", 30,
                    ColumnType.nominal);

            // Analyze the data, determine the min/max/mean/sd of every column.
            data.analyze();

            // Map the prediction column to the output of the model, and all
            // other columns to the input.
            data.defineSingleOutputOthersInput(outputColumn);

            // Create feedforward neural network as the model type. MLMethodFactory.TYPE_FEEDFORWARD.
            // You could also other model types, such as:
            // MLMethodFactory.SVM:  Support Vector Machine (SVM)
            // MLMethodFactory.TYPE_RBFNETWORK: RBF Neural Network
            // MLMethodFactor.TYPE_NEAT: NEAT Neural Network
            // MLMethodFactor.TYPE_PNN: Probabilistic Neural Network
            EncogModel model = new EncogModel(data);
            model.selectMethod(data, MLMethodFactory.TYPE_FEEDFORWARD);

            // Send any output to the console.
            model.setReport(new ConsoleStatusReportable());

            // Now normalize the data.  Encog will automatically determine the correct normalization
            // type based on the model you chose in the last step.
            data.normalize();

            // Hold back some data for a final validation.
            // Shuffle the data into a random ordering.
            // Use a seed of 1001 so that we always use the same holdback and will get more consistent results.
            model.holdBackValidation(0.3, true, 1001);

            // Choose whatever is the default training type for this model.
            model.selectTrainingType(data);

            // Use a 5-fold cross-validated train.  Return the best method found.
            bestMethod = (MLRegression)model.crossvalidate(5, true);

            // Display the training and validation errors.
            System.out.println( "Training error: " + EncogUtility.calculateRegressionError(bestMethod, model.getTrainingDataset()));
            System.out.println( "Validation error: " + EncogUtility.calculateRegressionError(bestMethod, model.getValidationDataset()));

            // Display our normalization parameters.
            helper = data.getNormHelper();
            System.out.println(helper.toString());

            // Display the final model.
            System.out.println("Final model: " + bestMethod);

            // Loop over the entire, original, dataset and feed it through the model.
            // This also shows how you would process new data, that was not part of your
            // training set.  You do not need to retrain, simply use the NormalizationHelper
            // class.  After you train, you can save the NormalizationHelper to later
            // normalize and denormalize your data.
            ReadCSV csv = new ReadCSV(trainingFile, false, CSVFormat.DECIMAL_POINT);
            line = new String[30];
            input = helper.allocateInputVector();

//            while(csv.next()) {
//                determineCompound(convertCSVToLineArray(csv));
//            }

            // Delete data file ande shut down.
//            trainingFile.delete();
            Encog.getInstance().shutdown();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String[] convertCSVToLineArray(ReadCSV csv) {
        List<String> list = new ArrayList<>();
        for (int col=0; col<30; col++) {
            list.add(csv.get(col));
        }
        return list.toArray(new String[]{});
    }

    public String determineCompound(String[] values) {
        StringBuilder result = new StringBuilder();
        line = values;
        String correct = "whatever";
        helper.normalizeInputVector(line,input.getData(),false);
        MLData output = bestMethod.compute(input);
        String compoundChosen = helper.denormalizeOutputVectorToString(output)[0];

        result.append(Arrays.toString(line));
        result.append(" -> predicted: ");
        result.append(compoundChosen);
        result.append("(correct: ");
        result.append(correct);
        result.append(")");

        System.out.println(result.toString());
        if(compoundChosen.equals("0")) {
            return "Air";
        } else if(compoundChosen.equals("1")) {
            return "Water";
        } else {
            return "Lead";
        }
    }
}
