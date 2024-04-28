package com.herma.apps.textbooks.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.herma.apps.textbooks.MainActivity;
import com.herma.apps.textbooks.R;
import com.herma.apps.textbooks.SplashActivity;
import com.herma.apps.textbooks.common.GradeAdapter;
import com.herma.apps.textbooks.common.GradeItem;
import com.herma.apps.textbooks.common.MainAdapter;
import com.herma.apps.textbooks.common.PaginationListener;
import com.herma.apps.textbooks.common.PostItem;
import com.herma.apps.textbooks.common.PostRecyclerAdapter;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AllNewCurriculumBooks extends Fragment
{
    LinearLayoutManager layoutManager;
    RecyclerView mRecyclerView;
    public PostRecyclerAdapter adapter;
    public int nextPageNumber = 1;
    public boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;
    String url,
    defult = SplashActivity.BASEAPI+ "ds_bm/v1/";
    public RequestQueue queue;
    public int random = 0;
    public String searchQuery = "Grade 12", loadType = "all";
    PostItem postItem;


    ///////////////////////
// Recycler View object
    RecyclerView recyclerViewGrade;
    // Array list for recycler view data source
    ArrayList<GradeItem> source;
    // Layout Manager
    RecyclerView.LayoutManager gradeRecyclerViewLayoutManager;
    // adapter class object
    GradeAdapter adapterGrade;
    // Linear Layout Manager
    LinearLayoutManager HorizontalLayoutGrade;

    SharedPreferences pre;
    ////////////////////////////////

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_allbooks, container, false);


        mRecyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);

        url = defult;

        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        adapter = new PostRecyclerAdapter(new ArrayList<Object>());
        mRecyclerView.setAdapter(adapter);
        adapter.addLoading();

//        doApiCall();

        /**
         * add scroll listener while user reach in bottom load more will call
         */
        mRecyclerView.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                nextPageNumber++;
//                doApiCall();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        pre = PreferenceManager.getDefaultSharedPreferences(getContext());

        String pageType = getArguments().getString("type");
        String userChoosedGradeT = "Blockchain Tech";
        if(pageType.equals("blockchain")){
            searchQuery = "15";
        }else {

            try {
                searchQuery = Integer.parseInt(pre.getString("grade_"+pageType, "12")) + "";

            } catch (Exception e) {
                searchQuery = "12";
            }
            initialiseGrade(pageType, root);

        }
        ArrayList arrayList = new MainActivity().getData(getActivity(),searchQuery, pageType);
        MainAdapter adapter = new MainActivity().setData(getActivity(), arrayList, userChoosedGradeT);

        mRecyclerView.setAdapter(adapter);

    return root;
    }

    private void initialiseGrade(String type, View root) {
        // initialisation with id's
        recyclerViewGrade
                = (RecyclerView) root.findViewById(
                R.id.recyclerviewGrade);
        gradeRecyclerViewLayoutManager
                = new LinearLayoutManager(
                getContext());
        // Set LayoutManager on Recycler View
        recyclerViewGrade.setLayoutManager(
                gradeRecyclerViewLayoutManager);
        // Adding items to RecyclerView.
        ArrayList<GradeItem> source = new ArrayList<GradeItem>();
        if(type.equals("old")){

            source.add(new GradeItem(1,"Grade 12"));
            source.add(new GradeItem(2,"Grade 11"));
            source.add(new GradeItem(3,"Grade 10"));
            source.add(new GradeItem(4,"Grade 9"));
            source.add(new GradeItem(5,"Grade 8"));
            source.add(new GradeItem(6,"Grade 7"));
            source.add(new GradeItem(7,"Grade 6"));
            source.add(new GradeItem(8,"Grade 5"));
            source.add(new GradeItem(11,"Grade 4"));
            source.add(new GradeItem(12,"Grade 3"));
            source.add(new GradeItem(13,"Grade 2"));
            source.add(new GradeItem(14,"Grade 1"));
            source.add(new GradeItem(9,"Grade 12 T. Guide"));
            source.add(new GradeItem(10,"Grade 11 T. Guide"));
            source.add(new GradeItem(15,"Grade 10 T. Guide"));
            source.add(new GradeItem(16,"Grade 9 T. Guide"));
            source.add(new GradeItem(17,"Grade 8 T. Guide"));
            source.add(new GradeItem(18,"Grade 7 T. Guide"));
        }else {
            source.add(new GradeItem(12, "Grade 12"));
            source.add(new GradeItem(11, "Grade 11"));
            source.add(new GradeItem(10, "Grade 10"));
            source.add(new GradeItem(9, "Grade 9"));
            source.add(new GradeItem(8, "Grade 8"));
            source.add(new GradeItem(7, "Grade 7"));
            source.add(new GradeItem(6, "Grade 6"));
            source.add(new GradeItem(5, "Grade 5"));
            source.add(new GradeItem(4, "Grade 4"));
            source.add(new GradeItem(3, "Grade 3"));
            source.add(new GradeItem(2, "Grade 2"));
            source.add(new GradeItem(1, "Grade 1"));
        }

        adapterGrade = new GradeAdapter(source, new GradeAdapter.OnGradeItemListener() {
            @Override
            public void onItemClick(GradeItem item) {

                nextPageNumber = 1;
                adapter.clear();
//                doApiCall();

                ArrayList arrayList = new MainActivity().getData(getActivity(), item.id + "", type);
                MainAdapter adapter = new MainActivity().setData(getActivity(), arrayList, item.gradeName);

                mRecyclerView.setAdapter(adapter);

                pre.edit().putString("grade_"+type, item.id+"").apply();
            }
        });
        // Set Horizontal Layout Manager
        // for Recycler view
        HorizontalLayoutGrade
                = new LinearLayoutManager(
                getActivity(),
                LinearLayoutManager.HORIZONTAL,
                false);
        recyclerViewGrade.setLayoutManager(HorizontalLayoutGrade);
        // Set adapter on recycler view
        recyclerViewGrade.setAdapter(adapterGrade);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        System.out.println("Inside this method");
        if (queue != null) {
//            System.out.println("Inside second this method");
            queue.stop();
//            queue.cancelAll(this);
        }
    }
    private void doApiCall() {
//        if(haveNext == false) {
//            adapter.removeLoading();
//            Toast.makeText(getContext(), "This is the last page!", Toast.LENGTH_LONG).show();
//        }
//        else{
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    if (queue == null ) {
                        queue = Volley.newRequestQueue(getContext());
                    }

                    StringRequest stringRequest;

                    loadType = "search/books/1-12-textbooks/"+nextPageNumber+"?search_query="+searchQuery;

                        stringRequest = new StringRequest(Request.Method.GET, url+loadType,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    if (response != null) {
                                        try {
//                                            parseAllBooks(response);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                  System.out.println("That didn't work! " + error);
                        try{
//                            Toast.makeText(getContext(), "That didn't work! " + error, Toast.LENGTH_LONG).show();

                            if (!isOnline()) {
                                showNetworkDialog(false);
                            }
                            else if (error instanceof TimeoutError || error.getCause() instanceof SocketTimeoutException
                                    || error.getCause() instanceof ConnectTimeoutException
                                    || error.getCause() instanceof SocketException
                                    || (error.getCause().getMessage() != null
                                    && error.getCause().getMessage().contains("Connection timed out"))) {
                                Toast.makeText(getActivity(), "Connection timeout error. \npls Swipe to reload",
                                        Toast.LENGTH_LONG).show();


                            } else {
                                Toast.makeText(getActivity(), "An unknown error occurred.\npls swap to refresh",
                                        Toast.LENGTH_LONG).show();
System.out.println("Error on sys:"+error);
                                try{
                                }catch(Exception k){}

                            }
                        }catch (Exception j){}
                        }

                    })
                    {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getContext());
                            if(!pre.getString("token", "None").equalsIgnoreCase("None"))
                                params.put("Authorization", "Bearer "+pre.getString("token", "None"));

                            return params;
                        }
                    };

                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                            10000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                    stringRequest.setTag(this);
// Add the request to the RequestQueue.
                    queue.add(stringRequest);
                }
            }, 1500);
//        }
    }
    /**
     * Check if there is the network connectivity
     *
     * @return true if connected to the network
     */
    public boolean isOnline() {
        // Get a reference to the ConnectivityManager to check the state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * Show a dialog when there is no internet connection
     *
     * @param isOnline true if connected to the network
     */
    public void showNetworkDialog(final boolean isOnline) {
            // Create an AlertDialog.Builder
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppTheme_Dark);
            // Set an Icon and title, and message
            builder.setIcon(R.drawable.ic_warning);
            builder.setTitle(getString(R.string.no_network_title));
            builder.setMessage(getString(R.string.no_network_message));
            builder.setPositiveButton(getString(R.string.go_to_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 1234);
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), null);

            // Create and show the AlertDialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

    }
}
