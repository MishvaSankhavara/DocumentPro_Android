package com.example.documenpro.utils;

import static android.os.Build.VERSION.SDK_INT;
import static com.example.documenpro.GlobalConstant.REQUEST_MANAGE_ALL_FILES_PERMISSION;
import static com.example.documenpro.GlobalConstant.REQUEST_STORAGE_PERMISSION;

import android.Manifest;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Icon;
import android.graphics.pdf.PdfRenderer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowMetrics;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdSize;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.example.documenpro.GlobalConstant;
import com.example.documenpro.R;
import com.example.documenpro.SharedPreferenceUtils;
import com.example.documenpro.adapter_reader.FileListAdapter;
import com.example.documenpro.advertisement.OnAdDismissedListener;
import com.example.documenpro.advertisement.AdManager;
import com.example.documenpro.database.DatabaseHelper;
import com.example.documenpro.clickListener.MoreClickListener;
import com.example.documenpro.clickListener.OnConfirmClickListener;
import com.example.documenpro.clickListener.SortingListener;
import com.example.documenpro.model_reader.DocumentModel;
import com.example.documenpro.model_reader.PDFReaderModel;
import com.example.documenpro.ui.activities.DocumentListActivity;
import com.example.documenpro.ui.activities.MainActivity;
import com.example.documenpro.ui.activities.SplashScreenActivity;
import com.example.documenpro.ui.activities.ViewOfficeActivity;
import com.example.documenpro.ui.dialog.ActionConfirmDialog;
import com.example.documenpro.ui.dialog.AppLoadingDialog;
import com.example.documenpro.ui.dialog.MoreDialog;
import com.example.documenpro.ui.dialog.RequestPermissionDialog;
import com.example.documenpro.ui.dialog.AppRateDialog;
import com.example.documenpro.ui.dialog.SortByDialog;
import com.shockwave.pdfium.PdfPasswordException;
import com.shockwave.pdfium.PdfiumCore;
import com.tom_roush.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Utils {
    public static String getFileNameFromUri(Uri uri) {

        String fileName = null;

        if (uri != null) {
            String path = uri.getPath();
            if (path != null) {
                File file = new File(path);
                fileName = file.getName();
            }
        }

        return fileName;

    }

    public static void copyFile(Context mContext, Uri source, String destination) {
        try (InputStream in = mContext.getContentResolver().openInputStream(source);
             OutputStream out = new FileOutputStream(destination)) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
    }

    public static boolean isTablet(Context context) {
        return context.getResources().getBoolean(R.bool.isTablet);
    }

    public static void deletePdfFiles(String str) {
        File file = new File(str);
        if (file.exists() && file.isDirectory()) {
            StringBuilder sb = new StringBuilder();
            sb.append("find ");
            sb.append(str);
            sb.append(" -xdev -mindepth 1 -delete");
            try {
                Runtime.getRuntime().exec(sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static PDFReaderModel removePassWordPDF(PDFReaderModel pdfModel, String password) {
        try {
            String path = GlobalConstant.RootDirectoryLock;
            File file2 = new File(path);
            if (!file2.exists()) {
                file2.mkdirs();
            }
            String filePathNeW = path + "Unlock_" + pdfModel.getName_PDFModel();

            PDDocument document = PDDocument.load(new File(pdfModel.getAbsolutePath_PDFModel()), password);


//            AccessPermission ap = document.getCurrentAccessPermission();
//            if (ap.isOwnerPermission()) {
            document.setAllSecurityToBeRemoved(true);
            document.save(filePathNeW);

            final File file = new File(filePathNeW);
            PDFReaderModel fileHolderModel = new PDFReaderModel();
            fileHolderModel.setName_PDFModel(file.getName());
            fileHolderModel.setAbsolutePath_PDFModel(file.getAbsolutePath());
            fileHolderModel.setFileUri_PDFModel(file.getAbsolutePath());
            fileHolderModel.setProtected_PDFModel(false);
            fileHolderModel.setLength_PDFModel(file.length());
            fileHolderModel.setLastModified_PDFModel(file.lastModified());
            fileHolderModel.setDirectory_PDFModel(file.isDirectory());
            return fileHolderModel;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean setPassPDF(String srcFile, String fileNameNew, String password) {

        byte[] USER = password.getBytes();
        byte[] OWNER = "Hi".getBytes();
        String path = GlobalConstant.RootDirectoryLock;
        File file2 = new File(path);
        if (!file2.exists()) {
            file2.mkdirs();
        }
        String filePathNeW = path + "Locked" + fileNameNew;

        try {
            PdfReader reader = new PdfReader(srcFile);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(filePathNeW));
            stamper.setEncryption(USER, OWNER, PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128 | PdfWriter.DO_NOT_ENCRYPT_METADATA);
            stamper.close();
            reader.close();
            return true;
        } catch (IOException | DocumentException e) {
            return false;
        }
    }

    public static String removeFileExtension(String str) {
        String fileSeparator = System.getProperty("file.separator");
        if (fileSeparator == null) {
            fileSeparator = "/";
            // You can replace "/" with "\\" if you're specifically targeting Windows environments.
        }
        int lastIndexOf = str.lastIndexOf(fileSeparator);
        if (lastIndexOf != -1) {
            str = str.substring(lastIndexOf + 1);
        }
        int lastIndexOf2 = str.lastIndexOf(".");
        if (lastIndexOf2 == -1) {
            return str;
        }
        return str.substring(0, lastIndexOf2);
    }

    public static int getPageCountPDF(File pdfFile) {
        int pageCount = 0;
        try {
            // Mở tệp PDF
            PDDocument document = PDDocument.load(pdfFile);
            // Lấy số trang
            pageCount = document.getNumberOfPages();
            // Đóng tệp PDF
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pageCount;
    }

    public static void displayPDFThumbnail(Context context, File pdfFile, ImageView imageView) {
        ParcelFileDescriptor fileDescriptor = null;
        PdfRenderer renderer = null;
        PdfRenderer.Page page = null;
        try {
            // Check if the file exists and is readable
            if (!pdfFile.exists() || !pdfFile.canRead()) {
                throw new IOException("File does not exist or cannot be read.");
            }

            // Open the PDF file in read-only mode
            fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);

            // Create a PdfRenderer to render the PDF
            renderer = new PdfRenderer(fileDescriptor);

            // Check if the PDF has at least one page
            if (renderer.getPageCount() <= 0) {
                throw new IllegalArgumentException("PDF has no pages.");
            }

            // Open the first page of the PDF
            page = renderer.openPage(0);

            // Create a Bitmap with the size of the first page
            Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);

            // Render the page to the Bitmap
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            // Display the rendered Bitmap on the ImageView
            imageView.setImageBitmap(bitmap);

        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            // Close the page if it was opened
            if (page != null) {
                page.close();
            }
            // Close the renderer if it was created
            if (renderer != null) {
                renderer.close();
            }
            // Close the file descriptor if it was opened
            if (fileDescriptor != null) {
                try {
                    fileDescriptor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static ArrayList<PDFReaderModel> getUnLockPDF(Context mContext) {
        ArrayList<PDFReaderModel> pdfList = new ArrayList<>();
        Uri collection;
        final String[] projection = new String[]{
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.MIME_TYPE,
        };
        final String sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";

//        final String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("doc");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Files.getContentUri("external");
        }
        try (Cursor cursor = mContext.getContentResolver().query(collection, projection, GlobalConstant.COUNT_PDF_FILE, null, sortOrder)) {
            assert cursor != null;

            if (cursor.moveToFirst()) {
                int columnData = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                int columnName = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
                do {
                    String file_path = cursor.getString(columnData);
                    String file_name = cursor.getString(columnName);
                    //you can get your pdf files
                    final File file = new File(file_path);
                    if (file_name == null) {
                        break;
                    }
                    if (!Utils.checkHavePassword(mContext, Uri.fromFile(new File(file_path)))) {
                        if (file.length() != 0) {
                            PDFReaderModel fileHolderModel = new PDFReaderModel();
                            fileHolderModel.setName_PDFModel(file.getName());
                            fileHolderModel.setAbsolutePath_PDFModel(file.getAbsolutePath());
                            fileHolderModel.setFileUri_PDFModel(file.getAbsolutePath());
                            fileHolderModel.setProtected_PDFModel(false);
//                        fileHolderModel.setUrlThumbnail(Utils.generateThumbnailPdf(mContext, file));
                            fileHolderModel.setLength_PDFModel(file.length());
                            fileHolderModel.setLastModified_PDFModel(file.lastModified());
                            fileHolderModel.setDirectory_PDFModel(file.isDirectory());
                            pdfList.add(fileHolderModel);
                        }
                    }
                } while (cursor.moveToNext());
            }
        }
        return pdfList;
    }

    public static ArrayList<PDFReaderModel> getLockPDF(Context mContext) {
        ArrayList<PDFReaderModel> pdfList = new ArrayList<>();
        Uri collection;
        final String[] projection = new String[]{
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.MIME_TYPE,
        };
        final String sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";

//        final String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("doc");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Files.getContentUri("external");
        }
        try (Cursor cursor = mContext.getContentResolver().query(collection, projection, GlobalConstant.COUNT_PDF_FILE, null, sortOrder)) {
            assert cursor != null;

            if (cursor.moveToFirst()) {
                int columnData = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                int columnName = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
                do {
                    String file_path = cursor.getString(columnData);
                    String file_name = cursor.getString(columnName);
                    //you can get your pdf files
                    final File file = new File(file_path);
                    if (file_name == null) {
                        break;
                    }
                    if (Utils.checkHavePassword(mContext, Uri.fromFile(new File(file_path)))) {
                        if (file.length() != 0) {
                            PDFReaderModel fileHolderModel = new PDFReaderModel();
                            fileHolderModel.setName_PDFModel(file.getName());
                            fileHolderModel.setAbsolutePath_PDFModel(file.getAbsolutePath());
                            fileHolderModel.setFileUri_PDFModel(file.getAbsolutePath());
                            fileHolderModel.setProtected_PDFModel(true);
//                        fileHolderModel.setUrlThumbnail(Utils.generateThumbnailPdf(mContext, file));
                            fileHolderModel.setLength_PDFModel(file.length());
                            fileHolderModel.setLastModified_PDFModel(file.lastModified());
                            fileHolderModel.setDirectory_PDFModel(file.isDirectory());
                            pdfList.add(fileHolderModel);
                        }
                    }
                } while (cursor.moveToNext());
            }
        }
        return pdfList;
    }

    public static boolean checkHavePassword(Context context, Uri uri) {
        try {
            new PdfiumCore(context).newDocument(context.getContentResolver().openFileDescriptor(uri, GlobalConstant.STYLE_ROMAN_LOWER));
            return false;
        } catch (PdfPasswordException e) {
            return true;
        } catch (IOException e2) {
//            Toast.makeText(context, R.string.toast_cannot_print_malformed_pdf, Toast.LENGTH_LONG).show();
            e2.printStackTrace();
            return true;
        } catch (UnsatisfiedLinkError error) {
            error.printStackTrace();
            return false;
        }
    }

    public static CharSequence setToolBarPdfCreated(Context mContext) {
        SpannableString first = new SpannableString("PDF");
        SpannableString second = new SpannableString(mContext.getString(R.string.str_pdf_created_toolbar));
        SpannableString empty = new SpannableString(" ");
        first.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.colorAccent)), 0, first.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        second.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.colorTextPrimary)), 0, second.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return TextUtils.concat(first, empty, second);

    }

    public static void shareImage(Context context, String filePath) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.utils_share_txt));
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), filePath, "", null);
            Uri screenshotUri = Uri.parse(path);
            intent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
            intent.setType("image/*");
            context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.utils_share_image_via)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void showConfirmDialog(Activity mContext, int typeConfirm, OnConfirmClickListener listener) {
        if (mContext == null) {
            return;
        }
        ActionConfirmDialog dialog = new ActionConfirmDialog(mContext, typeConfirm, listener);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        dialog.show();

    }

    public static void backWithAds(Activity mContext, int number) {
        int numberAds = SharedPreferenceUtils.getInstance(mContext).getInt(GlobalConstant.ADS_COUNT_BACK, 1);
        if (countAds(mContext, GlobalConstant.ADS_COUNT_BACK, number)) {
            AdManager.showAds_AdManager(mContext, new OnAdDismissedListener() {
                @Override
                public void OnAdDismissedListener() {
                    SharedPreferenceUtils.getInstance(mContext).setInt(GlobalConstant.ADS_COUNT_BACK, numberAds + 1);
                    mContext.finish();
                }
            });
            SharedPreferenceUtils.getInstance(mContext).setInt(GlobalConstant.ADS_COUNT_BACK, numberAds + 1);

        } else {
            SharedPreferenceUtils.getInstance(mContext).setInt(GlobalConstant.ADS_COUNT_BACK, numberAds + 1);
            mContext.finish();
        }
    }

    public static boolean countAds(Context mContext, String adsType, int numberAds) {
        return SharedPreferenceUtils.getInstance(mContext).getInt(adsType, 2) % numberAds == 0;
    }

    public static String getFileNameFromUri(Uri contentUri, ContentResolver contentResolver) {
        try {
            String filePath;
            String[] filePathColumn = {MediaStore.MediaColumns.DISPLAY_NAME};
            Cursor cursor = contentResolver.query(contentUri, filePathColumn, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                filePath = cursor.getString(columnIndex);
                cursor.close();
                return filePath;
            }
//            cursor.moveToFirst();
        } catch (SecurityException | CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    public static AdSize getAdSize(Activity context, View adContainer) {
        WindowMetrics windowMetrics = null;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            windowMetrics = context.getWindowManager().getCurrentWindowMetrics();
        }
        Rect bounds = null;


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            bounds = windowMetrics.getBounds();
        }
        float adWidthPixels = adContainer.getWidth();
        if (adWidthPixels == 0f && bounds != null) {
            adWidthPixels = bounds.width();
        } else if (adWidthPixels == 0f) {
            adWidthPixels = context.getResources().getDisplayMetrics().widthPixels;
        }
        float density = context.getResources().getDisplayMetrics().density;
        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }

    public static void copy(Context mContext, Uri source, String destination) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = mContext.getContentResolver().openInputStream(source);
            out = new FileOutputStream(destination);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void searchDocument(String string, ArrayList<DocumentModel> itemList, FileListAdapter adapter) {
        ArrayList<DocumentModel> arrayList = new ArrayList<>();
        for (DocumentModel documentModel : itemList) {
            if (documentModel.getFileName_DocModel().toLowerCase().contains(string.toLowerCase())) {
                arrayList.add(documentModel);
            }
            adapter.filter(arrayList);
        }
    }

    public static ArrayList<PDFReaderModel> getCreatedPdf(File directory) {
        ArrayList<PDFReaderModel> pdfList = new ArrayList<>();

        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                PDFReaderModel fileHolderModel = new PDFReaderModel();
                fileHolderModel.setName_PDFModel(file.getName());
                fileHolderModel.setAbsolutePath_PDFModel(file.getAbsolutePath());
                fileHolderModel.setFileUri_PDFModel(file.getAbsolutePath());
                fileHolderModel.setLength_PDFModel(file.length());
                fileHolderModel.setLastModified_PDFModel(file.lastModified());
                fileHolderModel.setDirectory_PDFModel(file.isDirectory());
                pdfList.add(fileHolderModel);
            }
        }


        return pdfList;


    }

    public static void openFileWithAds(Activity mContext, DocumentModel document, int number) {
        int numberAds = SharedPreferenceUtils.getInstance(mContext).getInt(GlobalConstant.ADS_COUNT, 1);
        if (countAds(mContext, number)) {
            AdManager.showAds_AdManager(mContext, new OnAdDismissedListener() {
                @Override
                public void OnAdDismissedListener() {
                    openFile(mContext, document);
                }

            });
            SharedPreferenceUtils.getInstance(mContext).setInt(GlobalConstant.ADS_COUNT, numberAds + 1);
        } else {
            SharedPreferenceUtils.getInstance(mContext).setInt(GlobalConstant.ADS_COUNT, numberAds + 1);
            openFile(mContext, document);
        }

    }

    public static boolean countAds(Context mContext, int numberAds) {
        return SharedPreferenceUtils.getInstance(mContext).getInt(GlobalConstant.ADS_COUNT, 2) % numberAds == 0;
    }

    public static void openFile(Activity mContext, DocumentModel document) {
        try {
            File file = new File(document.getFileUri_DocModel());

//            File file = new File(fileHolderModel.getAbsolutePath());
            DatabaseHelper.getInstance(mContext).addRecentDocument_DatabaseHelper(file.getAbsolutePath());
            Uri fromFile = Uri.fromFile(file);
            Intent intent = new Intent(mContext, ViewOfficeActivity.class);
            intent.setAction("android.intent.action.VIEW");
            intent.setData(fromFile);
            intent.putExtra(GlobalConstant.KEY_SELECTED_FILE_URI, file.getAbsolutePath());
            intent.putExtra(GlobalConstant.KEY_SELECTED_FILE_NAME, file.getName());
            intent.putExtra("STARTED_FROM_EXPLORER", true);
            intent.putExtra("START_PAGE", 0);
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openFile(Activity mContext, File file) {
        try {
//            File file = new File(fileHolderModel.getAbsolutePath());
            DatabaseHelper.getInstance(mContext).addRecentDocument_DatabaseHelper(file.getAbsolutePath());
            Uri fromFile = Uri.fromFile(file);
            Intent intent = new Intent(mContext, ViewOfficeActivity.class);
            intent.setAction("android.intent.action.VIEW");
            intent.setData(fromFile);
            intent.putExtra(GlobalConstant.KEY_SELECTED_FILE_URI, file.getAbsolutePath());
            intent.putExtra(GlobalConstant.KEY_SELECTED_FILE_NAME, file.getName());
            intent.putExtra("STARTED_FROM_EXPLORER", true);
            intent.putExtra("START_PAGE", 0);
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CharSequence setAppName(Context mContext) {
        SpannableString first = new SpannableString("Document");
        SpannableString second = new SpannableString(" Read");
        first.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.colorPrimaryText)), 0, first.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        second.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.ppt_file_list_bg)), 0, second.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return TextUtils.concat(first, second);
    }

    public static ObjectAnimator startAnim(View view) {

        int dimensionPixelOffset = view.getResources().getDimensionPixelOffset(R.dimen.dp_5);
        float f = (float) (-dimensionPixelOffset);

        float f2 = (float) dimensionPixelOffset;
        return ObjectAnimator.ofPropertyValuesHolder(view, PropertyValuesHolder.ofKeyframe(View.TRANSLATION_X, Keyframe.ofFloat(0.0f, 0.0f), Keyframe.ofFloat(0.1f, f), Keyframe.ofFloat(0.26f, f2), Keyframe.ofFloat(0.42f, f), Keyframe.ofFloat(0.58f, f2), Keyframe.ofFloat(0.74f, f), Keyframe.ofFloat(0.9f, f2), Keyframe.ofFloat(1.0f, 0.0f))).setDuration(500);
    }

    public static String removeExtension(String str) {
        int lastIndexOf = str.lastIndexOf(Objects.requireNonNull(System.getProperty("file.separator")));
        if (lastIndexOf != -1) {
            str = str.substring(lastIndexOf + 1);
        }
        int lastIndexOf2 = str.lastIndexOf(".");
        if (lastIndexOf2 == -1) {
            return str;
        }
        return str.substring(0, lastIndexOf2);
    }

    public static boolean isFileNameValid(String str) {
        String trim = str.trim();
        return !TextUtils.isEmpty(trim) && trim.matches("[a-zA-Z0-9-_ ]*");
    }

    public static void rateApp(Context mContext) {
        try {
            Uri marketUri = Uri.parse("market://details?id=" + mContext.getPackageName());
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, marketUri));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static ArrayList<DocumentModel> countFile(Context mContext, String whereClause) {
        ArrayList<DocumentModel> arrayList = new ArrayList<>();
        Uri collection;
        final String[] projection = new String[]{
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.MIME_TYPE,
        };
        final String sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";

//        final String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("doc");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Files.getContentUri("external");
        }
        try (Cursor cursor = mContext.getContentResolver().query(collection, projection, whereClause, null, sortOrder)) {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnData = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                    int columnName = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
                    do {
                        String file_path = cursor.getString(columnData);
                        String file_name = cursor.getString(columnName);

                        //you can get your pdf files
                        final File file = new File(file_path);
                        if (file_name == null) {
                            break;
                        }
                        if (file.length() != 0) {
                            DocumentModel document = new DocumentModel();
                            document.setFileName_DocModel(file.getName());
                            document.setFileUri_DocModel(file.getAbsolutePath());
                            document.setLength_DocModel(file.length());
                            document.setSrcImage_DocModel(getDocumentSrc(file));
                            document.setLastModified_DocModel(file.lastModified());

                            arrayList.add(document);
                        }
                    } while (cursor.moveToNext());
                }
            }

        }
        return arrayList;
    }

    public static String formatDateToHumanReadable(Long l) {
        return new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date(l));
    }

    public static void chooseFileManager(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] mimetypes = {
                "application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.ms-powerpoint",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "text/plain"
        };
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivityForResult(intent, GlobalConstant.REQUEST_CODE_PICK_FILE);
    }

    public static boolean checkPermission(Context mContext) {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            return !(ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED);
        }
    }

    public static void feedbackApp(Activity context) {
        try {
            Intent intent3 = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + GlobalConstant.EMAIL_FEEDBACK));
            intent3.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.str_feed_back_app));
            intent3.putExtra(Intent.EXTRA_TEXT, "");
            context.startActivity(intent3);
        } catch (ActivityNotFoundException ignored) {
        }
    }

    public static void shareApp(Activity context) {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            Resources res = context.getResources();

            i.putExtra(Intent.EXTRA_SUBJECT, res.getString(R.string.share_app_tittle));
            String appUrl = "https://play.google.com/store/apps/details?id=" + context.getPackageName();
            String shareMessage = res.getString(R.string.share_message) + "\n" + appUrl;
            i.putExtra(Intent.EXTRA_TEXT, shareMessage);
            context.startActivity(Intent.createChooser(i, res.getString(R.string.share_action)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void askPermission(Activity mContext) {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", mContext.getApplicationContext().getPackageName())));
                mContext.startActivityForResult(intent, REQUEST_MANAGE_ALL_FILES_PERMISSION);

            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                mContext.startActivityForResult(intent, REQUEST_MANAGE_ALL_FILES_PERMISSION);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Yêu cầu quyền truy cập lưu trữ
                ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
            }
            //below android 11
//            Dexter.withContext(mContext).withPermissions(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
//                @Override
//                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
//                }
//
//                @Override
//                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
//
//                }
//            }).onSameThread().check();
        }
    }

    public static ArrayList<DocumentModel> getAllDocument() {
        ArrayList<DocumentModel> documents = new ArrayList<>();
        File directory = new File(Environment.getExternalStorageDirectory().toString());
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && isDocumentFile(file)) {
                        DocumentModel document = new DocumentModel();
                        document.setFileName_DocModel(file.getName());
                        document.setFileUri_DocModel(file.getAbsolutePath());
                        document.setLength_DocModel(file.length());
                        document.setSrcImage_DocModel(getDocumentSrc(file));
                        document.setLastModified_DocModel(file.lastModified());
                        documents.add(document);
                    }
                }
            }
        }
        return documents;
    }

    public static ArrayList<DocumentModel> getPdfDocument() {
        ArrayList<DocumentModel> documents = new ArrayList<>();
        File directory = new File(Environment.getExternalStorageDirectory().toString());
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && isPdfFile(file)) {
                        DocumentModel document = new DocumentModel();
                        document.setFileName_DocModel(file.getName());
                        document.setFileUri_DocModel(file.getAbsolutePath());
                        document.setLength_DocModel(file.length());
                        document.setSrcImage_DocModel(getDocumentSrc(file));
                        document.setLastModified_DocModel(file.lastModified());
                        documents.add(document);
                    }
                }
            }
        }
        return documents;
    }

    public static ArrayList<DocumentModel> getWordDocument() {
        ArrayList<DocumentModel> documents = new ArrayList<>();
        File directory = new File(Environment.getExternalStorageDirectory().toString());
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && isWordFile(file)) {
                        DocumentModel document = new DocumentModel();
                        document.setFileName_DocModel(file.getName());
                        document.setFileUri_DocModel(file.getAbsolutePath());
                        document.setLength_DocModel(file.length());
                        document.setSrcImage_DocModel(getDocumentSrc(file));
                        document.setLastModified_DocModel(file.lastModified());
                        documents.add(document);
                    }
                }
            }
        }
        return documents;
    }

    public static ArrayList<DocumentModel> getPPTDocument() {
        ArrayList<DocumentModel> documents = new ArrayList<>();
        File directory = new File(Environment.getExternalStorageDirectory().toString());
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && isPPTFile(file)) {
                        DocumentModel document = new DocumentModel();
                        document.setFileName_DocModel(file.getName());
                        document.setFileUri_DocModel(file.getAbsolutePath());
                        document.setLength_DocModel(file.length());
                        document.setSrcImage_DocModel(getDocumentSrc(file));
                        document.setLastModified_DocModel(file.lastModified());
                        documents.add(document);
                    }
                }
            }
        }
        return documents;
    }

    public static ArrayList<DocumentModel> getExcelDocument() {
        ArrayList<DocumentModel> documents = new ArrayList<>();
        File directory = new File(Environment.getExternalStorageDirectory().toString());
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && isExcelFile(file)) {
                        DocumentModel document = new DocumentModel();
                        document.setFileName_DocModel(file.getName());
                        document.setFileUri_DocModel(file.getAbsolutePath());
                        document.setLength_DocModel(file.length());
                        document.setSrcImage_DocModel(getDocumentSrc(file));
                        document.setLastModified_DocModel(file.lastModified());
                        documents.add(document);
                    }
                }
            }
        }
        return documents;
    }

    public static boolean isDocumentFile(File file) {
        String fileName = file.getName();
        return fileName.endsWith(".pdf") ||
                fileName.endsWith(".doc") ||
                fileName.endsWith(".docx") ||
                fileName.endsWith(".xls") ||
                fileName.endsWith(".xlsx") ||
                fileName.endsWith(".ppt") ||
                fileName.endsWith(".pptx");
    }

    public static boolean isExcelFile(File file) {
        String fileName = file.getName();
        return fileName.endsWith(".xls") || fileName.endsWith(".xlsx");
    }

    public static boolean isWordFile(File file) {
        String fileName = file.getName();
        return fileName.endsWith(".doc") || fileName.endsWith(".docx");
    }

    public static boolean isPPTFile(File file) {
        String fileName = file.getName();
        return fileName.endsWith(".ppt") || fileName.endsWith(".pptx");
    }

    public static boolean isPdfFile(File file) {
        String fileName = file.getName();
        return fileName.endsWith(".pdf");
    }

    public static int getDocumentSrc(File file) {
        String fileName = file.getName();

        if (fileName.endsWith(".pdf")) {
            return R.drawable.ic_type_pdf;
        } else if (fileName.endsWith(".dox") || fileName.endsWith(".doc")) {
            return R.drawable.ic_type_word;
        } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
            return R.drawable.ic_type_excel;
        } else if (fileName.endsWith(".ppt") || fileName.endsWith(".pptx")) {
            return R.drawable.ic_type_ppt;
        } else if (fileName.endsWith(".txt")) {
            return R.drawable.ic_type_txt;
        }
        return R.drawable.ic_type_word;
    }


    public static void showRateDialog(Activity mContext) {
        if (mContext == null) {
            return;
        }
        AppRateDialog rateDialog = new AppRateDialog(mContext);
        Window window = rateDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        rateDialog.show();
    }

    public static void showPermissionDialog(Activity mContext) {
        if (mContext == null) {
            return;
        }
        RequestPermissionDialog permissionDialog = new RequestPermissionDialog(mContext);
        Window window = permissionDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        permissionDialog.show();
    }


    public static void showMoreDialog(Activity mContext, DocumentModel mDocument, boolean isRecent, MoreClickListener moreListener) {
        if (mContext == null) {
            return;
        }
        MoreDialog moreDialog = new MoreDialog(mContext, mDocument, isRecent, moreListener);
        Window window = moreDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        moreDialog.show();
    }

    public static void showSortDialog(Activity mContext, SortingListener listener) {
        if (mContext == null) {
            return;
        }
        SortByDialog moreDialog = new SortByDialog(mContext, listener);
        Window window = moreDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        moreDialog.show();
    }

    private void requestNotificationPermission(Context mContext) {

    }

    public static void showLoadingDialog(Context mContext) {
        if (mContext == null) {
            return;
        }
        AppLoadingDialog loadingDialog = new AppLoadingDialog(mContext);
        Window window = loadingDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        loadingDialog.show();
    }

    public static void openListFileActivity(MainActivity mainActivity, int i) {
        Intent intent = new Intent(mainActivity, DocumentListActivity.class);
        intent.putExtra(GlobalConstant.FILE_TYPE, i);
        mainActivity.startActivity(intent);

    }

    //    public static void openListFileActivity(MainActivity mainActivity, int i) {
//        Intent intent = new Intent(mainActivity, ListFileActivity.class);
//        intent.putExtra(GlobalConstant.FILE_TYPE, i);
//        mainActivity.startActivity(intent);
//
//    }
    public static void setTheme(Context mContext, boolean isDark) {
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        SharedPreferenceUtils.getInstance(mContext).setBoolean(GlobalConstant.NIGHT_MODE_KEY, isDark);
    }

    public static void createShortcut(Context mContext, DocumentModel document) {
        Intent launchIntentForPackage = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
        if (launchIntentForPackage == null) {
            // Xử lý trường hợp không tìm thấy intent khởi chạy
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launchIntentForPackage);
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, document.getFileName_DocModel());
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(mContext, document.getSrcImage_DocModel()));
            intent.putExtra("duplicate", true);
            mContext.sendBroadcast(intent);
        } else {
            ShortcutManager shortcutManager = mContext.getSystemService(ShortcutManager.class);
            if (shortcutManager == null || !shortcutManager.isRequestPinShortcutSupported()) {
                // Xử lý trường hợp không hỗ trợ tạo shortcut
                return;
            }

            Intent shortcutIntent = new Intent(mContext, SplashScreenActivity.class);
            shortcutIntent.setAction(Intent.ACTION_VIEW);
            shortcutIntent.setData(Uri.parse(document.getFileUri_DocModel()));

            File file = new File(document.getFileUri_DocModel());
            ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(mContext, document.getFileName_DocModel())
                    .setShortLabel(file.getName())
                    .setLongLabel(file.getPath())
                    .setIcon(Icon.createWithResource(mContext, document.getSrcImage_DocModel()))
                    .setIntent(shortcutIntent)
                    .build();

            shortcutManager.requestPinShortcut(shortcutInfo, null);
        }
    }


    public static void shareFile(Context mContext, File docFile) {
        try {
            // Quét tệp để có URI
            MediaScannerConnection.scanFile(mContext, new String[]{docFile.getPath()}, null,
                    (path, uri) -> {
                        // Tạo Intent chia sẻ
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("application/*");
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, mContext.getString(R.string.app_name));
                        shareIntent.putExtra(Intent.EXTRA_TITLE, mContext.getString(R.string.app_name));
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        // Thêm cờ để tạo một task mới và xóa các task cũ khi chia sẻ hoàn tất
                        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        // Khởi chạy Intent để chia sẻ
                        mContext.startActivity(Intent.createChooser(shareIntent, mContext.getString(R.string.share_file_using)));
                    });

        } catch (Exception e) {
            e.printStackTrace();
            // Xử lý ngoại lệ hoặc hiển thị thông báo lỗi cho người dùng
            Toast.makeText(mContext, mContext.getString(R.string.toast_error_share_file), Toast.LENGTH_SHORT).show();
        }
    }

    public static void showHideView(Context context, View layout, boolean isShow, int dimenId) {
        ValueAnimator valueAnimator;
        ValueAnimator.AnimatorUpdateListener animatorUpdateListener;
        if (layout != null) {
            if (layout.getVisibility() == View.GONE && isShow) {
                int dimensionPixelSize = context.getResources().getDimensionPixelSize(dimenId);
                ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
                layoutParams.height = 0;
                layout.setLayoutParams(layoutParams);
                layout.setVisibility(View.VISIBLE);
                valueAnimator = ValueAnimator.ofInt(0, dimensionPixelSize);
                valueAnimator.setDuration(350);
                animatorUpdateListener = new showView(layout);
            } else if (layout.getVisibility() == View.VISIBLE) {
                valueAnimator = ValueAnimator.ofInt(layout.getHeight(), 0);
                valueAnimator.setDuration(350);
                animatorUpdateListener = new hideView(layout);
            } else {
                return;
            }
            valueAnimator.addUpdateListener(animatorUpdateListener);
            valueAnimator.start();
        }
    }

    public static class showView implements ValueAnimator.AnimatorUpdateListener {

        public final View linearLayout;

        public showView(View linearLayout) {
            this.linearLayout = linearLayout;
        }

        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
            int intValue = (Integer) valueAnimator.getAnimatedValue();
            View linearLayout = this.linearLayout;
            ViewGroup.LayoutParams layoutParams = linearLayout.getLayoutParams();
            layoutParams.height = intValue;
            linearLayout.setLayoutParams(layoutParams);
        }
    }

    public static class hideView implements ValueAnimator.AnimatorUpdateListener {

        public final View f1287a;

        public hideView(View linearLayout) {
            this.f1287a = linearLayout;
        }

        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
            int intValue = (Integer) valueAnimator.getAnimatedValue();
            View linearLayout = this.f1287a;
            ViewGroup.LayoutParams layoutParams = linearLayout.getLayoutParams();
            layoutParams.height = intValue;
            linearLayout.setLayoutParams(layoutParams);
            if (intValue == 0) {
                linearLayout.setVisibility(View.GONE);
            }
        }
    }
}

