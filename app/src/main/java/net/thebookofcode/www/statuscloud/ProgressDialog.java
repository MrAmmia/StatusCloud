package net.thebookofcode.www.statuscloud;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressDialog {
    private Activity activity;
    private AlertDialog dialog;
    private TextView textView;
    ProgressBar progressBar;
    public ProgressDialog(Activity activity) {
        this.activity = activity;
    }

    public void startAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_progress_layout, null);
        builder.setView(view);
        textView = view.findViewById(R.id.textView);
        progressBar = view.findViewById(R.id.progressBar);
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    public void setText(int progress){
        textView.setText(progress + "% done");
        progressBar.setProgress(progress);
    }

    public void dismissDialog() {
        dialog.dismiss();
    }
}