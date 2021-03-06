package nz.co.hawkefilms.womenofinfluence;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

/**
 * Description:
 * Feedback form activity, the user has the option to send feedback to the app administrator
 * via an external email client.  The form contains three fields; Name, Subject, Message
 * the name and message fields are required and have validation checks made on them when the
 * user submits.
 */

public class Feedback extends AppCompatActivity {

    private GlobalAppData appData;
    private SearchView searchView;

    //EditText fields
    private EditText editName;
    private EditText editMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        setTitle(R.string.app_name);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_home_black);

        Button submitButton;
        appData = GlobalAppData.getInstance(getString(R.string.ACCESS_TOKEN), Feedback.this, "");

        //initialize edittext fields
        editName = (EditText) findViewById(R.id.fieldName);
        editMessage = (EditText) findViewById(R.id.fieldMessage);

        submitButton = (Button) findViewById(R.id.btnSubmit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //checks whether there are error messages is set
                //runs email intent if no errors
                if(validateFields())
                    sendFeedback(view);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        InputMethodManager inm = (InputMethodManager)  this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View focusedView = this.getCurrentFocus();
        if(focusedView != null)
            inm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

        //hide the refresh feedback menu item
        item = menu.findItem(R.id.menu_refresh);
        item.setVisible(false);
        item = menu.findItem(R.id.menu_feedback);
        item.setVisible(false);

//-------------------------------------------------------------------------------------------------
        //the search functionality is out of scope for the moment,
        // it will be re-enabled in a future update
        // to re-enable uncomment the searchView widget int eh menu.xml file
        menu.findItem(R.id.search).setVisible(false);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                //Log.e("onQueryTextChange", "called");
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                //Proceed to Search Results
                Intent intent = new Intent(Feedback.this, SearchResults.class);
                intent.putExtra("searchInput", searchView.getQuery().toString());
                searchView.clearFocus();
                startActivity(intent);
                return false;
            }

        });

        //customise the search view
        int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageResource(R.drawable.ic_search_black);
        searchView.setIconifiedByDefault(false);

        MenuItem searchItem = menu.findItem(R.id.search);
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                searchView.setIconifiedByDefault(false);
                searchView.setFocusable(true);
                searchView.requestFocusFromTouch();
                return true;
            }
        });
//-------------------------------------------------------------------------------------------------
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                startActivity(new Intent(Feedback.this, Home.class));
                return true;
            case R.id.action_app_settings:
                openAppSettings();
                return true;
            case R.id.menu_video_gallery:
                startActivity(new Intent(Feedback.this, VideoGallery.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    Description:
        Gathers form data and sends it to email intent
        intent opens email client chooser
    Parameters: View the current view
    Return Type: void
    */
    public void sendFeedback(View view) {

        String subject = "Ascend - Women of Influence - Feedback from " + editName.getText()
                                                                       + editMessage.getText();
        String message = "" + editMessage.getText();
        clearFields();

        //intent creates chooser for only email apps
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + getString(R.string.email_address)));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        try {
            startActivity(Intent.createChooser(emailIntent, "Send feedback using..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(Feedback.this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    Description:
        checks whether EditText fields are empty or not
        if so display error message
    Parameters: void
    Return Type: void
    */
    public boolean validateFields() {
        if (editName.getText().toString().equals(""))
            editName.setError("Name is Required");
        else
            editName.setError(null);

        if (editMessage.getText().toString().equals(""))
            editMessage.setError("Message is Required");
        else
            editMessage.setError(null);

        return isErrorFree();
    }

    /*
    Description:
        checks if any error message visible before submitting
    Parameters: void
    Return Type: void
    */
    boolean isErrorFree() {
        return TextUtils.isEmpty(editName.getError()) &&
                (TextUtils.isEmpty(editMessage.getError()));
    }

    /*
    Description:
        Clears fields in feedback form
    Parameters: void
    Return Type: void
    */
    public void clearFields() {
        EditText name = (EditText)findViewById(R.id.fieldName);
        name.setText("");

        EditText subject = (EditText)findViewById(R.id.fieldSubject);
        subject.setText("");

        EditText message = (EditText)findViewById(R.id.fieldMessage);
        message.setText("");
    }

    /*
    Description:
        Opens the app setting so the user can turn notifications on or off
    Parameters: void
    Return Type: void
    */
    public void openAppSettings() {
        String packageName = getString(R.string.package_name);

        try {
            //Open the specific App Info page:
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);

        } catch ( ActivityNotFoundException e ) {
            new AlertDialog.Builder(Feedback.this)
                    .setTitle("Notification Settings Not Available")
                    .setMessage("Unable to open the apps settings screen, please try again later")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
}