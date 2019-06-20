package com.example.alesha.percomsg;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.common.math.DoubleMath;
import com.google.common.math.PairedStatsAccumulator;
import com.google.common.math.Stats;
import com.google.common.primitives.Doubles;
import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.UserConsent;
import com.microsoft.band.sensors.BandAccelerometerEvent;
import com.microsoft.band.sensors.BandAccelerometerEventListener;
import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandHeartRateEventListener;
import com.microsoft.band.sensors.HeartRateConsentListener;
import com.microsoft.band.sensors.SampleRate;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.Logistic;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;

import static com.example.alesha.percomsg.Complex.getabsolute;

/**
 * Gesture Recognition elements from author
 * Zaire Ali @ https://github.com/zaireali649/GestureDetection
 */
public class BandCommunication extends AppCompatActivity {
    public static final String BROADCAST_ACTION = "";
    private BandClient client = null;
    private Button btnGetConsent, btnStartReport;
    private TextView txtAccel, txtHeartRate, txtBand;
    public String accelData,heartData, bandData;



    //Gesture Recognition Variables
    private double[] Raw = {-1.0078,-1,-1.0156,-1.0078,-1.0078,-1.0156,-1.0156,-1.0156,-1,-1,-1,-0.97656,-0.97656,-0.97656,-0.92969,-0.9375,-0.97656,-1,-1.0312,-1.0625,-1.0625,-1.0781,-1.1484,-1.0781,-1.1641,-1.1953,-1.125,-1.1172,-1.0234,-0.92188,-0.89062,-0.82812,-0.72656,-0.53125,-0.375,-0.17188,-0.03125,0.085938,0.21875,0.41406,0.48438,0.38281,0.26562,0.078125,-0.20312,-0.8125,-1.8906,-3.5625,-4,-4,-3.0078,-0.71094,1.6406,3.9922,3.9922,3.9922,3.9922,2.3672,1.8203,1.3594,1.1406,1.2422,1.2188,1.1641,1.0938,1.25,1.2188,0.92188,0.85156,0.97656,0.98438,0.85156,0.79688,0.78125,0.65625,0.54688,0.53125,0.5,0.46875,0.40625,0.41406,0.39062,0.39844,0.375,0.33594,0.30469,0.34375,0.35938,0.35938,0.26562,0.19531,0.19531,0.19531,0.17188,0.023438,0.007813,0.054688,-0.03125,-0.19531,-0.20312,-0.13281,-0.17969,-0.26562,-0.25,-0.35156,-0.38281,-0.32031,-0.28906,-0.44531,-0.51562,-0.53125,-0.53906,-0.58594,-0.64062,-0.64844,-0.71875,-0.8125,-0.89062,-0.91406,-0.89062,-0.84375,-0.875,-0.875,-0.875,-0.86719,-0.89062,-0.89062,-0.88281,0.1875,0.17969,0.19531,0.20312,0.15625,0.20312,0.21094,0.21094,0.19531,0.1875,0.21094,0.24219,0.27344,0.28906,0.39844,0.46094,0.46875,0.47656,0.60156,0.64844,0.75781,1.0391,1.1953,1.5625,1.6562,2.0781,1.9531,1.9062,1.6172,1.6797,1.5781,1.3203,1.3984,1.3516,0.92969,0.79688,0.77344,0.54688,0.39844,0.28906,0.16406,0.039063,0.046875,0.10156,0.11719,0.1875,0.24219,0.65625,1.5547,1.875,1.0078,0.42969,0.97656,1.0234,1.4844,2.3125,0.046875,-0.64844,-0.78906,-0.54688,0.91406,0.88281,0.8125,0.50781,0.90625,0.65625,0.49219,0.39062,0.28125,0.14062,0.40625,0.49219,0.21875,0.14062,0.21094,0.26562,0.0625,-0.125,-0.070313,0.007813,0.015625,0.0625,0.15625,0.17969,0.26562,0.33594,0.28906,0.39062,0.42188,0.47656,0.57812,0.53125,0.60938,0.63281,0.75781,0.85156,0.80469,0.60156,0.77344,1.2734,1.3828,1.1875,1.1641,1.1484,1.3125,1.6016,1.6094,1.1719,1.1016,1.3125,1.3906,1.2109,0.99219,1.0078,1.0703,0.98438,0.89062,0.99219,1.0859,0.97656,0.76562,0.77344,0.85938,0.875,0.70312,0.69531,0.65625,0.57812,-0.070313,-0.078125,-0.085938,-0.078125,-0.0625,-0.070313,-0.078125,-0.078125,-0.054688,-0.039063,-0.039063,-0.0625,-0.054688,-0.03125,0,0.015625,0,0,0.023438,0.085938,0.11719,0.1875,0.21094,-0.29688,-0.48438,-0.625,-0.74219,-0.89062,-0.91406,-1.0312,-0.76562,-0.82812,-0.8125,-0.82031,-0.73438,-0.57812,-0.53125,-0.46875,-0.38281,-0.32031,-0.22656,-0.1875,-0.17969,-0.13281,-0.16406,-0.14844,-0.23438,-0.55469,-1.3281,-1.8594,-2.1328,-2.5859,-3.4844,-4,-4,-2.0703,0.23438,0.64844,-0.21094,-1.3125,-1.8906,-1.3594,-0.89062,-0.95312,-1.1562,-1.2734,-1.1328,-0.83594,-0.63281,-0.66406,-0.73438,-0.67188,-0.46875,-0.32031,-0.27344,-0.21094,-0.09375,-0.007813,0,0.023438,0.015625,-0.03125,-0.11719,-0.13281,-0.09375,-0.125,-0.125,-0.078125,-0.125,-0.14062,-0.20312,-0.24219,-0.24219,-0.29688,-0.24219,-0.25781,-0.27344,-0.23438,-0.11719,-0.1875,-0.32031,-0.375,-0.35938,-0.39844,-0.49219,-0.46094,-0.39062,-0.33594,-0.34375,-0.39062,-0.32031,-0.25781,-0.28125,-0.32031,-0.33594,-0.30469,-0.21094,-0.085938,-0.13281,-0.23438,-0.24219,-0.20312,-0.16406,-0.20312,-0.22656,-0.24219,-0.21875,-0.19531};

    private String[] gestureLabel = {"Fist Pump", "High Wave", "Hand Shake", "Fist Bump", "Low Wave", "Point", "Bandage Wound", "Blood Pressure Cuff", "Shoulder Radio", "Motion Over", "High Five", "Clap", "Whistling"};


    private double[] xData;
    private double[] yData;
    private double[] zData;

    private double[][] featuresExtract;
    private double[][] raw;

    private double t1, t2;

    private boolean done = false;

    private TextView gesture, chain;

    private int num_features = 60;
    private int window_size = 128;
    private int num_data = 181;
    private int num_classes = 13;

    private double[] collected = new double[window_size*3];
    private double[] collectedF = new double[num_features];
    private int datapoint = 0;

    private int chain_num = 0;

    private String strSummary = null;

    private File theFile;
    private File theFile2;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    private ToggleButton modelButton, detectionButton;

    private FastVector fvWekaAttributes;

    final WeakReference<Activity> reference = new WeakReference<Activity>(this);


    private String gesty = "blah";
    private String gestyTemp = "blah";








    private BandHeartRateEventListener mHeartRateEventListener = new BandHeartRateEventListener() {
        @Override
        public void onBandHeartRateChanged(BandHeartRateEvent bandHeartRateEvent) {
            if (bandHeartRateEvent != null) {
                //appendToUI(String.format("Heart Rate = %d beats per minute\n" + "Quality = %s\n", bandHeartRateEvent.getHeartRate(), bandHeartRateEvent.getQuality()));
                heartData = (String.format("Heart Rate = %d beats per minute\n" + "Quality = %s\n", bandHeartRateEvent.getHeartRate(), bandHeartRateEvent.getQuality()));
            }
        }
    };
    private BandAccelerometerEventListener mAccelerometerEventListener = new BandAccelerometerEventListener() {
        @Override
        public void onBandAccelerometerChanged(BandAccelerometerEvent event) {
            if (event != null) {


                ///////////////////////////////////////////

                collected[datapoint] = event.getAccelerationX();
                collected[window_size + datapoint] = event.getAccelerationY();
                collected[(window_size * 2) + datapoint] = event.getAccelerationZ();
                datapoint = datapoint + 1;

                if(datapoint == 128)
                {
                    datapoint = 64;
                    try {

                        t1 = System.currentTimeMillis();


                        collectedF = featureExtraction(collected);
                        System.arraycopy(collected, datapoint, collected, 0, datapoint);


                        Classifier cModel = (Classifier) weka.core.SerializationHelper.read(String.valueOf(theFile2));


                        // Create an empty testing set
                        Instances isTestingSet = new Instances("test", fvWekaAttributes, 1);
                        isTestingSet.setClassIndex(num_features);

                        // Create the instance
                        Instance iExample2 = new DenseInstance(num_features);
                        //int rand = 0 + (int)(Math.random() * ((features.length-11 - 0) + 1));



                        for (int c = 0; c < num_features; c++)
                        {
                            iExample2.setValue((Attribute)fvWekaAttributes.elementAt(c), collectedF[c]);
                        }
                        //iExample2.setValue((Attribute)fvWekaAttributes.eleentAt(features[0].length), raw[b][raw[0].length-1]);

                        iExample2.setDataset(isTestingSet);



                        gestyTemp = String.valueOf(cModel.classifyInstance(iExample2));

                        //get the predicted probabilities
                        final double[] prediction = cModel.distributionForInstance(iExample2);



                        if (gesty.equals(gestyTemp))
                        {
                            chain_num = chain_num + 1;
                        }
                        else
                        {
                            chain_num = 0;
                        }
                        gesty = gestyTemp;
                        Log.d("Predict", "Finish");


                        t2 = System.currentTimeMillis();

                        Log.d("TIME: ", Integer.toString((int) (t2 - t1)));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(BandCommunication.this, "Probability of class "+String.valueOf((int) Double.parseDouble(gesty)) +" : "+Double.toString(BigDecimal.valueOf(prediction[(int) Double.parseDouble(gesty)]).setScale(3, RoundingMode.HALF_UP).doubleValue()), Toast.LENGTH_SHORT).show();

                                if (prediction[(int) Double.parseDouble(gesty)] > .5)
                                {
                                    gesture.setText(gestureLabel[(int) Double.parseDouble(gesty)]);
                                    //gesture.setText(gesty);
                                    chain.setText(String.valueOf(chain_num));
                                }
                                else
                                {
                                    gesture.setText("Unknown");
                                    //gesture.setText(gesty);
                                    chain_num = 0;
                                    chain.setText(String.valueOf(chain_num));
                                }


                                //output predictions
                                /*for(int i=0; i<prediction.length; i++)
                                {
                                    Toast.makeText(MainActivity.this, "Probability of class "+String.valueOf(i) +" : "+Double.toString(prediction[i]), Toast.LENGTH_LONG).show();
                                }*/

                            }
                        });


                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

        }
               ///////////////////////////////////////////
                accelData = ( "(" + event.getAccelerationX() + ", " + event.getAccelerationY() + " , " + event.getAccelerationZ() + ")" + "\n");

            } else {
                //Toast.makeText(BandCommunication.this,"Band is not connected, please make sure bluetooth is on and in range.\n",Toast.LENGTH_SHORT).show();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_band_communication);
        btnGetConsent = findViewById(R.id.btnGetConsent);
        btnStartReport = findViewById(R.id.btnStartReport);
        txtAccel = findViewById(R.id.txtAccel);
        txtBand = findViewById(R.id.txtBandSettings);
        txtHeartRate = findViewById(R.id.txtHeartRate);
        final WeakReference<Activity> reference = new WeakReference<Activity>(this);

        ///////////////////////////////////////////////
        theFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "models");
        theFile2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "models/logreg.model");

        modelButton = (ToggleButton) findViewById(R.id.tbtnModel);
        detectionButton = (ToggleButton) findViewById(R.id.tbtnDetect);

        gesture = (TextView) findViewById(R.id.txtGesture);
        chain = (TextView) findViewById(R.id.txtChain);

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {


                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},23
                );
            }
        }
        Attribute att;
        // Declare the feature vector
        fvWekaAttributes = new FastVector(num_features + 1);

        //int[] noDuplicates = DoubleStream.of(raw[0][raw[0].length-1]).distinct().toArray();

        for (int b = 0; b < num_features; b++ )
        {
            att = new Attribute(String.valueOf(b));
            fvWekaAttributes.addElement(att);
        }


        // Declare the class attribute along with its values
        FastVector fvClassVal = new FastVector(12);
        for (int b = 0; b < num_classes; b++) {
            fvClassVal.addElement(String.valueOf(b));
        }


        Attribute ClassAttribute = new Attribute("theClass", fvClassVal);
        fvWekaAttributes.addElement(ClassAttribute);

        modelButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    verifyStoragePermissions(BandCommunication.this);
                    modelButton.setEnabled(false);
                    Toast.makeText(BandCommunication.this, "Generating model... This may take up to 3 minutes...", Toast.LENGTH_LONG).show();
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                while(true) {
                                    if (!done) {
                                        done = true;

                                        t1 = System.currentTimeMillis();

                                        AssetManager assetManager = getAssets();
                                        raw = new double[num_data][window_size * 3 + 2];
                                        try {
                                            //String[] files = assetManager.list("");

                                            InputStream input = assetManager.open("rawData128SinglePoint.csv");

                                            final Reader reader = new InputStreamReader(input, "UTF-8");
                                            CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL.withIgnoreHeaderCase());
                                            try {
                                                List<CSVRecord> csvRecordList = parser.getRecords();

                                                //Toast.makeText(MainActivity.this, Integer.toString(parser.getRecords().size()), Toast.LENGTH_LONG).show();

                                                for (int x = 0; x < csvRecordList.size(); x++) {
                                                    for (int y = 0; y < window_size * 3 + 2; y++)
                                                    {
                                                        raw[x][y] = Double.parseDouble(csvRecordList.get(x).get(y));
                                                    }
                                                    //final String string = record.get(0);
                                                    //Toast.makeText(MainActivity.this, string, Toast.LENGTH_LONG).show();
                                                }
                                            } finally {
                                                parser.close();
                                                reader.close();
                                            }

                                            for (int y = 0; y < 10; y++)
                                            {
                                                Log.d("Raw[" + String.valueOf(y) + "]: ", Arrays.toString(raw[y]));
                                            }

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        final int n = 1;

                                        featuresExtract = new double[num_data][num_features];

                                        for (int z = 0; z < raw.length; z++) {
                                            featuresExtract[z] = featureExtraction(raw[z]);
                                            Log.d("Feat " + Integer.toString(z) + ": ", Arrays.toString(featuresExtract[z]));
                                        }

                                        // **************************************************************************************************


                                        // Create an empty training set
                                        Instances isTrainingSet = new Instances("Rel", fvWekaAttributes, featuresExtract.length);
                                        // Set class index
                                        isTrainingSet.setClassIndex(featuresExtract[0].length);

                                        // Create the instance
                                        Instance iExample = new DenseInstance(featuresExtract[0].length + 1);
                                        for (int b = 0; b < featuresExtract.length; b++)
                                        {
                                            for (int c = 0; c < featuresExtract[0].length; c++)
                                            {
                                                iExample.setValue((Attribute)fvWekaAttributes.elementAt(c), featuresExtract[b][c]);
                                            }
                                            iExample.setValue((Attribute)fvWekaAttributes.elementAt(featuresExtract[0].length), raw[b][raw[0].length-1]);
                                            //iExample.setValue((Attribute)fvWekaAttributes.elementAt(features[0].length), 0 + (int)(Math.random() * ((11 - 0) + 1)));

                                            // add the instance
                                            isTrainingSet.add(iExample);
                                        }

                                        Log.d("Class Build", "Start");

                                        // Create a LogisticRegression classifier
                                        Classifier cModel = (Classifier)new Logistic();
                                        cModel.buildClassifier(isTrainingSet);

                                        Log.d("Class Build", "Finish");


                                        theFile.mkdir();
                                        theFile2.createNewFile();

                                        SerializationHelper.write(String.valueOf(theFile2), cModel);

                                        t2 = System.currentTimeMillis();

                                        Log.d("TIME: ", Integer.toString((int) (t2 - t1)));

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                modelButton.toggle();
                                                modelButton.setEnabled(true);

                                            }
                                        });

                                    }
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    thread.start();

                }
                else{

                }
            }
        });


        detectionButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    // if toggle button is enabled/on
                    if(modelButton.isEnabled() && theFile2.exists())
                    {

                        //new HeartRateConsentTask().execute(reference);
                        datapoint = 0;
                        chain_num = 0;
                        new AccelerometerSubscriptionTask().execute();
                    }
                    else
                    {
                        detectionButton.toggle();
                        Toast.makeText(BandCommunication.this, "Please Generate a Model First!", Toast.LENGTH_LONG).show();
                    }


                }
                else{

                    try {
                        client.getSensorManager().unregisterAccelerometerEventListener(mAccelerometerEventListener);

                    } catch (BandIOException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                }
            }
        });















        ///////////////////////////////////////////////////////

        btnGetConsent.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View view) {
                new HeartRateConsentTask().execute(reference);
            }
        });
        btnStartReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtHeartRate.setText(heartData);
                new HeartRateSubscriptionTask().execute();
                txtAccel.setText(accelData);
                new AccelerometerSubscriptionTask().execute();
                txtBand.setText(bandData);

                final Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable(){
                    @Override
                    public void run() {
                        txtAccel.setText(accelData);
                        txtHeartRate.setText(heartData);
                    handler.post(this);
                    }
                });
            }
        });
    }//end of onCreate



    /////////////////////////////////////////////////////////////

    private double[] featureExtraction(double[] f)
    {
        double[] features = new double[num_features];
        xData = new double[128];
        yData = new double[128];
        zData = new double[128];

        System.arraycopy(f, 0, xData, 0, xData.length);
        System.arraycopy(f, xData.length, yData, 0, yData.length);
        System.arraycopy(f, xData.length + yData.length, zData, 0, zData.length);

        //Log.d("x: ", Arrays.toString(xData));
        //Log.d("y: ", Arrays.toString(yData));
        //Log.d("z: ", Arrays.toString(zData));

        features[0] = Doubles.min(xData);
        features[1] = Doubles.min(yData);
        features[2] = Doubles.min(zData);

        features[3] = Doubles.max(xData);
        features[4] = Doubles.max(yData);
        features[5] = Doubles.max(zData);

        features[6] = BigDecimal.valueOf(DoubleMath.mean(xData)).setScale(5, RoundingMode.HALF_UP).doubleValue();
        features[7] = BigDecimal.valueOf(DoubleMath.mean(yData)).setScale(5, RoundingMode.HALF_UP).doubleValue();
        features[8] = BigDecimal.valueOf(DoubleMath.mean(zData)).setScale(5, RoundingMode.HALF_UP).doubleValue();

        features[9] = BigDecimal.valueOf(Stats.of(xData).populationStandardDeviation()).setScale(6, RoundingMode.HALF_UP).doubleValue();
        features[10] = BigDecimal.valueOf(Stats.of(yData).populationStandardDeviation()).setScale(6, RoundingMode.HALF_UP).doubleValue();
        features[11] = BigDecimal.valueOf(Stats.of(zData).populationStandardDeviation()).setScale(6, RoundingMode.HALF_UP).doubleValue();

        PairedStatsAccumulator corrXY = new PairedStatsAccumulator();
        PairedStatsAccumulator corrXZ = new PairedStatsAccumulator();
        PairedStatsAccumulator corrYZ = new PairedStatsAccumulator();

        for (int i = 0; i < xData.length; i++) {
            corrXY.add(xData[i], yData[i]);
            corrXZ.add(xData[i], zData[i]);
            corrYZ.add(yData[i], zData[i]);
        }

        features[12] = BigDecimal.valueOf(corrXY.pearsonsCorrelationCoefficient()).setScale(5, RoundingMode.HALF_UP).doubleValue();
        features[13] = BigDecimal.valueOf(corrXZ.pearsonsCorrelationCoefficient()).setScale(5, RoundingMode.HALF_UP).doubleValue();
        features[14] = BigDecimal.valueOf(corrYZ.pearsonsCorrelationCoefficient()).setScale(5, RoundingMode.HALF_UP).doubleValue();

        features[15] = BigDecimal.valueOf(zero_cross_rate(xData)).setScale(6, RoundingMode.HALF_UP).doubleValue();
        features[16] = BigDecimal.valueOf(zero_cross_rate(yData)).setScale(6, RoundingMode.HALF_UP).doubleValue();
        features[17] = BigDecimal.valueOf(zero_cross_rate(zData)).setScale(6, RoundingMode.HALF_UP).doubleValue();

        // SKEW *************************************************************************************************************************************
        features[18] = BigDecimal.valueOf(new Skewness().evaluate(xData)).setScale(6, RoundingMode.HALF_UP).doubleValue();
        features[19] = BigDecimal.valueOf(new Skewness().evaluate(yData)).setScale(6, RoundingMode.HALF_UP).doubleValue();
        features[20] = BigDecimal.valueOf(new Skewness().evaluate(zData)).setScale(6, RoundingMode.HALF_UP).doubleValue();

        // KURTOSIS *************************************************************************************************************************************
        features[21] = BigDecimal.valueOf(new Kurtosis().evaluate(xData)).setScale(6, RoundingMode.HALF_UP).doubleValue();
        features[22] = BigDecimal.valueOf(new Kurtosis().evaluate(yData)).setScale(6, RoundingMode.HALF_UP).doubleValue();
        features[23] = BigDecimal.valueOf(new Kurtosis().evaluate(zData)).setScale(6, RoundingMode.HALF_UP).doubleValue();

        features[24] = BigDecimal.valueOf(features[6] / features[9]).setScale(5, RoundingMode.HALF_UP).doubleValue();
        features[25] = BigDecimal.valueOf(features[7] / features[10]).setScale(5, RoundingMode.HALF_UP).doubleValue();
        features[26] = BigDecimal.valueOf(features[8] / features[11]).setScale(5, RoundingMode.HALF_UP).doubleValue();

        features[27] = BigDecimal.valueOf(mean_cross_rate(xData, features[6])).setScale(6, RoundingMode.HALF_UP).doubleValue();
        features[28] = BigDecimal.valueOf(mean_cross_rate(yData, features[7])).setScale(6, RoundingMode.HALF_UP).doubleValue();
        features[29] = BigDecimal.valueOf(mean_cross_rate(zData, features[8])).setScale(6, RoundingMode.HALF_UP).doubleValue();

        features[30] = BigDecimal.valueOf(trapz(xData)).setScale(4, RoundingMode.HALF_UP).doubleValue();
        features[31] = BigDecimal.valueOf(trapz(yData)).setScale(4, RoundingMode.HALF_UP).doubleValue();
        features[32] = BigDecimal.valueOf(trapz(zData)).setScale(4, RoundingMode.HALF_UP).doubleValue();

        Complex[] xFFT = Complex.getfft(xData);
        Complex[] yFFT = Complex.getfft(yData);
        Complex[] zFFT = Complex.getfft(zData);

        features[33] = Complex.sigEnergy(xFFT);
        features[34] = Complex.sigEnergy(yFFT);
        features[35] = Complex.sigEnergy(zFFT);

        for (int a = 0; a < 8; a++) {
            features[36 + a] = BigDecimal.valueOf(getabsolute(xFFT[a])).setScale(5, RoundingMode.HALF_UP).doubleValue();
            features[44 + a] = BigDecimal.valueOf(getabsolute(yFFT[a])).setScale(5, RoundingMode.HALF_UP).doubleValue();
            features[52 + a] = BigDecimal.valueOf(getabsolute(zFFT[a])).setScale(5, RoundingMode.HALF_UP).doubleValue();
        }

        return features;
    }
    private Double zero_cross_rate(double[] data)
    {
        double counter = 0.0;

        for (int i = 1; i < data.length; i++)
        {
            if (data[i-1] * data[i] < 0)
            {
                counter = counter + 1;
            }
        }

        return counter*(1.0/127.0);
    }

    private Double mean_cross_rate(double[] data, double mean)
    {
        double counter = 0.0;

        for (int i = 1; i < data.length; i++)
        {
            if ((data[i-1] - mean) * (data[i] - mean) < 0)
            {
                counter = counter + 1;
            }
        }

        return counter*(1.0/127.0);
    }

    private Double trapz(double [] y)
    {
        double dx = 1.0;
        double d = dx;
        double area = 0;

        for (int i = 1; i < y.length; i++)
        {
            area = area + ((d * (y[i] + y[i-1]) / 2.0));
        }
        return area;
    }



    ///////////////////////////////////////////////////////////////

    //get connections to band
    private boolean getConnectedBandClient() throws InterruptedException, BandException {
        //get paired bands
        if (client == null) {
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
                return false;
            }
            client = BandClientManager.getInstance().create(getBaseContext(), devices[0]);
            bandData = ("PerCom; Band #7");
        } else if (ConnectionState.CONNECTED == client.getConnectionState()) {
            return true;
        }
        return ConnectionState.CONNECTED == client.connect().await();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //txtHeartRate.setText("");
       // txtAccel.setText("");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (client != null) {
            try {
                client.getSensorManager().unregisterHeartRateEventListener(mHeartRateEventListener);
            } catch (BandIOException e) {
                Toast.makeText(BandCommunication.this, e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (client != null) {
            try {
                client.disconnect().await();
            } catch (InterruptedException e) {

            } catch (BandException e) {
            }
        }
        super.onDestroy();
    }



/*
    private void appendToUI(final String string) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtHeartRate.setText(string);
                txtAccel.setText(string);
            }
        });
    }*/

    //Get consent of heart rate sensor data
    private class HeartRateConsentTask extends AsyncTask<WeakReference<Activity>, Void, Void> {
        @Override
        protected Void doInBackground(WeakReference<Activity>... params) {
            try {
                if (getConnectedBandClient()) {
                    if (params[0].get() != null) {
                        client.getSensorManager().requestHeartRateConsent(params[0].get(), new HeartRateConsentListener() {
                            @Override
                            public void userAccepted(boolean b) {

                            }
                        });
                    } else {
                        Toast.makeText(BandCommunication.this, "Band not connected",
                                Toast.LENGTH_SHORT).show();
                    }

                }
            } catch (Exception e) {
                Toast.makeText(BandCommunication.this, e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }

    private class HeartRateSubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    if (client.getSensorManager().getCurrentHeartRateConsent() == UserConsent.GRANTED) {
                        client.getSensorManager().registerHeartRateEventListener(mHeartRateEventListener);
                    } else {
//                        Toast.makeText(BandCommunication.this,"You have not given this application access to heart rate data yet; please press consent button to continue.\n",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //Toast.makeText(BandCommunication.this,"Band is not connected, please make sure bluetooth is on and in range.\n",Toast.LENGTH_SHORT).show();
                }
            } catch (BandException e) {
                // Toast.makeText(BandCommunication.this, e.getMessage(),
                //Toast.LENGTH_SHORT).show();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class AccelerometerSubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    client.getSensorManager().registerAccelerometerEventListener(mAccelerometerEventListener, SampleRate.MS16);
                } else {
                    //Toast.makeText(BandCommunication.this,"Band is not connected, please make sure bluetooth is on and in range.\n",Toast.LENGTH_SHORT).show();
                }
            } catch (BandException e) {
                String exceptionMsg = "";
                switch (e.getErrorType()) {
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMsg = "Microsoft Health Band Service does not support this SDK Version. Please update latest SDK.";
                        break;
                    case SERVICE_ERROR:
                        exceptionMsg = "Microsoft Health Band Service is not available. Please make sure Microsoft Health is installed on your device.";
                        break;
                    default:
                        exceptionMsg = "Unknown Error: " + e.getMessage() + "\n";
                        break;

                }
                Toast.makeText(getApplicationContext(), exceptionMsg, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.getMessage();
            }
            return null;
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }




}//End of program
