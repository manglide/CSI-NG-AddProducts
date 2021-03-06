package ng.com.addproducts;

/**
 * Created by manglide on 4/10/2017.
 */


import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.Toast;


public class UpdateProducts extends AsyncTask<Object, Void, Boolean>  {
    Context cvn = null;
    View pgd = null;
    TabLayout tab_host = null;
    //Boolean showLoading = false;
    DBHandler jdbc;
    long val;

    public UpdateProducts(View progress, TabLayout tbl) {
        super();
        tab_host = tbl;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //showLoading = true;

    }

    @Override
    protected Boolean doInBackground(Object... params) {

        cvn = (Context)params[10];

        jdbc = new DBHandler(cvn);

        String product = (String)params[0];
        String product_category_txt = (String)params[1];
        String aboutproduct = (String)params[2];
        String manufacturer = (String)params[3];
        String address = (String)params[4];
        String ingredients = (String)params[5];
        String data_image1 = (String)params[6];
        String data_image2 = (String)params[7];
        String price = (String)params[8];
        String unique_identifier = (String)params[9];
        ////(product,product_category_txt,aboutproduct,manufacturer,address,ingredients)
        long result = jdbc.updateProduct(product,product_category_txt,aboutproduct,manufacturer,
                address,ingredients, data_image1, data_image2, price, unique_identifier);
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
            //pgd.setVisibility(View.GONE);
            Toast.makeText(cvn.getApplicationContext(),"Data Entered Successfully",Toast.LENGTH_LONG).show();
            tab_host.getTabAt(1).select();
        } else {
            //pgd.setVisibility(View.GONE);
            Toast.makeText(cvn.getApplicationContext(),"Database Server Busy, try again later.",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCancelled() {
        //pgd.setVisibility(View.GONE);
    }

}
