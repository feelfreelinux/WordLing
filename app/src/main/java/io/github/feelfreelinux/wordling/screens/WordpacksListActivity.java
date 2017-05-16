package io.github.feelfreelinux.wordling.screens;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import io.github.feelfreelinux.wordling.R;
import io.github.feelfreelinux.wordling.WordLing;
import io.github.feelfreelinux.wordling.adapters.WordpackListAdapter;
import io.github.feelfreelinux.wordling.dialogs.EditTextDialog;
import io.github.feelfreelinux.wordling.dialogs.WordListMenuDialog;
import io.github.feelfreelinux.wordling.objects.Wordpack;
import io.github.feelfreelinux.wordling.objects.WordpackEntry;
import io.github.feelfreelinux.wordling.objects.WordpackList;
import io.github.feelfreelinux.wordling.utils.CloseAnimationListener;
import io.github.feelfreelinux.wordling.utils.EditTextDialogActivity;
import io.github.feelfreelinux.wordling.utils.SortedSessionManager;
import io.github.feelfreelinux.wordling.utils.StorageWordpackManager;
import io.github.feelfreelinux.wordling.utils.WebWordpackDownloader;
import io.github.feelfreelinux.wordling.utils.WordpackParser;

public class WordpacksListActivity extends EditTextDialogActivity {
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
                else closeFABMenu();
            }
        });

        // Import button listener
        fabImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();

                // Open Import dialog
                Bundle args = new Bundle();
                args.putString("title", getResources().getString(R.string.wordpackImportTitle));
                args.putString("buttonLabel", getResources().getString(R.string.wordpackImportButton));
                args.putString("hint", getResources().getString(R.string.urlAddress));
                EditTextDialog dialog = new EditTextDialog();
                dialog.setArguments(args);
                dialog.show(getFragmentManager(), "dialog");
            }
        });

        // Create button listener
        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                // Open wordpack editor

                Intent intent = new Intent(getApplicationContext(), WordpackEditorActivity.class);
                startActivity(intent);
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
                        locale = Locale.forLanguageTag(wordpack.getTranslationLang());
                    else locale = new Locale(wordpack.getTranslationLang());

                    // Init TTS service
                    ((WordLing) getApplication()).initTTS(locale);

                    // Create load wordpack data from memory, create session manager
                    SortedSessionManager sm = new SortedSessionManager(wordpack, entry.key);
                    // Start new session
                    Intent inputScreen = new Intent(getApplicationContext(), WordInputActivity.class);
                    inputScreen.putExtra("SortedSessionManager", sm);
                    getApplicationContext().startActivity(inputScreen);
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

    }

    @Override
    public void editTextAction(String url, Bundle args) {

        // Fill missing url data
        if(!url.startsWith("http://") && !url.startsWith("https://") ){
            url = "http://" + url;
        }
        // Create spinner
        progress = new ProgressDialog(this);
        progress.setTitle("Importing wordpack");
        progress.setCancelable(true);
        progress.show();
        Log.v("url", url);
        // Start asynctask dowloading json data
        wwd = (WebWordpackDownloader) new WebWordpackDownloader(){
            @Override
            protected void onPostExecute(String data) {
                progress.dismiss();
                if (!data.isEmpty()) {
                    // Save wordpack, add it to list
                    Wordpack wordpack = new WordpackParser().getWordpackFromText(data);
                    if (!(wordpack == null)) { strMgr.addWordpackToMemory(wordpack);
                        refreshList();
                    }
                } else {
                    Log.v("err", "or");
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
    public void closeFABMenu(){
        fabOpen = false;
        fabMenu.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward));
        // Start the animation
        closeFABAnimation(fabImport, importHint);
        closeFABAnimation(fabCreate, createHint);
    }

    public void openFABMenu(){
        fabOpen = true;
        fabMenu.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward));
        // Start the animation
        openFABAnimation(fabImport, importHint);
        openFABAnimation(fabCreate, createHint);
    }

    public void openFABAnimation(final View view, final View hint) {
        Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);

        // Listener changes visibility to GONE on finish
        view.setVisibility(View.VISIBLE);
        hint.setVisibility(View.VISIBLE);

        hint.startAnimation(slideUp);
        view.startAnimation(slideUp);
    }

    public void closeFABAnimation(final View view, final View hint) {
        Animation slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        // Listener changes visibility to GONE on finish
        CloseAnimationListener closeListener = new CloseAnimationListener();
        closeListener.setView(view, hint);
        slideDown.setAnimationListener(closeListener);
        view.startAnimation(slideDown);
        hint.startAnimation(slideDown);
    }

    @Override
    protected void onDestroy() {
        ((WordLing) getApplication()).shutdown();
        super.onDestroy();
    }
}
