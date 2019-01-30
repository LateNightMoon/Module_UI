package com.wharton.ui.weight.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.wharton.ui.R;
import com.wharton.ui.keyboard.NumberKeyboard;
import com.wharton.ui.weight.GridTextView;

public class NumberDialog extends DialogFragment {

    private static final String EXTRA_OPTIONS = "EXTRA_OPTIONS";

    private TextView tvTitle;

    private TextView tvSubTitle;

    private TextView tvPrice;

    public static NumberDialog newInstance(Options options) {
        NumberDialog mFragment = new NumberDialog();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_OPTIONS, options);
        mFragment.setArguments(args);
        return mFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_number, container, false);
        initViewDefault(view);
        initVariables();
        return view;
    }

    private void initViewDefault(View view) {
        tvTitle = view.findViewById(R.id.tv_number_title);
        tvSubTitle = view.findViewById(R.id.tv_number_sub_title);
        tvPrice = view.findViewById(R.id.tv_number_price);
        View mView = view.findViewById(R.id.tv_number_dismiss);
        GridTextView mGridView = view.findViewById(R.id.gtv_number);
        NumberKeyboard mKeyboard = view.findViewById(R.id.number_dialog_keyboard);
        mKeyboard.bindView(mGridView);
        mKeyboard.shuffleKeyboard();
        mGridView.setOnGridActionListener(new GridTextView.OnGridActionListener() {
            @Override
            public void onMaxLength(String s) {
                if (itemClickListener != null) {
                    itemClickListener.onMaxLength(s, NumberDialog.this);
                }
            }
        });
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberDialog.this.dismiss();
            }
        });
        View mHintView = view.findViewById(R.id.tv_number_hint);
        mHintView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onClickHint(NumberDialog.this);
                }
            }
        });
    }

    private void initVariables() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(EXTRA_OPTIONS)) {
            Options mOptions = bundle.getParcelable(EXTRA_OPTIONS);
            if (mOptions == null) return;
            tvTitle.setText(mOptions.title);
            tvSubTitle.setText(mOptions.subTitle);
            tvPrice.setText(mOptions.price);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window == null) return;
        WindowManager.LayoutParams params = window.getAttributes();
        params.windowAnimations = R.style.number_dialog_animation;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public static class Options implements Parcelable {

        public String title;

        public String subTitle;

        public String price;

        public Options() {

        }

        public Options(String title, String subTitle, String price) {
            this.title = title;
            this.subTitle = subTitle;
            this.price = price;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.title);
            dest.writeString(this.subTitle);
            dest.writeString(this.price);
        }

        protected Options(Parcel in) {
            this.title = in.readString();
            this.subTitle = in.readString();
            this.price = in.readString();
        }

        public final Creator<Options> CREATOR = new Creator<Options>() {
            @Override
            public Options createFromParcel(Parcel source) {
                return new Options(source);
            }

            @Override
            public Options[] newArray(int size) {
                return new Options[size];
            }
        };
    }

    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {

        void onMaxLength(String s, DialogFragment dialog);

        void onClickHint(DialogFragment dialog);

    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


}
