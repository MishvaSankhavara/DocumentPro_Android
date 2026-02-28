package com.example.documenpro.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.language.MultiLanguages;
import com.example.documenpro.GlobalConstant;
import com.example.documenpro.R;
import com.example.documenpro.SharedPreferenceUtils;
import com.example.documenpro.adapter_reader.LanguagePickerDialogAdapter;
import com.example.documenpro.model_reader.LanguageModel;
import com.example.documenpro.ui.activities.SplashScreenActivity;

import java.util.ArrayList;
import java.util.Locale;

public class LanguageDialog extends Dialog {
    RecyclerView recyclerView;
    LanguagePickerDialogAdapter adapter;
    Context mContext;
    int langChoice;

    public LanguageDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_language);
        this.mContext = context;
        langChoice = SharedPreferenceUtils.getInstance(mContext).getInt(GlobalConstant.LANGUAGE_KEY_NUMBER, 0);
        recyclerView = findViewById(R.id.rcv_list);
        final ArrayList<LanguageModel> arrayList = GlobalConstant.createArrayLanguage();
        adapter = new LanguagePickerDialogAdapter(getContext(), lang -> langChoice = lang);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        findViewById(R.id.tv_ok).setOnClickListener(view1 -> {
            SharedPreferenceUtils.getInstance(mContext).setBoolean(GlobalConstant.LANGUAGE_SET, true);
            SharedPreferenceUtils.getInstance(mContext).setString(GlobalConstant.LANGUAGE_NAME, GlobalConstant.createArrayLanguage().get(langChoice).getNameLanguage_LanModel());
            SharedPreferenceUtils.getInstance(mContext).setString(GlobalConstant.LANGUAGE_KEY, GlobalConstant.createArrayLanguage().get(langChoice).getKeyLanguage_LanModel());
            SharedPreferenceUtils.getInstance(mContext).setInt(GlobalConstant.LANGUAGE_KEY_NUMBER, langChoice);
            Intent refresh = new Intent(mContext, SplashScreenActivity.class);
            refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            MultiLanguages.setAppLanguage(context, new Locale(GlobalConstant.createArrayLanguage().get(langChoice).getKeyLanguage_LanModel()));

            mContext.startActivity(refresh);
            dismiss();
        });
    }

//    public LanguageDialog(Activity mContext) {
//        this.mContext = mContext;
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.dialog_language, container, false);
//
//
//        langChoice = SharedPreferenceUtils.getInstance(mContext).getInt(GlobalConstant.LANGUAGE_KEY_NUMBER, 0);
//        recyclerView = view.findViewById(R.id.rcv_list);
//        final ArrayList<Language> arrayList = GlobalConstant.createArrayLanguage();
//        adapter = new LanguageDialogAdapter(getContext(), lang -> langChoice = lang);
//        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
//        recyclerView.setAdapter(adapter);
//        view.findViewById(R.id.tv_ok).setOnClickListener(view1 -> {
//            SharedPreferenceUtils.getInstance(mContext).setString(GlobalConstant.LANGUAGE_NAME, arrayList.get(langChoice).getNameLanguage());
//            SharedPreferenceUtils.getInstance(mContext).setString(GlobalConstant.LANGUAGE_KEY, arrayList.get(langChoice).getKeyLanguage());
//            MultiLanguages.setAppLanguage(mContext, new Locale(GlobalConstant.createArrayLanguage().get(langChoice).getKeyLanguage()));
//
//            SharedPreferenceUtils.getInstance(mContext).setInt(GlobalConstant.LANGUAGE_KEY_NUMBER, langChoice);
//            dismiss();
//
//            mContext.startActivity(new Intent(mContext, SplashActivity.class));
//            mContext.finish();
////                Utils.loadLanguage(mContext, arrayList.get(langChoice).getKeyLanguage(), true);
//        });
//        return view;
//    }
}
