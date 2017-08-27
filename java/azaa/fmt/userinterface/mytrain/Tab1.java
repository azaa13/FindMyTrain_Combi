package azaa.fmt.userinterface.mytrain;

/**
 * Created by Azaa on 2/8/17.
 */
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class Tab1 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1central, container, false);
        TableLayout tableLayout = (TableLayout)rootView.findViewById(R.id.tab1table);
        String[] Central;
        String results="";
        String val = "1 + \"\"";
        String url = "http://10.129.28.168:8007/FindMyTrain/Reporter";
        Resources res = getResources();
        Central = res.getStringArray(R.array.Central);
        for (int i = 0; i < Central.length; i++) {
            TableRow row = new TableRow(rootView.getContext());

            //ImageView imageViewLeft = new ImageView(rootView.getContext());
            //imageViewLeft.setImageResource(R.drawable.track);
            int  px50 = (int) (TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 35, res.getDisplayMetrics()));

            int  px100 = (int) (TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 80, res.getDisplayMetrics()));


            // Load a bitmap from the drawable folder
            Bitmap bMap = BitmapFactory.decodeResource(getResources(), R.drawable.track);
            // Resize the bitmap to 150x100 (width x height)
            Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, px50, px100, true);
            // Loads the resized Bitmap into an ImageView
            ImageView imageViewLeft = new ImageView(rootView.getContext());
            imageViewLeft.setImageBitmap(bMapScaled);



            //imageViewLeft.getLayoutParams().height = 30;

            TextView textView = new TextView(rootView.getContext());
            textView.setText(Central[i]);
            //textView.getResources().getStringArray(R.array.Central);
            textView.setTextSize(17);
            textView.setTextColor(getResources().getColor(R.color.colortext));
            textView.setGravity(1);

            //ImageView imageViewRight = new ImageView(rootView.getContext());
            //imageViewRight.setImageResource(R.drawable.track);

            Bitmap bMap1 = BitmapFactory.decodeResource(getResources(), R.drawable.track);
            // Resize the bitmap to 150x100 (width x height)
            Bitmap bMapScaled1 = Bitmap.createScaledBitmap(bMap, px50, px100, true);
            // Loads the resized Bitmap into an ImageView
            ImageView imageViewRight = new ImageView(rootView.getContext());
            imageViewRight.setImageBitmap(bMapScaled1);
            //Bitmap bitmap = ((BitmapDrawable)dretMap();
           // imageViewRight.setImageBitmap(Bitmap.createScaledBitmap(bitmap,30,40,false));
            //imageViewRight.getLayoutParams().height = 30;


            row.addView(imageViewLeft);
            row.addView(textView);
            row.addView(imageViewRight);

            tableLayout.addView(row);
        }
        return rootView;

       

    }
}