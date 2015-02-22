package ca.mcgill.amv;

import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
    public static boolean isAdditionMode = false;
    public static boolean isScalarProductMode = false;
    public static boolean isCrossProductMode = false;
    public static boolean inPolarMode = false;

    Chart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Chart chart = (Chart) this.findViewById(R.id.chartBottom);
        Resources res = getResources();
//        chart = new Chart(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setCoordinatesMode(View view) {
        CheckBox coordMode = (CheckBox) findViewById(R.id.polarCoordinateCheckBox);

        if(coordMode.isChecked()) {
            setPolarMode();
        } else {
            setCartesianMode();
        }
    }

    public void setVectorAddition(View view) {
        EditText vecOneX = (EditText) findViewById(R.id.text_vector_1_x);
        EditText vecOneY = (EditText) findViewById(R.id.text_vector_1_y);
        EditText vecTwoX = (EditText) findViewById(R.id.text_vector_2_x);
        EditText vecTwoY = (EditText) findViewById(R.id.text_vector_2_y);
        EditText vecThreeX = (EditText) findViewById(R.id.text_vector_3_x);
        EditText vecThreeY = (EditText) findViewById(R.id.text_vector_3_y);

        Button calculateButton = (Button) findViewById(R.id.button_calculate);

        TextView vectorOneTextView = (TextView) findViewById(R.id.textView_Vector1);
        TextView vectorTwoTextView = (TextView) findViewById(R.id.textView_Vector2);
        TextView vectorThreeTextView = (TextView) findViewById(R.id.textView_Vector3);
        TextView results = (TextView) findViewById(R.id.text_result);

        vecOneX.setVisibility(View.VISIBLE);
        vecOneY.setVisibility(View.VISIBLE);
        vecTwoX.setVisibility(View.VISIBLE);
        vecTwoY.setVisibility(View.VISIBLE);
        vecThreeX.setVisibility(View.VISIBLE);
        vecThreeY.setVisibility(View.VISIBLE);
        vecThreeX.setText("");
        vecThreeY.setText("");

        calculateButton.setVisibility(View.VISIBLE);

        vectorOneTextView.setVisibility(View.VISIBLE);
        vectorTwoTextView.setVisibility(View.VISIBLE);
        vectorThreeTextView.setVisibility(View.VISIBLE);
        results.setVisibility(View.VISIBLE);
        results.setText("∠");

        setAdditionMode();
    }

    public void setScalarProduct(View view) {
        EditText vecOneX = (EditText) findViewById(R.id.text_vector_1_x);
        EditText vecOneY = (EditText) findViewById(R.id.text_vector_1_y);
        EditText vecTwoX = (EditText) findViewById(R.id.text_vector_2_x);
        EditText vecTwoY = (EditText) findViewById(R.id.text_vector_2_y);
        EditText vecThreeX = (EditText) findViewById(R.id.text_vector_3_x);
        EditText vecThreeY = (EditText) findViewById(R.id.text_vector_3_y);

        Button calculateButton = (Button) findViewById(R.id.button_calculate);

        TextView vectorOneTextView = (TextView) findViewById(R.id.textView_Vector1);
        TextView vectorTwoTextView = (TextView) findViewById(R.id.textView_Vector2);
        TextView vectorThreeTextView = (TextView) findViewById(R.id.textView_Vector3);
        TextView results = (TextView) findViewById(R.id.text_result);

        vecOneX.setVisibility(View.VISIBLE);
        vecOneY.setVisibility(View.VISIBLE);
        vecTwoX.setVisibility(View.VISIBLE);
        vecTwoY.setVisibility(View.VISIBLE);
        vecThreeX.setVisibility(View.INVISIBLE);
        vecThreeY.setVisibility(View.INVISIBLE);

        calculateButton.setVisibility(View.VISIBLE);

        vectorOneTextView.setVisibility(View.VISIBLE);
        vectorTwoTextView.setVisibility(View.VISIBLE);
        vectorThreeTextView.setVisibility(View.INVISIBLE);
        results.setVisibility(View.VISIBLE);
        results.setText("");

        setScalarProductMode();
    }

    public void setCrossProduct(View view) {
        EditText vecOneX = (EditText) findViewById(R.id.text_vector_1_x);
        EditText vecOneY = (EditText) findViewById(R.id.text_vector_1_y);
        EditText vecTwoX = (EditText) findViewById(R.id.text_vector_2_x);
        EditText vecTwoY = (EditText) findViewById(R.id.text_vector_2_y);
        EditText vecThreeX = (EditText) findViewById(R.id.text_vector_3_x);
        EditText vecThreeY = (EditText) findViewById(R.id.text_vector_3_y);

        Button calculateButton = (Button) findViewById(R.id.button_calculate);

        TextView vectorOneTextView = (TextView) findViewById(R.id.textView_Vector1);
        TextView vectorTwoTextView = (TextView) findViewById(R.id.textView_Vector2);
        TextView vectorThreeTextView = (TextView) findViewById(R.id.textView_Vector3);
        TextView results = (TextView) findViewById(R.id.text_result);

        vecOneX.setVisibility(View.VISIBLE);
        vecOneY.setVisibility(View.VISIBLE);
        vecTwoX.setVisibility(View.VISIBLE);
        vecTwoY.setVisibility(View.VISIBLE);
        vecThreeX.setVisibility(View.INVISIBLE);
        vecThreeY.setVisibility(View.INVISIBLE);

        calculateButton.setVisibility(View.VISIBLE);

        vectorOneTextView.setVisibility(View.VISIBLE);
        vectorTwoTextView.setVisibility(View.VISIBLE);
        vectorThreeTextView.setVisibility(View.INVISIBLE);
        results.setVisibility(View.VISIBLE);
        results.setText("");

        setCrossProductMode();
    }

    public void runCalculate(View view) {
        EditText vec1XText = (EditText) findViewById(R.id.text_vector_1_x);
        EditText vec1YText = (EditText) findViewById(R.id.text_vector_1_y);
        EditText vec2XText = (EditText) findViewById(R.id.text_vector_2_x);
        EditText vec2YText = (EditText) findViewById(R.id.text_vector_2_y);
        EditText vec3XText = (EditText) findViewById(R.id.text_vector_3_x);
        EditText vec3YText = (EditText) findViewById(R.id.text_vector_3_y);

        TextView vec1View = (TextView) findViewById(R.id.textView_Vector1);
        TextView vec2View = (TextView) findViewById(R.id.textView_Vector2);
        TextView vec3View = (TextView) findViewById(R.id.textView_Vector3);
        TextView results = (TextView) findViewById(R.id.text_result);

        Button calculateButton = (Button) findViewById(R.id.button_calculate);

        String result = calculate(vec1XText.getText().toString(), vec1YText.getText().toString(),
                                  vec2XText.getText().toString(), vec2YText.getText().toString(),
                                  vec3XText.getText().toString(), vec3YText.getText().toString());
        results.setText(result);
    }

    public static double[] vectorAddition(double[] vec1, double[] vec2, double[] vec3) {
        double[] results = new double[vec1.length];

        results[0] = vec1[0] + vec2[0] + vec3[0];
        results[1] = vec1[1] + vec2[1] + vec3[1];

        return results;
    }

    public static double scalarVectorProduct(double[] vec1, double[] vec2) {
        return (vec1[0] * vec2[0]) + (vec1[1] * vec2[1]);
    }

    public static double crossVectorProduct(double[] vec1, double[] vec2) {
        return (vec1[0] * vec2[1]) - (vec1[1] * vec2[0]);
    }

    public static double[] convertPolarToCartesianCoordinates(double[] polarCoord) {
        double[] results = new double[2];

        results[0] = polarCoord[0] * Math.cos(Math.toRadians(polarCoord[1]));
        results[1] = polarCoord[0] * Math.sin(Math.toRadians(polarCoord[1]));

        return results;
    }

    public static double[] convertCartesianToPolarCoordinates(double[] cartesianCoord) {
        double[] results = new double[2];

        results[0] = Math.sqrt((cartesianCoord[0] * cartesianCoord[0]) + (cartesianCoord[1] * cartesianCoord[1]));
        results[1] = Math.toDegrees(Math.atan2(cartesianCoord[1], cartesianCoord[0]));

        return results;
    }

    public static void setAdditionMode() {
        isAdditionMode = true;
        isScalarProductMode = false;
        isCrossProductMode = false;
    }

    public static void setScalarProductMode() {
        isAdditionMode = false;
        isScalarProductMode = true;
        isCrossProductMode = false;
    }

    public static void setCrossProductMode() {
        isAdditionMode = false;
        isScalarProductMode = false;
        isCrossProductMode = true;
    }

    public static void setPolarMode() {
        inPolarMode = true;
    }

    public static void setCartesianMode() {
        inPolarMode = false;
    }

    public static String calculate(String vec1X, String vec1Y, String vec2X, String vec2Y, String vec3X, String vec3Y) {
        if(!inPolarMode) {
            if (isAdditionMode) {
                double[] vec1 = new double[]{0.0, 0.0};
                double[] vec2 = new double[]{0.0, 0.0};
                double[] vec3 = new double[]{0.0, 0.0};

                if (!vec1X.isEmpty() && !vec1Y.isEmpty()) {
                    vec1 = new double[]{Double.parseDouble(vec1X), Double.parseDouble(vec1Y)};
                }
                if (!vec2X.isEmpty() && !vec2Y.isEmpty()) {
                    vec2 = new double[]{Double.parseDouble(vec2X), Double.parseDouble(vec2Y)};
                }
                if (!vec3X.isEmpty() && !vec3Y.isEmpty()) {
                    vec3 = new double[]{Double.parseDouble(vec3X), Double.parseDouble(vec3Y)};
                }

                double[] result = vectorAddition(vec1, vec2, vec3);
                return "{" + result[0] + ", " + result[1] + "}";
            } else if (isScalarProductMode) {
                double[] vec1 = new double[]{Double.parseDouble(vec1X), Double.parseDouble(vec1Y)};
                double[] vec2 = new double[]{Double.parseDouble(vec2X), Double.parseDouble(vec2Y)};

                return Double.toString(scalarVectorProduct(vec1, vec2));
            } else {
                double[] vec1 = new double[]{Double.parseDouble(vec1X), Double.parseDouble(vec1Y)};
                double[] vec2 = new double[]{Double.parseDouble(vec2X), Double.parseDouble(vec2Y)};

                return Double.toString(crossVectorProduct(vec1, vec2));
            }
        } else {
            if(isAdditionMode) {
                double[] vec1 = new double[]{0.0, 0.0};
                double[] vec2 = new double[]{0.0, 0.0};
                double[] vec3 = new double[]{0.0, 0.0};

                if (!vec1X.isEmpty() && !vec1Y.isEmpty()) {
                    vec1 = new double[]{Double.parseDouble(vec1X), Double.parseDouble(vec1Y)};
                    vec1 = convertPolarToCartesianCoordinates(vec1);
                }
                if (!vec2X.isEmpty() && !vec2Y.isEmpty()) {
                    vec2 = new double[]{Double.parseDouble(vec2X), Double.parseDouble(vec2Y)};
                    vec2 = convertPolarToCartesianCoordinates(vec2);
                }
                if (!vec3X.isEmpty() && !vec3Y.isEmpty()) {
                    vec3 = new double[]{Double.parseDouble(vec3X), Double.parseDouble(vec3Y)};
                    vec3 = convertPolarToCartesianCoordinates(vec3);
                }

                double[] result = vectorAddition(vec1, vec2, vec3);
                result = convertCartesianToPolarCoordinates(result);
                return String.format("%.2f", result[0]) + "∠" + String.format("%.2f", result[1]) + "°";
            }
            else if (isScalarProductMode) {
                double[] vec1 = new double[]{0.0, 0.0};
                double[] vec2 = new double[]{0.0, 0.0};

                vec1 = new double[]{Double.parseDouble(vec1X), Double.parseDouble(vec1Y)};
                vec2 = new double[]{Double.parseDouble(vec2X), Double.parseDouble(vec2Y)};

                vec1 = convertPolarToCartesianCoordinates(vec1);
                vec2 = convertPolarToCartesianCoordinates(vec2);

                return String.format("%.4f",scalarVectorProduct(vec1, vec2));
            }
            else {
                double[] vec1 = new double[]{0.0, 0.0};
                double[] vec2 = new double[]{0.0, 0.0};

                vec1 = new double[]{Double.parseDouble(vec1X), Double.parseDouble(vec1Y)};
                vec2 = new double[]{Double.parseDouble(vec2X), Double.parseDouble(vec2Y)};

                vec1 = convertPolarToCartesianCoordinates(vec1);
                vec2 = convertPolarToCartesianCoordinates(vec2);

                return String.format("%.4f",crossVectorProduct(vec1, vec2));
            }
        }
    }
}