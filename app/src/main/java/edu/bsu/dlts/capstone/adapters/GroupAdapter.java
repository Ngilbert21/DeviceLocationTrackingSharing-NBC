package edu.bsu.dlts.capstone.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import edu.bsu.dlts.capstone.models.Group;
import edu.bsu.dlts.capstone.R;
import edu.bsu.dlts.capstone.activities.BrandNewGroupActivity;

/**
 * This adapter allows groups to be displayed in a list
 */
public class GroupAdapter extends ArrayAdapter<Group> {
    /**
     * Adapter context
     */
    Context mContext;

    /**
     * Adapter View layout
     */
    int mLayoutResourceId;

    public GroupAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
    }

    /**
     * Returns the view for a specific item on the list
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final Group currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
        }

        row.setTag(currentItem);
        final CheckBox checkBox = (CheckBox) row.findViewById(R.id.checkToDoItem);
        checkBox.setText(currentItem.getName());
        checkBox.setChecked(false);
        checkBox.setEnabled(true);

        checkBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (checkBox.isChecked()) {
                    checkBox.setEnabled(false);
                    if (mContext instanceof BrandNewGroupActivity) {
                        BrandNewGroupActivity activity = (BrandNewGroupActivity) mContext;
                        String groupName = checkBox.getText().toString();
                        activity.changePage(groupName);
                    }
                }
            }
        });

        return row;
    }
}
