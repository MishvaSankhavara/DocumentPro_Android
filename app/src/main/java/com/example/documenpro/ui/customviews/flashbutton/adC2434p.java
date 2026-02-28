package com.example.documenpro.ui.customviews.flashbutton;


import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class adC2434p {

    public View af4946h;
    public Paint af4939a;
    public Path af4940b;
    public int af4941c;
    public int af4942d = 0;
    public boolean af4943e = false;
    public boolean af4944f = true;
    public boolean af4945g = true;
    public final Runnable af4947i = new C2435a();

    public class C2435a implements Runnable {
        public C2435a() {
        }

        public void run() {
            adC2434p pVar = adC2434p.this;
            pVar.af4943e = false;
            View view = pVar.af4946h;
            if (view != null) {
                view.postInvalidate();
            }
        }
    }
}
