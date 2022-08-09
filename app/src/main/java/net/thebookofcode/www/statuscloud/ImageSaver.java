package net.thebookofcode.www.statuscloud;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ImageSaver {
    Context context;
    private ArrayList<String> videoPath;

    public ImageSaver(Context context) {
        this.context = context;
    }

    public Bitmap singleImage(File images) {
        Bitmap thumb = null;
        Bitmap pre_thumb;
        Size size = new Size(300, 300);
        if (images.getName().endsWith("mp4")) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    thumb = ThumbnailUtils.createVideoThumbnail(images, size, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                pre_thumb = ThumbnailUtils.createVideoThumbnail(images.getAbsolutePath(), MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
                thumb = Bitmap.createScaledBitmap(pre_thumb, 300, 300, false);
            }
        } else if (images.getName().endsWith("jpg")) {
            thumb = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(images.getAbsolutePath()), 300, 300);
        }

        return thumb;
    }

    public Bitmap load(File file) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public ArrayList<String> getActiveVideoPath() {
        File file = new File(Environment.getExternalStorageDirectory() + "/WhatsApp/Media/.Statuses/");
        File[] dirFile = file.listFiles();
        if (dirFile.length != 0) {
            videoPath = new ArrayList<>();
            for (File file1 : dirFile) {
                if (file1.getName().endsWith(".mp4")) {
                    videoPath.add(file1.getAbsolutePath());
                } else if (file1.getName().endsWith(".nomedia")) {
                    continue;
                }
            }
        }
        return videoPath;
    }

    public ArrayList<String> getSavedVideoPath() {
        File file = new File(Environment.getExternalStorageDirectory() + "/StatusCloud/Videos");
        File[] dirFile = file.listFiles();
        if (dirFile.length != 0) {
            videoPath = new ArrayList<>();
            for (File file1 : dirFile) {
                if (file1.getName().endsWith(".mp4")) {
                    videoPath.add(file1.getAbsolutePath());
                } else if (file1.getName().endsWith(".nomedia")) {
                    continue;
                }
            }
        }
        return videoPath;
    }

    public void saveImage(File file) {
        Bitmap bitmapImage = singleImage(file);
        FileOutputStream fileOutputStream = null;
        String uuid = String.valueOf((int) System.currentTimeMillis());
        try {
            fileOutputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/StatusCloud/Images", "image" + uuid + ".jpg"));
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveImageWithName(File file, String name) {
        Bitmap bitmapImage = singleImage(file);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/StatusCloud/Images", name + ".jpg"));
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveUploadImage(ContentResolver contentResolver, Uri uri) {
        Bitmap bitmapImage = null;
        try {
            bitmapImage = MediaStore.Images.Media.getBitmap(contentResolver, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fileOutputStream = null;
        String uuid = String.valueOf((int) System.currentTimeMillis());
        try {
            fileOutputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/StatusCloud/.Uploads", "image" + uuid + ".jpg"));
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveVideo(File file) {
        String uuid = String.valueOf((int) System.currentTimeMillis());
        try {
            FileInputStream in = new FileInputStream(file);
            FileOutputStream out = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/StatusCloud/Videos", "video" + uuid + ".mp4"));
            byte[] buff = new byte[1024];
            int len;
            while ((len = in.read(buff)) > 0) {
                out.write(buff, 0, len);
            }
            in.close();
            out.close();
        } catch (Exception e) {
        }
    }

    public void saveVideoWithName(File file, String name) {
        try {
            FileInputStream in = new FileInputStream(file);
            FileOutputStream out = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/StatusCloud/Videos", name + ".mp4"));
            byte[] buff = new byte[1024];
            int len;
            while ((len = in.read(buff)) > 0) {
                out.write(buff, 0, len);
            }
            in.close();
            out.close();
        } catch (Exception e) {
        }
    }

    public void saveUploadVideo(ContentResolver contentResolver, Uri uri) {
        uri.toString();
        File file = new File(uri.getPath());
        //File file1 = new File(Environment.getDataDirectory().)
        String uuid = String.valueOf((int) System.currentTimeMillis());
        try {
            InputStream in = contentResolver.openInputStream(uri);
            //FileInputStream in = new FileInputStream(file);
            FileOutputStream out = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/StatusCloud/.Uploads", "video" + uuid + ".mp4"));
            byte[] buff = new byte[1024];
            int len;
            while ((len = in.read(buff)) > 0) {
                out.write(buff, 0, len);
            }
            in.close();
            out.close();
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
            Log.e("Exception", uri.getPath());
        }
    }
}
