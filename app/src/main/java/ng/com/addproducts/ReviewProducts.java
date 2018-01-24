package ng.com.addproducts;

/**
 * Created by manglide on 4/22/2017.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.Toast;


public class ReviewProducts extends AsyncTask<Object, Void, Boolean>  {
    Context cvn = null;
    View pgd = null;
    TabLayout tab_host = null;
    //Boolean showLoading = false;
    DBHandler jdbc;
    long val;

    public ReviewProducts(View progress, TabLayout tbl) {
        super();
        //pgd = progress;
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

        String product_name = (String)params[0];
        String product_category = (String)params[1];
        String likes = (String)params[2];
        String dislikes = (String)params[3];
        String rate = (String)params[4];
        String userfullcomments = (String)params[5];
        Double latitude = (Double) params[6];
        Double longitude = (Double) params[7];
        String date = (String)params[8];
        String user = (String)params[9];
        String identifier = (String)params[11];
        long result = jdbc.addReview(product_name,product_category,likes,dislikes,rate,userfullcomments,latitude,longitude,date,user,identifier);
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
