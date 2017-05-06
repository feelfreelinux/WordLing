package io.github.feelfreelinux.wordling.screens;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import io.github.feelfreelinux.wordling.R;
import io.github.feelfreelinux.wordling.dialogs.ImportWordpackDialog;
import io.github.feelfreelinux.wordling.dialogs.WordListMenuDialog;
import io.github.feelfreelinux.wordling.objects.Wordpack;
import io.github.feelfreelinux.wordling.objects.WordpackEntry;
import io.github.feelfreelinux.wordling.objects.WordpackList;
import io.github.feelfreelinux.wordling.objects.WordpackListAdapter;
import io.github.feelfreelinux.wordling.utils.SortedSessionManager;
import io.github.feelfreelinux.wordling.utils.StorageWordpackManager;
import io.github.feelfreelinux.wordling.utils.WebWordpackDownloader;
import io.github.feelfreelinux.wordling.utils.WordlingActivity;
import io.github.feelfreelinux.wordling.utils.WordpackParser;

public class WordpacksListActivity extends WordlingActivity {
    private ListView listView;
    private StorageWordpackManager strMgr;

    private FloatingActionButton fab;
    private WordpackListAdapter adapter;
    private WordpackList wordpacks;
    ProgressDialog progress;
    WebWordpackDownloader wwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordpack_list);
        // Set title
        setTitle(getResources().getString(R.string.wordpacksListTitle));
        listView = (ListView) findViewById(R.id.wordpacks_list);
        fab = (FloatingActionButton) findViewById(R.id.wordpackAddButton);
        // Construct add button click listener
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImportWordpackDialog().show(getFragmentManager(), "dialog");
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
                // Create load wordpack data from memory, create session manager
                SortedSessionManager sm = new SortedSessionManager(
                        new StorageWordpackManager(getApplicationContext())
                                .getWordpackFromStorage(entry.key), entry.key);
                // Start new session
                Intent inputScreen = new Intent(getApplicationContext(), WordInputActivity.class);
                inputScreen.putExtra("SortedSessionManager", sm);
                getApplicationContext().startActivity(inputScreen);
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

    public void importWordpackFromUrl(String url) {
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
        adapter = new WordpackListAdapter(this, getFragmentManager(), wordpacks);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
