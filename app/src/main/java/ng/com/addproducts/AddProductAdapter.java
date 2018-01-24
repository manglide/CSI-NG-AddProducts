package ng.com.addproducts;

/**
 * Created by manglide on 04/10/2017.
 */

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class AddProductAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> id_list = new ArrayList<String>();
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;
    private Cursor csr;
    private static final String REVIEWED_TITLE = "title";

    private static final String REVIEWED_ID = "_id";

    private static DBHandler jdbc = null;

    public AddProductAdapter(Cursor mCursor, Context context) {
        this.csr = mCursor;
        int iCMID = mCursor.getColumnIndex(REVIEWED_ID);
        int iCM = mCursor.getColumnIndex(REVIEWED_TITLE);
        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            list.add(mCursor.getString(iCM));
            id_list.add(mCursor.getString(iCMID));
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
            view = inflater.inflate(R.layout.addedproductrow, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.individual_product);
        listItemText.setText(list.get(position));

        //Handle buttons and add onClickListeners
        //Button deleteBtn = (Button)view.findViewById(R.id.delete_button);
        /*
        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View v) {
                //do something
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getRootView().getContext());
                alertDialog.setCancelable(false);
                alertDialog.setMessage("Delete Product?");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        RelativeLayout rl = (RelativeLayout) v.getParent();
                        TextView tv = (TextView)rl.findViewById(R.id.individual_product);
                        String text = tv.getText().toString();
                        String id_of_item = id_list.get(position);
                        if(jdbc.removeProduct(text,id_of_item)) {
                            list.remove(position);
                            id_list.remove(position);
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
        */

        return view;
    }
}

