package com.example.documenpro;

import android.os.Environment;
import android.provider.MediaStore;

import com.example.documenpro.model_reader.CodeColorModel;
import com.example.documenpro.model_reader.LanguageModel;
import com.example.documenpro.model_reader.ToolsModel;

import java.io.File;
import java.util.ArrayList;

public class AppGlobalConstants {
    public static String PREF_GUIDE_COMPLETED = "guide_set";
    public static String PREF_NIGHT_MODE = "night_mode";
    public static String NAVIGATION_CLICK_COUNT = "navigationClickCount";
    public static int DIALOG_CONFIRM_CLEAR_RECENT = 5;
    public static int DIALOG_CONFIRM_CLEAR_FAV = 6;
    public static int DIALOG_CONFIRM_EXIT_SPLIT = 2;
    public static int DIALOG_CONFIRM_EXIT_MERGE = 3;
    public static final String EXTRA_PDF_FILE_NAME = "PDF_FILE_NAME";
    public static String MERGE_PDF_FILE = "merge_pdf_file_name";
    public static String MERGE_ORDER_TIP = "merge_order_tip";
    public static String PDF_SET_PASSWORD = "pdf_set_password";
    public static String EXTRA_PDF_MODEL = "pdf_model_send";
    public static String PHOTO_ORDER_TIP = "photo_order_tip";
    public static String FROM_SAVE_IMAGE = "from_save_image";
    public static String PHOTO_TO_PDF_FILE_NAME = "photo_2_pdf_file_name";
    public static final String STYLE_ROMAN_LOWER = "r";
    public static String DOC_APP_FATAL = "PDF READER FATAL";

    public static final String TEST_DEVICE_HASHED_ID = "903957CFACA92DD2EAD1287E887E33AA";
    public static String PRIVACY_POLICY_URL = "https://sites.google.com/view/duytanstudio-privacypolicy";
    public static final String PREF_IS_APP_RATED = "is_rate_app";
    public static final String ACTION_RATE_APP = "RATE_APP";
    public static final String PREF_ADS_COUNT = "count_ads";
    public static final String PREF_ADS_COUNT_BACK = "count_ads_back";
    public static Boolean IS_ADS_ENABLED = false;

    public static String PREF_IS_FIRST_TIME = "first_time";
    public static String PREF_LANGUAGE_NUMBER = "language_key_number";
    public static String PREF_LANGUAGE_NAME = "language_name";
    public static String PREF_LANGUAGE_KEY = "language_key";

    public static final String EXTRA_SELECTED_FILE_URI = "KEY_SELECTED_FILE_URI";
    public static final String EXTRA_SELECTED_FILE_NAME = "KEY_SELECTED_FILE_NAME";
    public static final String EXTRA_DATA_FROM_OUTSIDE = "data123";
    public static String EXTRA_FILE_TYPE = "file_type";
    public static String EXTRA_FILE_TYPE_LIST = "file_type_list";
    public final static int FILE_TYPE_ALL = 0;
    public final static int FILE_TYPE_EXCEL = 1;
    public final static int FILE_TYPE_TEXT = 66;
    public final static int FILE_TYPE_PDF = 2;
    public final static int FILE_TYPE_WORD = 3;
    public final static int FILE_TYPE_PPT = 4;
    public final static int FILE_TYPE_FAVORITE = 5;
    public final static int FILE_TYPE_RECENT = 6;

    public static int DIALOG_CONFIRM_DELETE = 0;
    public static int DIALOG_CONFIRM_REMOVE_RECENT = 1;
    public static int DIALOG_CONFIRM_EXIT_PHOTO_TO_PDF = 4;
    public static int DIALOG_CONFIRM_REMOVE_FAVORITE = 7;

    public static String DIRECTORY_COMPRESSED_PDF = Environment.getExternalStorageDirectory() + "/Documents/AllPdf/Compressed/";
    public static String DIRECTORY_MERGED_PDF = Environment.getExternalStorageDirectory() + "/Documents/AllPdf/Merged/";
    public static String DIRECTORY_SPLIT_PDF = Environment.getExternalStorageDirectory() + "/Documents/AllPdf/Split/";
    public static String DIRECTORY_IMAGE_TO_PDF = Environment.getExternalStorageDirectory() + "/Documents/AllPdf/Image2Pdf/";
    public static String DIRECTORY_IMAGES = Environment.getExternalStorageDirectory() + "/Documents/AllPdf/Images/";
    public static String DIRECTORY_LOCKED_PDF = Environment.getExternalStorageDirectory() + "/Documents/AllPdf/Lock/";

    public static File DIRECTORY_COMPRESSED_PDF_FILE = new File(Environment.getExternalStorageDirectory() + "/Documents/AllPdf/Compressed");
    public static File DIRECTORY_MERGED_PDF_FILE = new File(Environment.getExternalStorageDirectory() + "/Documents/AllPdf/Merged");
    public static File DIRECTORY_SPLIT_PDF_FILE = new File(Environment.getExternalStorageDirectory() + "/Documents/AllPdf/Split");
    public static File DIRECTORY_IMAGE_TO_PDF_FILE = new File(Environment.getExternalStorageDirectory() + "/Documents/AllPdf/Image2Pdf");
    public static File DIRECTORY_LOCKED_PDF_FILE = new File(Environment.getExternalStorageDirectory() + "/Documents/AllPdf/Lock");

    public static File DIRECTORY_SAVED_IMAGES = new File(Environment.getExternalStorageDirectory() + "/Documents/AllPdf/Images");

    public static String FEEDBACK_EMAIL = "goat.mobile.apps@gmail.com";

    public static final int REQUEST_CODE_FILE_PICKER = 1001;
    public static int REQUEST_CODE_STORAGE_PERMISSION = 2296;
    public static int REQUEST_CODE_MANAGE_ALL_FILES = 2290;

    public static final String QUERY_PDF_FILES = MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'application/pdf'";

    public static final String QUERY_ALL_DOCUMENT_FILES =
            MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'"
                    + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'application/vnd.ms-excel'"
                    + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'"
                    + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'application/msword'"
                    + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'application/vnd.openxmlformats-officedocument.presentationml.presentation'"
                    + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'application/vnd.ms-powerpoint'"
                    + " OR  " + MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'application/pdf'";

    public static final String QUERY_EXCEL_FILES =
            MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'"
                    + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'application/vnd.ms-excel'";

    public static final String QUERY_WORD_FILES =
            MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'"
                    + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'application/msword'";

    public static final String QUERY_PPT_FILES =
            MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'application/vnd.openxmlformats-officedocument.presentationml.presentation'"
                    + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'application/vnd.ms-powerpoint'";

    public static final String QUERY_TEXT_FILES =
            MediaStore.Files.FileColumns.MIME_TYPE + " LIKE 'text/plain'";

    public static String PREF_LANGUAGE_SET = "language_set";

    public static int TOOL_YOUR_PDF = 0;
    public static int TOOL_ID_SPLIT = 1;
    public static int TOOL_ID_COMPRESS = 2;
    public static int TOOL_ID_PDF_TO_PHOTO = 3;
    public static int TOOL_PDF_TO_PHOTO = 4;
    public static int TOOL_ID_PHOTO_TO_PDF = 5;
    public static int TOOL_ID_BROWSE_PDF = 6;
    public static int TOOL_ID_LOCK_PDF = 7;
    public static int TOOL_ID_PRINT = 8;
    public static int TOOL_ID_UNLOCK_PDF = 9;
    public static int TOOL_ID_SHARE_PDF_AS_PHOTO = 10;

    public static String EXTRA_TOOL_TYPE = "tool_type";

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

    public static ArrayList<ToolsModel> setToolsList() {
        ArrayList<ToolsModel> arrayList = new ArrayList<>();
        arrayList.add(new ToolsModel(R.string.tool_tittle_your_pdf, R.drawable.ic_tools_your_pdf, AppGlobalConstants.TOOL_YOUR_PDF, 12));
        arrayList.add(new ToolsModel(R.string.tool_tittle_merge, R.drawable.ic_tools_merge, AppGlobalConstants.TOOL_ID_PDF_TO_PHOTO, 12));
        arrayList.add(new ToolsModel(R.string.tool_tittle_compress, R.drawable.ic_tools_compress, AppGlobalConstants.TOOL_ID_COMPRESS, 12));
        arrayList.add(new ToolsModel(R.string.tool_tittle_split, R.drawable.ic_tools_split, AppGlobalConstants.TOOL_ID_SPLIT, 0));
//        arrayList.add(new Tools(R.string.tool_tittle_browse, R.drawable.ic_tools_browse_pdf, GlobalConstant.TOOL_BROWSE_PDF, 12));
        arrayList.add(new ToolsModel(R.string.tool_tittle_print, R.drawable.ic_tools_print, AppGlobalConstants.TOOL_ID_PRINT, 12));
        arrayList.add(new ToolsModel(R.string.tool_tittle_image_to_pdf, R.drawable.ic_tools_img_to_pdf, AppGlobalConstants.TOOL_ID_PHOTO_TO_PDF, 12));
        arrayList.add(new ToolsModel(R.string.tool_tittle_pdf_to_image, R.drawable.ic_pdf_to_image, AppGlobalConstants.TOOL_PDF_TO_PHOTO, 0));
        arrayList.add(new ToolsModel(R.string.tool_tittle_lock_pdf, R.drawable.ic_tools_lock, AppGlobalConstants.TOOL_ID_LOCK_PDF, 12));
        arrayList.add(new ToolsModel(R.string.tool_tittle_unlock_pdf, R.drawable.ic_tools_unlock, AppGlobalConstants.TOOL_ID_UNLOCK_PDF, 12));
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
}
