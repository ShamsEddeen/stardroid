package com.google.android.stardroid.activities.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.stardroid.R;
import com.google.android.stardroid.StardroidApplication;
import com.google.android.stardroid.util.Analytics;
import com.google.android.stardroid.util.MiscUtil;

/**
 * End User License agreement dialog.
 * Created by johntaylor on 4/3/16.
 */
public class EulaDialogFragment extends DialogFragment {
  private static final String TAG = MiscUtil.getTag(EulaDialogFragment.class);
  private Activity parentActivity;
  private boolean showButtons;
  private Analytics analytics;
  private EulaAcceptanceListener resultListener;

  public interface EulaAcceptanceListener {
    void eulaAccepted();
    void eulaRejected();
  }

  public EulaDialogFragment(Activity parentActivity, boolean showButtons, Analytics analytics,
                            EulaAcceptanceListener resultListener) {
    this.parentActivity = parentActivity;
    this.showButtons = showButtons;
    this.analytics = analytics;
    this.resultListener = resultListener;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    LayoutInflater inflater = parentActivity.getLayoutInflater();
    View view = inflater.inflate(R.layout.tos_view, null);

    String apologyText = parentActivity.getString(R.string.language_apology_text);
    Spanned formattedApologyText = Html.fromHtml(apologyText);
    TextView apologyTextView = (TextView) view.findViewById(R.id.language_apology_box_text);
    apologyTextView.setText(formattedApologyText, TextView.BufferType.SPANNABLE);

    String whatsNewText = String.format(parentActivity.getString(R.string.whats_new_text), getVersionName());
    Spanned formattedWhatsNewText = Html.fromHtml(whatsNewText);
    TextView whatsNewTextView = (TextView) view.findViewById(R.id.whats_new_box_text);
    whatsNewTextView.setText(formattedWhatsNewText, TextView.BufferType.SPANNABLE);

    String eulaText = String.format(parentActivity.getString(R.string.eula_text), getVersionName());
    Spanned formattedEulaText = Html.fromHtml(eulaText);
    TextView eulaTextView = (TextView) view.findViewById(R.id.eula_box_text);
    eulaTextView.setText(formattedEulaText, TextView.BufferType.SPANNABLE);

    AlertDialog.Builder tosDialogBuilder = new AlertDialog.Builder(parentActivity)
        .setTitle(R.string.menu_tos)
        .setView(view);
    // Note that we've made the "accept" button the negative button and the "decline" button
    // the positive button as an experiment.
    if (showButtons) {
      tosDialogBuilder
          .setNegativeButton(R.string.dialog_accept,
              new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                  Log.d(TAG, "TOS Dialog closed.  User accepts.");
                  resultListener.eulaAccepted();
                  dialog.dismiss();
                  analytics.trackEvent(
                      Analytics.APP_CATEGORY, Analytics.TOS_ACCEPT, Analytics.TOS_ACCEPTED, 1);
                }
              })
          .setPositiveButton(R.string.dialog_decline,
              new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                  Log.d(TAG, "TOS Dialog closed.  User declines.");
                  dialog.dismiss();
                  analytics.trackEvent(
                      Analytics.APP_CATEGORY, Analytics.TOS_ACCEPT, Analytics.TOS_REJECTED, 0);
                  resultListener.eulaRejected();
                }
              });
    }
    return tosDialogBuilder.create();
  }

  private String getVersionName() {
    return ((StardroidApplication) parentActivity.getApplication()).getVersionName();
  }
}
