package com.cikuu.pigai.activity.uiutils;

/**
 * Created by Administrator on 2015/5/8.
 */

import android.content.Context;
        import android.util.AttributeSet;
        import android.view.ActionMode;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.widget.EditText;
import android.widget.TextView;

/**
 *  This is a thin veneer over EditText, with copy/paste/spell-check removed.
 */
public class NoPasteMenuEditText extends EditText
{
    private final Context context;
    private boolean pasted;

    /** This is a replacement method for the base TextView class' method of the same name. This
     * method is used in hidden class android.widget.Editor to determine whether the PASTE/REPLACE popup
     * appears when triggered from the text insertion handle. Returning false forces this window
     * to never appear.
     * @return false
     */
    boolean canPaste()
    {
        return pasted;
    }

    boolean canCut(){
        return pasted;
    }

    boolean   canSelectText(){
        return pasted;
    }

    public void setPasted(Boolean pasted ){
        this.pasted = pasted;
    }

    /** This is a replacement method for the base TextView class' method of the same name. This method
     * is used in hidden class android.widget.Editor to determine whether the PASTE/REPLACE popup
     * appears when triggered from the text insertion handle. Returning false forces this window
     * to never appear.
     * @return false
     */
    @Override
    public boolean isSuggestionsEnabled()
    {
        return false;
    }

    public NoPasteMenuEditText(Context context)
    {
        super(context);
        this.context = context;
        init();
    }

    public NoPasteMenuEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
        init();
    }

    public NoPasteMenuEditText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    private void init()
    {
  //   this.setCustomSelectionActionModeCallback(new ActionModeCallbackInterceptor());
  //   this.setLongClickable(false);
    }


    /**
     * Prevents the action bar (top horizontal bar with cut, copy, paste, etc.) from appearing
     * by intercepting the callback that would cause it to be created, and returning false.
     */
//    private class ActionModeCallbackInterceptor implements ActionMode.Callback
//    {
//        private final String TAG = NoPasteMenuEditText.class.getSimpleName();
//
//        public boolean onCreateActionMode(ActionMode mode, Menu menu) { return false; }
//        public boolean onPrepareActionMode(ActionMode mode, Menu menu) { return false; }
//        public boolean onActionItemClicked(ActionMode mode, MenuItem item) { return false; }
//        public void onDestroyActionMode(ActionMode mode) {}
//    }
}
