package ng.com.addproducts;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;


/**
 * Created by manglide on 4/16/2017.
 */
public class AddProducts extends AsyncTask<Object, Void, Boolean>  {

    Context cvn = null;
    View pgd = null;
    TabLayout tab_host = null;
    Boolean showLoading = false;
    long val;

    public AddProducts(View progress, TabLayout tbl) {
        super();
        pgd = progress;
        tab_host = tbl;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //pgd.setVisibility(View.VISIBLE);
        showLoading = true;

    }

    @Override
    protected Boolean doInBackground(Object... params) {

            cvn = (Context)params[9];


            DBHandler jdbc = new DBHandler(cvn);

            String product_name = (String)params[0];
            String product_category = (String)params[1];
            String product_manufacturer = (String)params[2];
            /*byte[] data_image1 = getBitmapAsByteArray((Bitmap)params[3]);
            byte[] data_image2 = getBitmapAsByteArray((Bitmap)params[4]);
            byte[] data_image3 = getBitmapAsByteArray((Bitmap)params[5]);
            byte[] data_image4 = getBitmapAsByteArray((Bitmap)params[6]);
            byte[] data_image5 = getBitmapAsByteArray((Bitmap)params[7]);*/
            String data_image1 = (String)params[3];
            String data_image2 = (String)params[4];
            //String data_image3 = (String)params[5];
            //String data_image4 = (String)params[6];
            //String data_image5 = (String) params[7];
            String competitor_one = (String)params[5];
            String competitor_one_man = (String)params[6];
            String competitor_two = (String)params[7];
            String competitor_two_man = (String)params[8];
            //String competitor_three = (String)params[9];
            //String competitor_three_man = (String)params[10];
            String identifier = (String)params[10];
            Double price = (Double)params[11];
            if(competitor_two.equalsIgnoreCase("")) competitor_two = "";
            if(competitor_two_man.equalsIgnoreCase("")) competitor_two_man = "";
            //if(competitor_three.equalsIgnoreCase("")) competitor_three = "";
            //if(competitor_three_man.equalsIgnoreCase("")) competitor_three_man = "";

            long result = jdbc.addProduct(product_name,product_category,
                    product_manufacturer,data_image1,data_image2,
                    competitor_one,competitor_one_man,competitor_two,
                    competitor_two_man,identifier,price);

            if(result <= 0) {
                val = result;
                return false;
            } else {
                val = result;
                return true;
            }


    }

    @Override
    protected void onPostExecute(final Boolean success) {
        //new Dashboard().showProgress(false);

        if(success) {
            pgd.setVisibility(View.GONE);
            Toast.makeText(cvn.getApplicationContext(),"Data Entered Successfully",Toast.LENGTH_LONG).show();
            tab_host.getTabAt(1).select();
        } else {
            pgd.setVisibility(View.GONE);
            Toast.makeText(cvn.getApplicationContext(),"Database Server Busy, try again later.",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCancelled() {
        pgd.setVisibility(View.GONE);
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
}
