package io.github.feelfreelinux.wordling.screens;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.github.feelfreelinux.wordling.R;
import io.github.feelfreelinux.wordling.Values;
import io.github.feelfreelinux.wordling.WordLing;
import io.github.feelfreelinux.wordling.adapters.WordpackListAdapter;
import io.github.feelfreelinux.wordling.dialogs.EditTextDialog;
import io.github.feelfreelinux.wordling.dialogs.GithubUploadedDialog;
import io.github.feelfreelinux.wordling.dialogs.WordListMenuDialog;
import io.github.feelfreelinux.wordling.objects.Wordpack;
import io.github.feelfreelinux.wordling.objects.WordpackEntry;
import io.github.feelfreelinux.wordling.objects.WordpackList;
import io.github.feelfreelinux.wordling.utils.CloseAnimationListener;
import io.github.feelfreelinux.wordling.utils.EditTextDialogActionListener;
import io.github.feelfreelinux.wordling.utils.GithubGistUploader;
import io.github.feelfreelinux.wordling.utils.SortedSessionManager;
import io.github.feelfreelinux.wordling.utils.StorageWordpackManager;
import io.github.feelfreelinux.wordling.utils.WebWordpackDownloader;
import io.github.feelfreelinux.wordling.utils.WordlingActivity;
import io.github.feelfreelinux.wordling.utils.WordpackParser;

public class WordpacksListActivity extends WordlingActivity implements EditTextDialogActionListener {
    private ListView listView;
    private StorageWordpackManager strMgr;

    private FloatingActionButton fabMenu, fabImport, fabCreate;
    private TextView importHint, createHint;
    private WordpackListAdapter adapter;
    private WordpackList wordpacks;
    private boolean fabOpen = false;
    ProgressDialog progress;
    WebWordpackDownloader wwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordpack_list);

        PackageManager pm = getPackageManager();
        Intent installIntent = new Intent();
        installIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        ResolveInfo resolveInfo = pm.resolveActivity( installIntent, PackageManager.MATCH_DEFAULT_ONLY );

        if( resolveInfo == null ) {
            Toast.makeText(this, R.string.tts_not_available, Toast.LENGTH_SHORT).show();
        } else {
            startActivityForResult( installIntent, Values.TTSCheckData );
        }

        // Set title
        setTitle(getResources().getString(R.string.wordpacksListTitle));
        listView = (ListView) findViewById(R.id.wordpacks_list);

        // Init parts of FAB menu
        fabMenu = (FloatingActionButton) findViewById(R.id.FABMenu);
        fabImport = (FloatingActionButton) findViewById(R.id.menu_importWordpack);
        fabCreate = (FloatingActionButton) findViewById(R.id.menu_createWordpack);
        importHint = (TextView) findViewById(R.id.menu_importWordpack_hint);
        createHint = (TextView) findViewById(R.id.menu_createWordpack_hint);

        // FAB Menu close / open listener
        fabMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!fabOpen) openFABMenu();
                else closeFABMenu(null);
            }
        });

        // Import button listener
        fabImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu(new CloseAnimationListener.FabCloseListener() {
                    @Override
                    public void onClose() {
                        if (((WordLing) getApplication()).isOnline()) {
                            // Open Import dialog
                            Bundle args = new Bundle();
                            args.putString("title", getResources().getString(R.string.wordpackImportTitle));
                            args.putString("buttonLabel", getResources().getString(R.string.wordpackImportButton));
                            args.putString("hint", getResources().getString(R.string.urlAddress));
                            EditTextDialog dialog = new EditTextDialog();
                            dialog.setArguments(args);
                            dialog.show(getFragmentManager(), "dialog");
                        } else showNoInternetDialog();
                    }
                });
            }
        });

        // Create button listener
        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu(new CloseAnimationListener.FabCloseListener() {
                    @Override
                    public void onClose() {
                        // Open wordpack editor
                        Intent intent = new Intent(getApplicationContext(), WordpackEditorActivity.class);
                        startActivityForResult(intent, Values.WordpackEditor);
                    }
                });
            }
        });

        strMgr = new StorageWordpackManager(this);

        if (!strMgr.contains("wordpacks")) {
            // Create empty wordpack list
            WordpackList emptyWordpackList = new WordpackList(new ArrayList<WordpackEntry>());
            // Save it to memory
            strMgr.saveJSONtoMemory("wordpacks", emptyWordpackList.toString());
        }

        // Refresh list
        refreshList();

        // Open wordpack on short click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WordpackEntry entry = (WordpackEntry) adapter.getItem(position);
                if (!(entry.key == null)) {
                    // Get wordpack from entry
                    Wordpack wordpack = new StorageWordpackManager(getApplicationContext()).getWordpackFromStorage(entry.key);

                    // Get Locale from wordpack BCP-47 code (In api 21)
                    Locale locale;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        locale = Locale.forLanguageTag(wordpack.getWordLanguage());
                    else locale = new Locale(wordpack.getWordLanguage());

                    // Init TTS service
                    ((WordLing) getApplication()).initTTS(locale);

                    // Create load wordpack data from memory, create session manager
                    SortedSessionManager sm = new SortedSessionManager(wordpack, entry.key);
                    // Start new session
                    Intent inputScreen = new Intent(getApplicationContext(), WordInputActivity.class);
                    inputScreen.putExtra("SortedSessionManager", sm);
                    startActivity(inputScreen);
                }
            }
        });

        // Show menu on LongClick
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                WordpackEntry entry = (WordpackEntry) adapter.getItem(position);
                // Create bundle, and pass it to fragment
                Bundle args = new Bundle();
                args.putSerializable("entry", entry);
                WordListMenuDialog menu = new WordListMenuDialog();
                menu.setArguments(args);
                menu.show(getFragmentManager(), "dialog");
                return true;
            }
        });

        // Set empty view for showing if there's no information provided
        listView.setEmptyView(findViewById(R.id.emptyView));

    }

    @Override
    public void editTextAction(String url, Bundle args) {

        // Fill missing url data
        if(!url.startsWith("http://") && !url.startsWith("https://") ){
            url = "http://" + url;
        }
        // Create spinner
        progress = new ProgressDialog(this);
        progress.setTitle(getString(R.string.importing_wordpack));
        progress.setCancelable(true);
        progress.show();

        // Start asynctask dowloading json data
        wwd = (WebWordpackDownloader) new WebWordpackDownloader(){
            @Override
            protected void onPostExecute(String data) {
                progress.dismiss();
                if (data != null && !data.isEmpty()) {
                    // Save wordpack, add it to list
                    Wordpack wordpack = new WordpackParser().getWordpackFromText(data);
                    if (!(wordpack == null)) { strMgr.addWordpackToMemory(wordpack);
                        refreshList();
                    }
                } else {
                    new AlertDialog.Builder(WordpacksListActivity.this)
                            .setTitle(R.string.error)
                            .setMessage(R.string.im_error)
                            .setPositiveButton(android.R.string.ok, null).show();
                }
            }
        }.execute(url);
        // Cancel asynctask when you cancel the dialog
        progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                wwd.cancel(true);
            }
        });
    }

    public void refreshList() {
        // Retreive wordpack list from storage, list it in listView
        adapter = null;
        wordpacks = strMgr.getWordpackListFromStorage();
        adapter = new WordpackListAdapter(this, wordpacks);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void closeFABMenu(CloseAnimationListener.FabCloseListener listener) {
        fabOpen = false;
        fabMenu.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward));
        fabImport.setClickable(false);
        fabCreate.setClickable(false);
        // Start the animation
        closeFABAnimation(fabImport, importHint, listener);
        closeFABAnimation(fabCreate, createHint, null);
    }

    public void openFABMenu(){
        fabOpen = true;
        fabMenu.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward));
        // Start the animation
        openFABAnimation(fabImport, importHint);
        openFABAnimation(fabCreate, createHint);
        fabImport.setClickable(true);
        fabCreate.setClickable(true);
    }

    public void editWordpack(WordpackEntry entry) {
        Intent intent = new Intent(getApplicationContext(), WordpackEditorActivity.class);
        intent.putExtra("wordpack", entry);
        startActivityForResult(intent, Values.WordpackEditor);
    }

    public void openFABAnimation(final View view, final View hint) {
        Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);

        // Listener changes visibility to GONE on finish
        view.setVisibility(View.VISIBLE);
        hint.setVisibility(View.VISIBLE);

        hint.startAnimation(slideUp);
        view.startAnimation(slideUp);
    }

    public void closeFABAnimation(final View view, final View hint, @Nullable CloseAnimationListener.FabCloseListener listener) {
        Animation slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        // Listener changes visibility to GONE on finish
        CloseAnimationListener closeListener = new CloseAnimationListener();
        closeListener.setView(view, hint, listener);
        slideDown.setAnimationListener(closeListener);
        view.startAnimation(slideDown);
        hint.startAnimation(slideDown);
    }

    @Override
    protected void onDestroy() {
        ((WordLing) getApplication()).shutdown();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Handle word editor exit
        if (resultCode == Values.WordpackEdited) {
            refreshList();
        } else
        if (requestCode == Values.TTSCheckData) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                ((WordLing) getApplication()).initTTS(Locale.US);
            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }

    }

    public void safeDeleteWordpack(final String key) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_wordpack)
                .setMessage(R.string.ask_delete_wordpack)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Delete the wordpack, refresh ListView
                        new StorageWordpackManager(getApplicationContext()).removeWordpack(key);
                        refreshList();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }
    @SuppressLint("StaticFieldLeak")
    public void uploadWordpackToWeb(WordpackEntry wordpackEntry) {
        Wordpack wp = strMgr.getWordpackFromStorage(wordpackEntry.key);
        progress = new ProgressDialog(this);
        progress.setTitle(getString(R.string.uploading_wp));
        progress.setCancelable(true);
        progress.show();
        new GithubGistUploader() {
            @Override
            protected void onPostExecute(String data) {
                progress.dismiss();
                if (!data.isEmpty()) {
                    Bundle args = new Bundle();
                    args.putString("url", data);
                    GithubUploadedDialog dialog = new GithubUploadedDialog();
                    dialog.setArguments(args);
                    dialog.show(getFragmentManager(), "dialog");
                } else {
                    new AlertDialog.Builder(WordpacksListActivity.this)
                            .setTitle(R.string.error)
                            .setMessage(R.string.up_error)
                            .setPositiveButton(android.R.string.ok, null).show();
                }
            }
        }.execute(wp);
    }
    public void showNoInternetDialog(){
        new AlertDialog.Builder(this)
                .setTitle(R.string.internet_connection_title)
                .setMessage(R.string.internet_connection)
                .setPositiveButton(android.R.string.ok, null).show();
    }

    public boolean isActivityCallable( String packageName, String className) {
        final Intent intent = new Intent();
        intent.setClassName(packageName, className);
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}
