package com.herma.apps.textbooks.common;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.herma.apps.textbooks.R;

import java.util.List;

public class GradeAdapter
        extends RecyclerView.Adapter<GradeAdapter.MyView> {

    // List with String type
    private List<GradeItem> list;

    protected OnGradeItemListener mListener;

    ViewGroup viewGroup;
    GradeItem gradeItem;

    // Constructor for adapter class
    // which takes a list of String type
    public GradeAdapter(List<GradeItem> horizontalList, OnGradeItemListener itemListener)
    {
        this.list = horizontalList;
        mListener = itemListener;
    }
    // View Holder class which
    // extends RecyclerView.ViewHolder
    public class MyView
            extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Text View
        TextView textView;

        // parameterised constructor for View Holder class
        // which takes the view as a parameter
        public MyView(View view)
        {
            super(view);

            view.setOnClickListener(this);
            // initialise TextView with id
            textView = (TextView)view
                    .findViewById(R.id.textview);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                gradeItem = list.get(getAdapterPosition());
                mListener.onItemClick(gradeItem);
            }
        }
    }


    // Override onCreateViewHolder which deals
    // with the inflation of the card layout
    // as an item for the RecyclerView.
    @Override
    public MyView onCreateViewHolder(ViewGroup parent,
                                     int viewType)
    {

        // Inflate item.xml using LayoutInflator
        View itemView
                = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.grade_item,
                        parent,
                        false);

        viewGroup = parent;

        // return itemView
        return new MyView(itemView);
    }

    // Override onBindViewHolder which deals
    // with the setting of different data
    // and methods related to clicks on
    // particular items of the RecyclerView.
    @Override
    public void onBindViewHolder(final MyView holder,
                                 final int position)
    {

        // Set the text of each item of
        // Recycler view with the list items
        holder.textView.setText(list.get(position).gradeName);
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(viewGroup.getContext(), list.get(position), Toast.LENGTH_SHORT).show();
//                System.out.println(list.get(position));
//            }
//        });
    }

    // Override getItemCount which Returns
    // the length of the RecyclerView.
    @Override
    public int getItemCount()
    {
        return list.size();
    }

    public interface OnGradeItemListener {
        void onItemClick(GradeItem item);
    }
}