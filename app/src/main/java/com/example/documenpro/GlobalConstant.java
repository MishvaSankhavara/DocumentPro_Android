package com.example.documenpro;

import android.os.Environment;
import android.provider.MediaStore;

import com.example.documenpro.model_reader.CodeColorModel;
import com.example.documenpro.model_reader.LanguageModel;
import com.example.documenpro.model_reader.ToolsModel;

import java.io.File;
import java.util.ArrayList;

public class GlobalConstant {
    public static String GUIDE_SET = "guide_set";
    public static String NAVIGATION_CLICK_COUNT = "navigationClickCount";

    public static int DIALOG_CONFIRM_CLEAR_RECENT = 5;
    public static int DIALOG_CONFIRM_EXIT_SPLIT = 2;
    public static int DIALOG_CONFIRM_CLEAR_FAV = 6;
    public static String NIGHT_MODE_KEY = "night_mode";

    public static final String PDF_FILE_NAME = "PDF_FILE_NAME";

    public static String MERGE_PDF_FILE_NAME = "merge_pdf_file_name";

    public static int DIALOG_CONFIRM_EXIT_MERGE = 3;
    public static String MERGE_ORDER_TIP = "merge_order_tip";

    public static String PDF_SET_PASSWORD = "pdf_set_password";

    public static String PDF_MODEL_SEND = "pdf_model_send";

    public static final String STYLE_ROMAN_LOWER = "r";

    public static String PHOTO_ORDER_TIP = "photo_order_tip";

    public static String FROM_SAVE_IMAGE = "from_save_image";
    public static String PHOTO_2_PDF_FILE_NAME = "photo_2_pdf_file_name";

    public static String TAG_LOG = "PDF READER FATAL";

    public static final String TEST_DEVICE_HASHED_ID = "903957CFACA92DD2EAD1287E887E33AA";
    public static String PRIVACY_URL = "https://sites.google.com/view/duytanstudio-privacypolicy";
    public static final String IS_RATED_APP = "is_rate_app";
    public static final String RATE_APP = "RATE_APP";
    public static final String ADS_COUNT = "count_ads";
    public static final String KEY_DATA_FROM_OUTSIDE = "data123";
    public static final String ADS_COUNT_BACK = "count_ads_back";
    public static Boolean IS_ADS = false;
    public static String IS_FIRST_TIME = "first_time";
    public static final String KEY_SELECTED_FILE_URI = "KEY_SELECTED_FILE_URI";
    public static final String KEY_SELECTED_FILE_NAME = "KEY_SELECTED_FILE_NAME";
    public static String LANGUAGE_KEY_NUMBER = "language_key_number";
    public static String LANGUAGE_NAME = "language_name";
    public static String LANGUAGE_KEY = "language_key";
    public static String FILE_TYPE = "file_type";
    public static String FILE_TYPE_LIST = "file_type_list";
    public final static int ALL_FILE_TYPE = 0;
    public final static int EXCEL_FILE_TYPE = 1;
    public final static int TXT_FILE_TYPE = 66;
    public final static int PDF_FILE_TYPE = 2;
    public final static int WORD_FILE_TYPE = 3;
    public final static int PPT_FILE_TYPE = 4;
    public final static int FAV_FILE_TYPE = 5;
    public final static int RECENT_FILE_TYPE = 6;
    public static int DIALOG_CONFIRM_DELETE = 0;
    public static int DIALOG_CONFIRM_REMOVE_RECENT = 1;
    public static int DIALOG_CONFIRM_REMOVE_FAV = 7;
    public static int DIALOG_CONFIRM_EXIT_PHOTO_2_PDF = 4;

    public static String RootDirectoryCompress = Environment.getExternalStorageDirectory() + "/Documents/AllPdf/Compressed/";
    public static String RootDirectoryMerged = Environment.getExternalStorageDirectory() + "/Documents/AllPdf/Merged/";
    public static String RootDirectorySplit = Environment.getExternalStorageDirectory() + "/Documents/AllPdf/Split/";
    public static String RootDirectoryImage2Pdf = Environment.getExternalStorageDirectory() + "/Documents/AllPdf/Image2Pdf/";
    public static String RootDirectoryImage = Environment.getExternalStorageDirectory() + "/Documents/AllPdf/Images/";

    public static String RootDirectoryLock = Environment.getExternalStorageDirectory() + "/Documents/AllPdf/Lock/";


    public static File RootDirectoryCompressSaved = new File(Environment.getExternalStorageDirectory() + "/Documents/AllPdf/Compressed");
    public static File RootDirectoryMergedSaved = new File(Environment.getExternalStorageDirectory() + "/Documents/AllPdf/Merged");
    public static File RootDirectorySplitSaved = new File(Environment.getExternalStorageDirectory() + "/Documents/AllPdf/Split");
    public static File RootDirectoryImage2PdfSaved = new File(Environment.getExternalStorageDirectory() + "/Documents/AllPdf/Image2Pdf");

    public static File RootDirectoryLockSaved = new File(Environment.getExternalStorageDirectory() + "/Documents/AllPdf/Lock");

    public static File RootDirectoryImageSaved = new File(Environment.getExternalStorageDirectory() + "/Documents/AllPdf/Images");
    public static String EMAIL_FEEDBACK = "goat.mobile.apps@gmail.com";
    public static final int REQUEST_CODE_PICK_FILE = 1001;
    public static final String COUNT_PDF_FILE = MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'application/pdf'";
    public static final String COUNT_ALL_FILE = MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'"
            + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'application/vnd.ms-excel'"
            + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'"
            + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'application/msword'"
            + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'application/vnd.openxmlformats-officedocument.presentationml.presentation'"
            + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'application/vnd.ms-powerpoint'"
            + " OR  " + MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'application/pdf'";
    public static final String COUNT_EXCEL_FILE = MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'"
            + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'application/vnd.ms-excel'";
    public static final String COUNT_WORD_FILE = MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'"
            + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'application/msword'";

    public static final String COUNT_PPT_FILE = MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'application/vnd.openxmlformats-officedocument.presentationml.presentation'"
            + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'application/vnd.ms-powerpoint'";

    public static final String COUNT_TXT_FILE = MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'text/plain'";

    public static int REQUEST_STORAGE_PERMISSION = 2296;

    public static int REQUEST_MANAGE_ALL_FILES_PERMISSION = 2290;

    public static String LANGUAGE_SET = "language_set";
    public static int TOOL_YOUR_PDF = 0;
    public static int TOOL_SPLIT = 1;
    public static int TOOL_COMPRESS = 2;
    public static int TOOL_MERGE = 3;
    public static int TOOL_PDF_TO_PHOTO = 4;
    public static int TOOL_PHOTO_TO_PDF = 5;
    public static int TOOL_BROWSE_PDF = 6;
    public static int TOOL_LOCK_PDF = 7;
    public static int TOOL_PRINT = 8;
    public static int TOOL_UNLOCK_PDF = 9;
    public static int TOOL_SHARE_PDF_AS_PHOTO = 10;

    public static String TOOL_TYPE = "tool_type";

    public static ArrayList<LanguageModel> createArrayLanguage() {
        ArrayList<LanguageModel> arrayList = new ArrayList<>();
        arrayList.add(new LanguageModel("en", "English", R.drawable.flag_en));
        arrayList.add(new LanguageModel("vi", "Tiếng Việt", R.drawable.flag_vi));
        arrayList.add(new LanguageModel("de", "Deutsch", R.drawable.flag_de));
        arrayList.add(new LanguageModel("in", "Indonesia", R.drawable.flag_in));
        arrayList.add(new LanguageModel("it", "Italiano", R.drawable.flag_it));
        arrayList.add(new LanguageModel("ja", "日本語", R.drawable.flag_ja));
        arrayList.add(new LanguageModel("ko", "한국어", R.drawable.flag_ko));
        arrayList.add(new LanguageModel("pt", "Português", R.drawable.flag_pt));
        arrayList.add(new LanguageModel("ru", "Русский", R.drawable.flag_ru));
        arrayList.add(new LanguageModel("ar", "عربي", R.drawable.flag_ar));
        arrayList.add(new LanguageModel("cs", "čeština", R.drawable.flag_cs));
        arrayList.add(new LanguageModel("es", "Español", R.drawable.flag_es));
        arrayList.add(new LanguageModel("hi", "हिंदी", R.drawable.flag_hi));
        arrayList.add(new LanguageModel("pl", "Język polski", R.drawable.flag_pl));
        arrayList.add(new LanguageModel("ro", "Română", R.drawable.flag_ro));
        arrayList.add(new LanguageModel("sv", "Svenska", R.drawable.flag_sv));
        arrayList.add(new LanguageModel("th", "แบบไทย", R.drawable.flag_th));
        return arrayList;
    }

    public static ArrayList<CodeColorModel> getColorDrawList() {

        ArrayList<CodeColorModel> arrayList = new ArrayList<>();
        arrayList.add(new CodeColorModel(R.drawable.bg_color_1, "#000000", false));
        arrayList.add(new CodeColorModel(R.drawable.bg_color_2, "#ffffff", true));
        arrayList.add(new CodeColorModel(R.drawable.bg_color_3, "#888888", false));
        arrayList.add(new CodeColorModel(R.drawable.bg_color_4, "#fe0000", false));
        arrayList.add(new CodeColorModel(R.drawable.bg_color_5, "#00f402", false));
        arrayList.add(new CodeColorModel(R.drawable.bg_color_6, "#1f20fb", false));
        arrayList.add(new CodeColorModel(R.drawable.bg_color_7, "#ff00aa", false));
        arrayList.add(new CodeColorModel(R.drawable.bg_color_8, "#00effe", false));
        arrayList.add(new CodeColorModel(R.drawable.bg_color_9, "#ff5c02", false));
        arrayList.add(new CodeColorModel(R.drawable.bg_color_10, "#ffc44e", false));


        return arrayList;
    }

    public static ArrayList<CodeColorModel> getColorTextList() {

        ArrayList<CodeColorModel> arrayList = new ArrayList<>();
        arrayList.add(new CodeColorModel(R.drawable.bg_color_1, "#000000", false));
        arrayList.add(new CodeColorModel(R.drawable.bg_color_2, "#ffffff", true));
        arrayList.add(new CodeColorModel(R.drawable.bg_color_3, "#888888", false));
        arrayList.add(new CodeColorModel(R.drawable.bg_color_4, "#fe0000", false));
        arrayList.add(new CodeColorModel(R.drawable.bg_color_5, "#00f402", false));
        arrayList.add(new CodeColorModel(R.drawable.bg_color_6, "#1f20fb", false));
        arrayList.add(new CodeColorModel(R.drawable.bg_color_7, "#ff00aa", false));
        arrayList.add(new CodeColorModel(R.drawable.bg_color_8, "#00effe", false));
        arrayList.add(new CodeColorModel(R.drawable.bg_color_9, "#ff5c02", false));
        arrayList.add(new CodeColorModel(R.drawable.bg_color_10, "#ffc44e", false));


        return arrayList;
    }

    public static ArrayList<CodeColorModel> getColorBgList() {

        ArrayList<CodeColorModel> arrayList = new ArrayList<>();
        arrayList.add(new CodeColorModel(R.drawable.bg_color_tran, "transparent", false));
        arrayList.add(new CodeColorModel(R.drawable.bg_color_1, "#000000", false));
        arrayList.add(new CodeColorModel(R.drawable.bg_color_2, "#ffffff", true));
        arrayList.add(new CodeColorModel(R.drawable.bg_color_3, "#888888", false));
        arrayList.add(new CodeColorModel(R.drawable.bg_color_4, "#fe0000", false));
        arrayList.add(new CodeColorModel(R.drawable.bg_color_5, "#00f402", false));
        arrayList.add(new CodeColorModel(R.drawable.bg_color_6, "#1f20fb", false));
        arrayList.add(new CodeColorModel(R.drawable.bg_color_7, "#ff00aa", false));
        arrayList.add(new CodeColorModel(R.drawable.bg_color_8, "#00effe", false));
        arrayList.add(new CodeColorModel(R.drawable.bg_color_9, "#ff5c02", false));
        arrayList.add(new CodeColorModel(R.drawable.bg_color_10, "#ffc44e", false));


        return arrayList;
    }

    public static ArrayList<ToolsModel> setToolsList() {
        ArrayList<ToolsModel> arrayList = new ArrayList<>();

        arrayList.add(new ToolsModel(R.string.tool_tittle_your_pdf, R.drawable.ic_tools_your_pdf, GlobalConstant.TOOL_YOUR_PDF, 12));
        arrayList.add(new ToolsModel(R.string.tool_tittle_merge, R.drawable.ic_tools_merge, GlobalConstant.TOOL_MERGE, 12));
        arrayList.add(new ToolsModel(R.string.tool_tittle_compress, R.drawable.ic_tools_compress, GlobalConstant.TOOL_COMPRESS, 12));
        arrayList.add(new ToolsModel(R.string.tool_tittle_split, R.drawable.ic_tools_split, GlobalConstant.TOOL_SPLIT, 0));
//        arrayList.add(new Tools(R.string.tool_tittle_browse, R.drawable.ic_tools_browse_pdf, GlobalConstant.TOOL_BROWSE_PDF, 12));
        arrayList.add(new ToolsModel(R.string.tool_tittle_print, R.drawable.ic_tools_print, GlobalConstant.TOOL_PRINT, 12));
        arrayList.add(new ToolsModel(R.string.tool_tittle_image_to_pdf, R.drawable.ic_tools_img_to_pdf, GlobalConstant.TOOL_PHOTO_TO_PDF, 12));
        arrayList.add(new ToolsModel(R.string.tool_tittle_pdf_to_image, R.drawable.ic_pdf_to_image, GlobalConstant.TOOL_PDF_TO_PHOTO, 0));
        arrayList.add(new ToolsModel(R.string.tool_tittle_lock_pdf, R.drawable.ic_tools_lock, GlobalConstant.TOOL_LOCK_PDF, 12));
        arrayList.add(new ToolsModel(R.string.tool_tittle_unlock_pdf, R.drawable.ic_tools_unlock, GlobalConstant.TOOL_UNLOCK_PDF, 12));
        return arrayList;
    }
}
