package com.wharton.ui.keyboard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.widget.TextView;

import com.wharton.ui.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NumberKeyboard extends KeyboardView implements KeyboardView.OnKeyboardActionListener {

    /**
     * 用于区分左下角空白的按键
     */
    public static final int KEYCODE_EMPTY = -10;
    /**
     * 宽度
     */
    private int deleteKeyWidth;
    /**
     * 高度
     */
    private int deleteKeyHeight;
    /**
     * 背景色
     */
    private int deleteKeyBackgroundColor;
    /**
     * 背景色
     */
    private int emptyKeyBackgroundColor;
    /**
     * 图片
     */
    private Drawable deleteKeySrc;

    private Rect mDeleteKeyDrawRect;

    public NumberKeyboard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initKeyBoard(context, attrs, defStyleAttr);
    }

    private void initKeyBoard(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.NumberKeyboard, defStyleAttr, 0);
        deleteKeySrc = attributes.getDrawable(R.styleable.NumberKeyboard_deleteKeySrc);
        int KEYBOARD_DELETE_BACKGROUND = Color.TRANSPARENT;
        deleteKeyBackgroundColor = attributes.getColor(R.styleable.NumberKeyboard_deleteKeyBackgroundColor, KEYBOARD_DELETE_BACKGROUND);
        int KEYBOARD_EMPTY_BACKGROUND = Color.TRANSPARENT;
        emptyKeyBackgroundColor = attributes.getColor(R.styleable.NumberKeyboard_emptyKeyBackgroundColor, KEYBOARD_EMPTY_BACKGROUND);
        deleteKeyWidth = attributes.getDimensionPixelOffset(R.styleable.NumberKeyboard_deleteKeyWidth, -1);
        deleteKeyHeight = attributes.getDimensionPixelOffset(R.styleable.NumberKeyboard_deleteKeyHeight, -1);
        attributes.recycle();

        Keyboard keyboard = new Keyboard(context, R.xml.number_keyboard);
        setKeyboard(keyboard);
        setEnabled(true);
        setPreviewEnabled(false);
        setOnKeyboardActionListener(this);
    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        if (primaryCode == Keyboard.KEYCODE_DELETE) {
            if(mTextView!=null){
                String content = mTextView.getText().toString();
                if (content.length() > 0) {
                    mTextView.setText(content.substring(0, content.length() - 1));
                }
            }
        } else if (primaryCode != KEYCODE_EMPTY) {
            String text = Character.toString((char) primaryCode);
            if(mTextView!=null){
                mTextView.append(text);
            }
        }
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        List<Keyboard.Key> keyList = getKeyboard().getKeys();
        for (Keyboard.Key key : keyList) {
            if (key.codes[0] == KEYCODE_EMPTY) {
                drawKeyBackground(key, canvas, emptyKeyBackgroundColor);
            } else if (key.codes[0] == Keyboard.KEYCODE_DELETE) {
                drawKeyBackground(key, canvas, deleteKeyBackgroundColor);
                drawDeleteButton(key, canvas);
            }
        }
    }

    private TextView mTextView;

    public void bindView(TextView view){
        if(mTextView == view){
            return;
        }
        mTextView =view;
    }

    /**
     * 绘制按键的背景
     */
    private void drawKeyBackground(Keyboard.Key key, Canvas canvas, int color) {
        ColorDrawable drawable = new ColorDrawable(color);
        drawable.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
        drawable.draw(canvas);
    }

    /**
     * 绘制删除按键
     */
    private void drawDeleteButton(Keyboard.Key key, Canvas canvas) {
        if (deleteKeySrc == null) {
            return;
        }
        // 计算删除图标绘制的坐标
        if (mDeleteKeyDrawRect == null || mDeleteKeyDrawRect.isEmpty()) {
            int drawWidth, drawHeight;
            int intrinsicWidth = deleteKeySrc.getIntrinsicWidth();
            int intrinsicHeight = deleteKeySrc.getIntrinsicHeight();

            if (deleteKeyWidth > 0 && deleteKeyHeight > 0) {
                drawWidth = deleteKeyWidth;
                drawHeight = deleteKeyHeight;
            } else if (deleteKeyWidth > 0) {
                drawWidth = deleteKeyWidth;
                drawHeight = drawWidth * intrinsicHeight / intrinsicWidth;
            } else if (deleteKeyHeight > 0) {
                drawHeight = deleteKeyHeight;
                drawWidth = drawHeight * intrinsicWidth / intrinsicHeight;
            } else {
                drawWidth = intrinsicWidth;
                drawHeight = intrinsicHeight;
            }
            // 限制图标的大小，防止图标超出按键
            if (drawWidth > key.width) {
                drawWidth = key.width;
                drawHeight = drawWidth * intrinsicHeight / intrinsicWidth;
            }
            if (drawHeight > key.height) {
                drawHeight = key.height;
                drawWidth = drawHeight * intrinsicWidth / intrinsicHeight;
            }
            // 获取删除图标绘制的坐标
            int left = key.x + (key.width - drawWidth) / 2;
            int top = key.y + (key.height - drawHeight) / 2;
            mDeleteKeyDrawRect = new Rect(left, top, left + drawWidth, top + drawHeight);
        }
        // 绘制删除的图标
        if (mDeleteKeyDrawRect != null && !mDeleteKeyDrawRect.isEmpty()) {
            deleteKeySrc.setBounds(mDeleteKeyDrawRect.left, mDeleteKeyDrawRect.top,
                    mDeleteKeyDrawRect.right, mDeleteKeyDrawRect.bottom);
            deleteKeySrc.draw(canvas);
        }
    }

    /**
     * 0-9 数字的 Character 值
     */
    private final List<Character> keyCodes = Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');

    /**
     * 随机打乱数字键盘上键位的排列顺序。
     */
    public void shuffleKeyboard() {
        Keyboard keyboard = getKeyboard();
        if (keyboard != null && keyboard.getKeys() != null && keyboard.getKeys().size() > 0) {
            Collections.shuffle(keyCodes); // 随机排序数字
            // 遍历所有的按键
            List<Keyboard.Key> keys = getKeyboard().getKeys();
            int index = 0;
            for (Keyboard.Key key : keys) {
                // 如果按键是数字
                if (key.codes[0] != KEYCODE_EMPTY && key.codes[0] != Keyboard.KEYCODE_DELETE) {
                    char code = keyCodes.get(index++);
                    key.codes[0] = code;
                    key.label = Character.toString(code);
                }
            }
            setKeyboard(keyboard);
        }
    }

}
