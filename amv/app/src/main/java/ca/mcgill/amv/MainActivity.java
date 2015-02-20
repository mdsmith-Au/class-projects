package ca.mcgill.amv;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        calculateButton.setVisibility(View.VISIBLE);

        vectorOneTextView.setVisibility(View.VISIBLE);
        vectorOneTextView.setText("Vector 1:");
        vectorTwoTextView.setVisibility(View.VISIBLE);
        vectorThreeTextView.setVisibility(View.VISIBLE);
        results.setVisibility(View.INVISIBLE);
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
        vecOneY.setVisibility(View.INVISIBLE);
        vecTwoX.setVisibility(View.VISIBLE);
        vecTwoY.setVisibility(View.VISIBLE);
        vecThreeX.setVisibility(View.INVISIBLE);
        vecThreeY.setVisibility(View.INVISIBLE);

        calculateButton.setVisibility(View.VISIBLE);

        vectorOneTextView.setVisibility(View.VISIBLE);
        vectorOneTextView.setText("Scalar:");
        vectorTwoTextView.setVisibility(View.VISIBLE);
        vectorThreeTextView.setVisibility(View.INVISIBLE);
        results.setVisibility(View.INVISIBLE);
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
        vectorOneTextView.setText("Vector 1:");
        vectorTwoTextView.setVisibility(View.VISIBLE);
        vectorThreeTextView.setVisibility(View.INVISIBLE);
        results.setVisibility(View.INVISIBLE);
    }
}
