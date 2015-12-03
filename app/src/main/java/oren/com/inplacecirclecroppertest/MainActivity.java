package oren.com.inplacecirclecroppertest;

import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final CropCircleView cropCircleView = (CropCircleView) findViewById(R.id.test1);
        final ImageView imageView = (ImageView) findViewById(R.id.test2);


        cropCircleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v)
            {
                final Bitmap crop = cropCircleView.crop();
                imageView.setImageBitmap(crop);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v)
            {
                imageView.setImageBitmap(cropCircleView.crop());
            }
        });

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v)
            {
                try
                {
                    final int i = Integer.parseInt(((EditText) findViewById(R.id.size_field)).getText().toString());
                    cropCircleView.setSidePadding(i);
                }
                catch (Throwable t)
                {
                    Log.e("moo", "parse issue " + t);
                }


            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
