package com.herma.apps.textbooks.common;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.herma.apps.textbooks.ChaptersActivity;
import com.herma.apps.textbooks.R;

import java.util.ArrayList;
import java.util.List;

public class PostRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
  private static final int VIEW_TYPE_LOADING = 0;
  private static final int VIEW_TYPE_NORMAL = 1;
  private boolean isLoaderVisible = false;

  private List<Object> mPostItems;

  ViewGroup viewGroup;

  // The banner ad view type.
  private static final int BANNER_AD_VIEW_TYPE = 5;

  public PostRecyclerAdapter(List<Object> postItems) {
    this.mPostItems = postItems;
  }

  public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    viewGroup = parent;

    switch (viewType) {
      case VIEW_TYPE_NORMAL:
        return new ViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false));
      case VIEW_TYPE_LOADING:
        return new ProgressHolder(
            LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
      default:

        return null;
    }
  }

  @Override
  public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
    int viewType = getItemViewType(position);
    switch (viewType) {
      case BANNER_AD_VIEW_TYPE:
        // Replace holder.onBind(position); by native add
        holder.onBind(position);
        // fall through
      default:
        holder.onBind(position);

    }
  }

  @Override
  public int getItemCount() {
    return mPostItems == null ? 0 : mPostItems.size();
  }

  /**
   * Determines the view type for the given position.
   */
  @Override
  public int getItemViewType(int position) {

    if (isLoaderVisible) {
      return position == mPostItems.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
    } else {
        return VIEW_TYPE_NORMAL;
    }
  }

  public void addItems(ArrayList<Object> postItems) {
    mPostItems.addAll(postItems);
    notifyDataSetChanged();
  }

  public void addLoading() {
    isLoaderVisible = true;
    mPostItems.add(new PostItem());
    notifyItemInserted(mPostItems.size() - 1);
  }

  public void removeLoading() throws Exception {
    isLoaderVisible = false;
    int position = mPostItems.size() - 1;
    PostItem item = getItem(position);

    if (item != null) {
      mPostItems.remove(position);
      notifyItemRemoved(position);
    }else {
      System.out.println(item.getSubjectName());
    }

  }

  public void clear() {
    mPostItems.clear();
    notifyDataSetChanged();
  }

  PostItem getItem(int position) {
    return (PostItem) mPostItems.get(position);
  }

  public class ViewHolder extends BaseViewHolder {
    TextView textViewTitle;
    TextView textViewDescription;
    TextView txtPublished_at;

    ViewHolder(View itemView) {
      super(itemView);

    textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
    textViewDescription = (TextView) itemView.findViewById(R.id.textViewDescription);

    txtPublished_at = (TextView) itemView.findViewById(R.id.txtPublished_at);

    }

    protected void clear() {

    }

    public void onBind(int position) {
      super.onBind(position);
      try{
      final PostItem item = (PostItem) mPostItems.get(position);

        textViewTitle.setText(item.getSubjectName().trim());
        textViewDescription.setText(item.getSubjectChapters().length() + " Units");
        txtPublished_at.setText("   " + item.getSubjectGrade());

//       another option
      itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intent = new Intent(viewGroup.getContext(), ChaptersActivity.class);

          intent.putExtra("book_name", item.getSubjectName());
          intent.putExtra("courseDepartment", item.getSubjectGrade());
          intent.putExtra("courseChapters", item.getSubjectChapters().toString());
          intent.putExtra("courseEn", item.getSubjectEn());

          viewGroup.getContext().startActivity(intent);
        }
      });
    }catch(Exception lk){System.out.println("error on adapter : " + lk);}
    }

  }

  public static class ProgressHolder extends BaseViewHolder {
    ProgressHolder(View itemView) {
      super(itemView);
      ProgressBar progressBar = itemView.findViewById(R.id.progressBar);

    }

    @Override
    protected void clear() {
    }
  }
}
