package ng.com.addproducts;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Adeyemi Salau on 4/23/2017.
 */
public class ReviewAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> id_list = new ArrayList<String>();
    private ArrayList<String> list = new ArrayList<String>();
    private ArrayList<String> rating = new ArrayList<String>();
    private Context context;
    private Cursor csr;
    private static final String REVIEWED_TITLE = "product_id";

    private static final String REVIEWED_ID = "_id";

    private static final String RATING = "rating";

    private static DBHandler jdbc = null;

    public ReviewAdapter(Cursor mCursor, Context context) {
        this.csr = mCursor;
        int iCMID = mCursor.getColumnIndex(REVIEWED_ID);
        int iCMTitle = mCursor.getColumnIndex(REVIEWED_TITLE);
        int iCMRating = mCursor.getColumnIndex(RATING);
        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            list.add(mCursor.getString(iCMTitle));
            id_list.add(mCursor.getString(iCMID));
            //rating.add(mCursor.getString(iCMRating) + " Star(s)");
            rating.add(mCursor.getString(iCMRating));
        }
        this.context = context;
        this.jdbc = new DBHandler(ApplicationName.getAppContext());
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        //return Long.parseLong(list.get(pos));
        return 0;
        //return Long.parseLong(mProjectsList.get(position).get("ID")) ;
        //just return 0 if your list items do not have an Id variable.
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.reviews_row, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.individual_product_reviewed);
        // Set ID
        view.setTag(id_list.get(position));
        // Set Title
        listItemText.setText(list.get(position));
        // Set the Rating TextView
        RatingBar rate = (RatingBar)view.findViewById(R.id.viewreview);
        //rate.setText(rating.get(position));
        rate.setRating(Float.valueOf(rating.get(position)));
        //Handle buttons and add onClickListeners
        Button deleteBtn = (Button)view.findViewById(R.id.review_delete_button);


        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getRootView().getContext());
                alertDialog.setCancelable(false);
                alertDialog.setMessage("Delete Reviewed item?");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        RelativeLayout rl = (RelativeLayout) v.getParent();
                        TextView tv = (TextView)rl.findViewById(R.id.individual_product_reviewed);
                        String text = tv.getText().toString();
                        String id_of_item = id_list.get(position);
                        if(jdbc.removeProductReviewed(text,id_of_item)) {
                            list.remove(position);
                            id_list.remove(position);
                            rating.remove(position);
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(ApplicationName.getAppContext(),"Error in deleting",Toast.LENGTH_LONG).show();
                        }
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).show();
            }
        });
        //Button deleteBtn = (Button)view.findViewById(R.id.review_delete_button);
        notifyDataSetChanged();
        return view;
    }
}
