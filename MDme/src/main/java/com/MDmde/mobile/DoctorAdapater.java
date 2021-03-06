package com.MDmde.mobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Matt Hamada on 4/10/14.
 */
public class DoctorAdapater extends BaseAdapter
{
    private Context mContext;
    private List<Doctor> mDoctors;
    LayoutInflater mInflater;


    public DoctorAdapater(Context context, List<Doctor> doctors)
    {
        mContext = context;
        mDoctors = doctors;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount()
    {
        return mDoctors.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mDoctors.get(position);
    }

    public String getFullName(int position)
    {
        return mDoctors.get(position).getFullName();
    }

    public String getPhotoUrl(int position)
    {
        return mDoctors.get(position).getPhotoUrl();
    }

    public int getDoctorId(int position)
    {
        return mDoctors.get(position).getId();
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    //create new view for each doctor item
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = null;
        if (convertView == null)
        {

            view = mInflater.inflate(R.layout.doctor_name_image_view, null);
        }
        else
        {
            view = convertView;
        }

        TextView name = (TextView)view.findViewById(R.id.doctor_index_text);
        name.setText(mDoctors.get(position).getFullName());
        ImageView image = (ImageView)view.findViewById(R.id.doctor_index_image);
        new DownloadImageTask(image).execute(mDoctors.get(position).getPhotoUrl());

        return view;

    }
}
