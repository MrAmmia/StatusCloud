package net.thebookofcode.www.statuscloud.ui.upload;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Parcelable;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import net.thebookofcode.www.statuscloud.Global;
import net.thebookofcode.www.statuscloud.ImageSaver;
import net.thebookofcode.www.statuscloud.R;
import net.thebookofcode.www.statuscloud.RecyclerviewOnClickListener;
import net.thebookofcode.www.statuscloud.SharedViewModel;
import net.thebookofcode.www.statuscloud.ui.saved.SavedRecyclerViewAdapter;

import java.io.File;
import java.util.ArrayList;

public class UploadRecyclerViewAdapter  extends RecyclerView.Adapter<UploadRecyclerViewAdapter.MyViewHolder> implements Filterable {

    Context mContext;
    Activity activity;
    private RecyclerviewOnClickListener recyclerviewOnClickListener;
    public ArrayList<File> mMediaFiles = new ArrayList<>();
    public ArrayList<File> mMediaFilesFull;
    ArrayList<String> imageNames = new ArrayList<>();
    SharedViewModel viewModel;
    ArrayList<Bitmap> mImageSource;
    ImageSaver imageSaver;
    Boolean isEnabled = false;
    Boolean isSelectAll = false;
    ArrayList<File> selectList = new ArrayList<>();
    public Boolean StatusText = true;
    private ArrayList<String> uriArray = new ArrayList<>();

    public UploadRecyclerViewAdapter(Activity activity, Context mContext, ImageSaver imageSaver/*ArrayList<String> uriArray*/) {
        this.mContext = mContext;
        //this.mMediaFiles = mMediaFiles;
        this.imageSaver = imageSaver;
        this.activity = activity;
        //this.uriArray = uriArray;
    }

    @NonNull
    @Override
    public UploadRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.upload_grid_layout, parent, false);
        UploadRecyclerViewAdapter.MyViewHolder vHolder = new UploadRecyclerViewAdapter.MyViewHolder(v);
        //SharedViewModelFactory sharedViewModelFactory = new SharedViewModelFactory(imageSaver);
        viewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(SharedViewModel.class);
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UploadRecyclerViewAdapter.MyViewHolder holder, int position) {
        if(StatusText){
            holder.txtName.setVisibility(View.VISIBLE);
            holder.txtName.setText(imageNames.get(position));
        }
        holder.thumbnail.setImageBitmap(imageSaver.singleImage(mMediaFiles.get(position)));
        holder.uploadShareMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(mContext,holder.uploadShareMenu);
                popup.getMenuInflater().inflate(R.menu.upload_share_popup,popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        switch (id){
                            case R.id.share:
                                Intent mediaIntent = new Intent();
                                mediaIntent.setAction(Intent.ACTION_SEND);
                                mediaIntent.putExtra(Intent.EXTRA_STREAM, (Parcelable) mMediaFiles.get(position));
                                Intent shareMediaIntent = Intent.createChooser(mediaIntent, null);
                                mContext.startActivity(shareMediaIntent);
                                break;
                            case R.id.shareUrl:
                               /*
                                Intent uriIntent = new Intent();
                                uriIntent.setAction(Intent.ACTION_SEND);
                                uriIntent.putExtra(Intent.EXTRA_TEXT, uriArray.get(position));
                                uriIntent.setType("text/plain");

                                Intent shareUriIntent = Intent.createChooser(uriIntent, null);
                                mContext.startActivity(shareUriIntent);
                                */
                        }
                        return true;
                    }
                });
            }
        });
        holder.gridLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                holder.uploadShareMenu.setVisibility(View.GONE);
                holder.countdown.setVisibility(View.GONE);
                if (!isEnabled) {
                    //If Action Mode is Disabled
                    ActionMode.Callback callback = new ActionMode.Callback() {
                        @Override
                        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                            MenuInflater menuInflater = actionMode.getMenuInflater();
                            menuInflater.inflate(R.menu.action_menu_two, menu);
                            Global.sActionMode = actionMode;
                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                            //when action mode is enabled
                            isEnabled = true;
                            ClickItem(holder);

                            viewModel.getText().observe((LifecycleOwner) activity, new Observer<String>() {
                                @Override
                                public void onChanged(String s) {
                                    actionMode.setTitle(String.format("%s Selected", s));
                                }
                            });
                            return true;
                        }


                        @Override
                        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                            int id = menuItem.getItemId();
                            switch (id) {
                                case R.id.menu_delete:
                                    new AlertDialog.Builder(mContext).setTitle("Delete Media?")
                                            .setMessage("Do you want to delete selected media\nThis may be irreversible")
                                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    for (File f : selectList) {
                                                        mMediaFiles.remove(f);
                                                        f.delete();
                                                        actionMode.finish();
                                                    }
                                                }
                                            })
                                            .setNegativeButton("NO", null).show();

                                    break;
                                case R.id.menu_select_all:
                                    if (selectList.size() == mMediaFiles.size()) {
                                        isSelectAll = false;
                                        selectList.clear();
                                    } else {
                                        selectList.clear();
                                        selectList.addAll(mMediaFiles);
                                        isSelectAll = true;
                                    }
                                    viewModel.setText(String.valueOf(selectList.size()));
                                    notifyDataSetChanged();
                                    break;
                                case R.id.menu_share:
                                    //to do later
                                    break;
                            }
                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode actionMode) {
                            isEnabled = false;
                            isSelectAll = false;
                            selectList.clear();
                            Global.sActionMode = null;
                            notifyDataSetChanged();
                        }
                    };
                    ((AppCompatActivity) view.getContext()).startActionMode(callback);
                } else {
                    ClickItem(holder);

                }
                return true;
            }
        });
        holder.gridLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEnabled) {
                    ClickItem(holder);
                } else {
                    recyclerviewOnClickListener.onItemClick(holder.getAdapterPosition());
                    notifyDataSetChanged();

                }
            }
        });
        if(isSelectAll){
            holder.imageChecked.setVisibility(View.VISIBLE);
            holder.gridLayout.setBackgroundColor(Color.LTGRAY);
        }else{
            holder.imageChecked.setVisibility(View.GONE);
            holder.gridLayout.setBackgroundColor(Color.TRANSPARENT);
            holder.countdown.setVisibility(View.VISIBLE);
            holder.uploadShareMenu.setVisibility(View.INVISIBLE);
        }
    }

    private void ClickItem(UploadRecyclerViewAdapter.MyViewHolder holder) {
        File f = mMediaFiles.get(holder.getAdapterPosition());
        //int position = holder.getAdapterPosition();
        if (holder.imageChecked.getVisibility() == View.GONE) {
            holder.imageChecked.setVisibility(View.VISIBLE);
            holder.gridLayout.setBackgroundColor(Color.LTGRAY);
            selectList.add(f);
        } else {
            holder.imageChecked.setVisibility(View.GONE);
            holder.gridLayout.setBackgroundColor(Color.TRANSPARENT);
            selectList.remove(f);
        }
        viewModel.setText(String.valueOf(selectList.size()));
    }

    @Override
    public int getItemCount() {
        return mMediaFiles.size();
    }

    public void setmMediaFiles(ArrayList<File> mMediaFiles) {
        this.mMediaFiles = mMediaFiles;
        mMediaFilesFull = new ArrayList<>(mMediaFiles);
        notifyDataSetChanged();
    }
    public void setmMediaName(ArrayList<String> imageNames) {
        this.imageNames = imageNames;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return mMediaFilesFilter;
    }

    private Filter mMediaFilesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<File> filteredList = new ArrayList<>();
            if(charSequence == null || charSequence.length() == 0 || charSequence.equals("")){
                filteredList.addAll(mMediaFilesFull);
            }else{
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for(String item : imageNames){
                    if(item.toLowerCase().contains(filterPattern)){
                        filteredList.add(mMediaFilesFull.get(imageNames.indexOf(item)));
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mMediaFiles.clear();
            mMediaFiles.addAll((ArrayList) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName;
        private ConstraintLayout gridLayout;
        private ImageView thumbnail;
        private ImageView imageChecked;
        private ImageView uploadShareMenu;
        private ProgressBar countdown;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            gridLayout = itemView.findViewById(R.id.gridLayout);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            txtName = itemView.findViewById(R.id.txtName);
            imageChecked = itemView.findViewById(R.id.imageChecked);
            uploadShareMenu = itemView.findViewById(R.id.uploadShareMenu);
            countdown = itemView.findViewById(R.id.countdown);
        }
    }
    public void setOnItemClick(RecyclerviewOnClickListener recyclerviewOnClickListener) {
        this.recyclerviewOnClickListener = recyclerviewOnClickListener;
    }
}




