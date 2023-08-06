package juloo.keyboard2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;

/** Allows to enter custom keys to be added to the keyboard. This shows up at
    the top of the "Add keys to the keyboard" option. */
public class CustomExtraKeysPreference extends ListGroupPreference
{
  /** This pref stores a list of strings encoded as JSON. */
  static final String KEY = "custom_extra_keys";

  public CustomExtraKeysPreference(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    setKey(KEY);
  }

  public static List<KeyValue> get(SharedPreferences prefs)
  {
    List<KeyValue> kvs = new ArrayList<KeyValue>();
    List<String> key_names = load_from_preferences(KEY, prefs, null);
    if (key_names != null)
    {
      for (String key_name : key_names)
        kvs.add(KeyValue.makeStringKey(key_name));
    }
    return kvs;
  }

  @Override
  void select(final SelectionCallback callback)
  {
    new AlertDialog.Builder(getContext())
      .setView(R.layout.custom_extra_key_add_dialog)
      .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
        public void onClick(DialogInterface dialog, int which)
        {
          EditText input = (EditText)((AlertDialog)dialog).findViewById(R.id.key_name);
          final String k = input.getText().toString();
          if (!k.equals(""))
            callback.select(k);
        }
      })
    .setNegativeButton(android.R.string.cancel, null)
      .setIcon(android.R.drawable.ic_dialog_alert)
      .show();
  }
}
