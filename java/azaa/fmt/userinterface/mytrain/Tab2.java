package azaa.fmt.userinterface.mytrain;

/**
 * Created by Azaa on 2/8/17.
 */

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Tab2 extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //View rootView = inflater.inflate(R.layout.tab2western, container, false);

        View rootView = inflater.inflate(R.layout.tab2western, container, false);
        TableLayout tableLayout = (TableLayout)rootView.findViewById(R.id.tab2table);
        String[] Western;
        Resources res = getResources();
        Western = res.getStringArray(R.array.Western);

        int  px50 = (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 35, res.getDisplayMetrics()));

        int  px100 = (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 80, res.getDisplayMetrics()));

        for (int i = 0; i < Western.length; i++) {
            TableRow row = new TableRow(rootView.getContext());

            
            //ImageView imageViewLeft = new ImageView(rootView.getContext());
            //imageViewLeft.setImageResource(R.drawable.track);
            //imageViewLeft.getLayoutParams().height = 30;
            Bitmap bMap = BitmapFactory.decodeResource(getResources(), R.drawable.track);
            // Resize the bitmap to 150x100 (width x height)
            Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, px50, px100, true);
            // Loads the resized Bitmap into an ImageView
            ImageView imageViewLeft = new ImageView(rootView.getContext());
            imageViewLeft.setImageBitmap(bMapScaled);


            TextView textView = new TextView(rootView.getContext());
            //String[] Western;
            //Resources res = getResources();
            //Western = res.getStringArray(R.array.Western);
            textView.setText(Western[i]);
            //textView.getResources().getStringArray(R.array.Central);
            textView.setTextSize(17);
            textView.setTextColor(getResources().getColor(R.color.colortext));
            textView.setGravity(1);

            //ImageView imageViewRight = new ImageView(rootView.getContext());
            //imageViewRight.setImageResource(R.drawable.track);
            //imageViewRight.getLayoutParams().height = 30;
            Bitmap bMap1 = BitmapFactory.decodeResource(getResources(), R.drawable.track);
            // Resize the bitmap to 150x100 (width x height)
            Bitmap bMapScaled1 = Bitmap.createScaledBitmap(bMap, px50, px100, true);
            // Loads the resized Bitmap into an ImageView
            ImageView imageViewRight = new ImageView(rootView.getContext());
            imageViewRight.setImageBitmap(bMapScaled1);

            row.addView(imageViewLeft);
            row.addView(textView);
            row.addView(imageViewRight);

            tableLayout.addView(row);
        }
        return rootView;
    }
}
