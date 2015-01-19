package com.handmark.pulltorefresh.samples;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by toker-rg on 19.01.15.
 */

public final class PullToRefreshRecyclerViewActivity extends Activity {

    static final int MENU_MANUAL_REFRESH = 0;
    static final int MENU_DISABLE_SCROLL = 1;
    static final int MENU_SET_MODE = 2;
    static final int MENU_DEMO = 3;

    //private LinkedList<String> mListItems;
    private PullToRefreshRecyclerView mPullToRefreshRecyclerView;
    //private ArrayAdapter<String> mAdapter;
    RecyclerViewAdapter mAdapter;

    /* Called when the activity is first created.*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptr_recycler_view);

        mPullToRefreshRecyclerView = (PullToRefreshRecyclerView) findViewById(R.id.pull_refresh_recycler_view);

        // Set a listener to be invoked when the list should be refreshed.
        mPullToRefreshRecyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

                // Do work to refresh the list here.
                new GetDataTask().execute();
            }
        });

        // Add an end-of-list listener
        /*mPullRefreshListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                Toast.makeText(PullToRefreshRecyclerViewActivity.this, "End of List!", Toast.LENGTH_SHORT).show();
            }
        });*/

        RecyclerView actualListView = mPullToRefreshRecyclerView.getRefreshableView();

        // Need to use the Actual ListView when registering for Context Menu
        //registerForContextMenu(actualListView);

        /*mListItems = new LinkedList<String>();
        mListItems.addAll(Arrays.asList(mStrings));*/

        //TODO no need
        //mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mListItems);


        /* Add Sound Event Listener*/

        /*SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(this);
        soundListener.addSoundEvent(PullToRefreshBase.State.PULL_TO_REFRESH, R.raw.pull_event);
        soundListener.addSoundEvent(PullToRefreshBase.State.RESET, R.raw.reset_sound);
        soundListener.addSoundEvent(PullToRefreshBase.State.REFRESHING, R.raw.refreshing_sound);
        mPullRefreshListView.setOnPullEventListener(soundListener);*/

        // You can also just use setListAdapter(mAdapter) or
        // mPullRefreshListView.setAdapter(mAdapter)
        //TODO no need
        //actualListView.setAdapter(mAdapter);
        List<Record> records = new ArrayList<Record>();
        populateRecords(records);

        RecyclerViewAdapter mAdapter = new RecyclerViewAdapter(records);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        actualListView.setAdapter(mAdapter);
        actualListView.setLayoutManager(layoutManager);
    }

    private void populateRecords(List<Record> records){
        for (int i = 0; i<50; i++){
            Record record = new Record();
            record.setName("Item №" + i);
            record.setType(Type.values()[i%3]);
            records.add(record);
        }
    }

    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            // Simulates a background job.
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
            }
            return mStrings;
        }

        @Override
        protected void onPostExecute(String[] result) {
            //mListItems.addFirst("Added after refresh...");
            //mAdapter.notifyDataSetChanged();

            // Call onRefreshComplete when the list has been refreshed.
            mPullToRefreshRecyclerView.onRefreshComplete();

            super.onPostExecute(result);
        }
    }

    private String[] mStrings = { "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
            "Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre",
            "Allgauer Emmentaler", "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
            "Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre",
            "Allgauer Emmentaler"
    };

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private List<Record> records;

        public RecyclerViewAdapter(List<Record> records) {
            this.records = records;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_item, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            Record record = records.get(i);
            int iconResourceId = 0;
            switch (record.getType()) {
                case GREEN:
                    iconResourceId = R.drawable.green_circle;
                    break;
                case RED:
                    iconResourceId = R.drawable.red_circle;
                    break;
                case YELLOW:
                    iconResourceId = R.drawable.yellow_circle;
                    break;
            }
            viewHolder.icon.setImageResource(iconResourceId);
            viewHolder.name.setText(record.getName());
            viewHolder.deleteButtonListener.setRecord(record);
            viewHolder.copyButtonListener.setRecord(record);
        }

        @Override
        public int getItemCount() {
            return records.size();
        }

        private void copy(Record record) {
            int position = records.indexOf(record);
            Record copy = record.copy();
            records.add(position + 1, copy);
            notifyItemInserted(position + 1);
        }

        private void delete(Record record) {
            int position = records.indexOf(record);
            records.remove(position);
            notifyItemRemoved(position);
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private TextView name;
            private ImageView icon;
            private Button deleteButton;
            private Button copyButton;
            private DeleteButtonListener deleteButtonListener;
            private CopyButtonListener copyButtonListener;

            public ViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.recyclerViewItemName);
                icon = (ImageView) itemView.findViewById(R.id.recyclerViewItemIcon);
                deleteButton = (Button) itemView.findViewById(R.id.recyclerViewItemDeleteButton);
                copyButton = (Button) itemView.findViewById(R.id.recyclerViewItemCopyButton);
                deleteButtonListener = new DeleteButtonListener();
                copyButtonListener = new CopyButtonListener();
                deleteButton.setOnClickListener(deleteButtonListener);
                copyButton.setOnClickListener(copyButtonListener);
            }
        }

        private class CopyButtonListener implements View.OnClickListener {
            private Record record;

            @Override
            public void onClick(View v) {
                copy(record);
            }

            public void setRecord(Record record) {
                this.record = record;
            }
        }

        private class DeleteButtonListener implements View.OnClickListener {
            private Record record;

            @Override
            public void onClick(View v) {
                delete(record);
            }

            public void setRecord(Record record) {
                this.record = record;
            }
        }
    }

    public class Record {

        private String name;
        public Type type;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public Record copy(){
            Record copy = new Record();
            copy.setType(type);
            copy.setName(name);
            return copy;
        }
    }

    public enum Type {RED, GREEN, YELLOW}
}