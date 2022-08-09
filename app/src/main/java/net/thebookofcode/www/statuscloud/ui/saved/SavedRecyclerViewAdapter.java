package net.thebookofcode.www.statuscloud.ui.saved;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import net.thebookofcode.www.statuscloud.BuildConfig;
import net.thebookofcode.www.statuscloud.Global;
import net.thebookofcode.www.statuscloud.ImageSaver;
import net.thebookofcode.www.statuscloud.R;
import net.thebookofcode.www.statuscloud.RecyclerviewOnClickListener;
import net.thebookofcode.www.statuscloud.SharedViewModel;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

public class SavedRecyclerViewAdapter extends RecyclerView.Adapter<SavedRecyclerViewAdapter.MyViewHolder> implements Filterable {

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
    String mimeType;
    boolean searching = false;

    public SavedRecyclerViewAdapter(Activity activity, Context mContext, ImageSaver imageSaver, String mimeType) {
        this.mContext = mContext;
        //this.mMediaFiles = mMediaFiles;
        this.imageSaver = imageSaver;
        this.mimeType = mimeType;
        this.activity = activity;
    }

    @NonNull
    @Override
    public SavedRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.custom_grid_layout, parent, false);
        SavedRecyclerViewAdapter.MyViewHolder vHolder = new SavedRecyclerViewAdapter.MyViewHolder(v);
        //SharedViewModelFactory sharedViewModelFactory = new SharedViewModelFactory(imageSaver);
        viewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(SharedViewModel.class);
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SavedRecyclerViewAdapter.MyViewHolder holder, int position) {
        if (StatusText) {
            holder.txtName.setVisibility(View.VISIBLE);
            holder.txtName.setText(mMediaFiles.get(position).getName());
        }
        holder.thumbnail.setImageBitmap(imageSaver.singleImage(mMediaFiles.get(position)));

        holder.gridLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
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

                                                    }
                                                    actionMode.finish();
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
                                    ArrayList<Uri> toSend = new ArrayList<>();
                                    for (File item : selectList) {
                                        //toSend.add(Uri.fromFile(item));
                                        toSend.add(FileProvider.getUriForFile(mContext.getApplicationContext(), mContext.getPackageName() + ".provider", item));
                                        //toSend.add(FileProvider.getUriForFile(mContext.getApplicationContext(), BuildConfig.APPLICATION_ID+".provider",item));
                                    }
                                    actionMode.finish();
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, toSend);
                                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    intent.setType(mimeType);
                                    mContext.startActivity(Intent.createChooser(intent, "Share via..."));
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
        if (isSelectAll) {
            holder.imageChecked.setVisibility(View.VISIBLE);
            holder.gridLayout.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.imageChecked.setVisibility(View.GONE);
            holder.gridLayout.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void ClickItem(SavedRecyclerViewAdapter.MyViewHolder holder) {
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
            if (charSequence == null || charSequence.length() == 0 || charSequence.equals("")) {
                filteredList.addAll(mMediaFilesFull);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (String item : imageNames) {
                    if (item.toLowerCase().contains(filterPattern)) {
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

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            gridLayout = itemView.findViewById(R.id.gridLayout);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            txtName = itemView.findViewById(R.id.txtName);
            imageChecked = itemView.findViewById(R.id.imageChecked);
        }
    }

    public void setOnItemClick(RecyclerviewOnClickListener recyclerviewOnClickListener) {
        this.recyclerviewOnClickListener = recyclerviewOnClickListener;
    }
}



