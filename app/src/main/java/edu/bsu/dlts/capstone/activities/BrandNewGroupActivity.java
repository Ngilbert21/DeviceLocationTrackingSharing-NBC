package edu.bsu.dlts.capstone.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncContext;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.ColumnDataType;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.MobileServiceLocalStoreException;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.SQLiteLocalStore;
import com.microsoft.windowsazure.mobileservices.table.sync.synchandler.SimpleSyncHandler;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import edu.bsu.dlts.capstone.adapters.AzureServiceAdapter;
import edu.bsu.dlts.capstone.models.Group;
import edu.bsu.dlts.capstone.adapters.GroupAdapter;
import edu.bsu.dlts.capstone.R;
import edu.bsu.dlts.capstone.models.UserGroup;

// TODO: This class is overengineered. Consider remaking with necessary components
// TODO: Rename with more relevant name
public class BrandNewGroupActivity extends AppCompatActivity {

    /**
     * Client reference
     */
    private MobileServiceClient mClient;

    /**
     * Table used to access data from the mobile app backend.
     */
    private MobileServiceTable<Group> mGroupTable;

    // TODO: Delete if not needed
    //Offline Sync
    //private MobileServiceSyncTable<ToDoItem> mToDoTable;

    /**
     * Adapter to sync the items list with the view
     */
    private GroupAdapter mAdapter;

    /**
     * EditText containing the "New To Do" text
     */
    // TODO: Rename
    private EditText mTextNewToDo;

    /**
     * Progress spinner to use for table operations
     */
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: Rename
        setContentView(R.layout.activity_brand_new_group);

        mProgressBar = findViewById(R.id.loadingProgressBar);

        // Initialize the progress bar
        mProgressBar.setVisibility(ProgressBar.GONE);

        try {
            // Create the client instance, using the provided mobile app URL.
            // TODO: Possibly move getInstance to new line?
            mClient = AzureServiceAdapter.getInstance().getClient().withFilter(new BrandNewGroupActivity.ProgressFilter());

            // TODO: This chunk is questionable, but copy if useful
            // Extend timeout from default of 10s to 20s
            mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(20, TimeUnit.SECONDS);
                    client.setWriteTimeout(20, TimeUnit.SECONDS);
                    return client;
                }
            });

            // Get the remote table instance to use.
            mGroupTable = mClient.getTable(Group.class);

            // TODO: Remove when not needed
            // Offline sync table instance.
            //mToDoTable = mClient.getSyncTable("ToDoItem", ToDoItem.class);

            //Init local storage
            initLocalStore().get();

            // TODO: Rename
            mTextNewToDo = findViewById(R.id.textNewToDo);

            // Create an adapter to bind the items with the view
            mAdapter = new GroupAdapter(this, R.layout.row_list_users);
            // TODO: Why are we still using todo?
            ListView listViewToDo = (ListView) findViewById(R.id.listViewToDo);
            listViewToDo.setAdapter(mAdapter);

            // TODO: Why are we refreshing when we just created the activity?
            // Load the items from the mobile app backend.
            refreshItemsFromTable();

        } catch (Exception e){
            createAndShowDialog(e, "Error");
        }
    }

    /**
     * Mark an item as completed
     *
     * @param item
     *            The item to mark
     */
    // TODO: What even is this?
//    public void checkItem(final Group item) {
//        if (mClient == null) {
//            return;
//        }
//
//        // Set the item as completed and update it in the table
//        //item.setComplete(true);
//
//        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
//            @Override
//            protected Void doInBackground(Void... params) {
//                try {
//
//                    checkItemInTable(item);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            //if (item.isComplete()) {
//                            mAdapter.remove(item);
//                            //}
//                        }
//                    });
//                } catch (final Exception e) {
//                    createAndShowDialogFromTask(e, "Error");
//                }
//
//                return null;
//            }
//        };
//
//        runAsyncTask(task);
//
//    }

//    /**
//     * Mark an item as completed in the Mobile Service Table
//     *
//     * @param item
//     *            The item to mark
//     */
//    public void checkItemInTable(Group item) throws ExecutionException, InterruptedException {
//        mGroupTable.update(item).get();
//    }

    /**
     * Add a new item
     *
     * @param view
     *            The view that originated the call
     */
    public void addItem(View view) {
        if (mClient == null) {
            return;
        }

        // Create a new item
        final Group item = new Group();
        item.setName(mTextNewToDo.getText().toString());

        item.setName(mTextNewToDo.getText().toString());
        //item.setComplete(false);

        // Insert the new item
        // TODO: Move to separate class
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final Group entity = addItemInTable(item);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //if(!entity.isComplete()){
                            SharedPreferences pref = getSharedPreferences("user", 0);
                            String id = pref.getString("id", "");
                            UserGroup userGroup = new UserGroup();
                            userGroup.setUserID(id);
                            userGroup.setGroupID(entity.getId());
                            userGroup.setActive(true);
                            AzureServiceAdapter.getInstance().getClient().getTable(UserGroup.class).insert(userGroup);
                            mAdapter.add(entity);
                            //}
                        }
                    });
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };

        runAsyncTask(task);



        mTextNewToDo.setText("");
    }

    /**
     * Add an item to the Mobile Service Table
     *
     * @param item
     *            The item to Add
     */
    public Group addItemInTable(Group item) throws ExecutionException, InterruptedException {
        return mGroupTable.insert(item).get();
    }

    /**
     * Refresh the list with the items in the Table
     */
    private void refreshItemsFromTable() {

        // Get the items that weren't marked as completed and add them in the
        // adapter

        //TODO: Move to new class
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    final List<Group> results = refreshItemsFromMobileServiceTable();

                    //Offline Sync
                    //final List<ToDoItem> results = refreshItemsFromMobileServiceTableSyncTable();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.clear();

                            for (Group item : results) {
                                mAdapter.add(item);
                            }
                        }
                    });
                } catch (final Exception e){
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        runAsyncTask(task);
    }

    /**
     * Refresh the list with the items in the Mobile Service Table
     */

    // TODO: Does this actually work? I might need to use this
    private List<Group> refreshItemsFromMobileServiceTable() throws ExecutionException, InterruptedException {
        List<Group> groups = new ArrayList<>();
        SharedPreferences pref = getSharedPreferences("user", 0);
        String id = pref.getString("id", "");
        List<UserGroup> usersGroups = AzureServiceAdapter.getInstance().getClient().getTable(UserGroup.class).where().field("UserID").eq(id).execute().get();
        for (UserGroup group: usersGroups) {
            groups.add(AzureServiceAdapter.getInstance().getClient().getTable(Group.class).lookUp(group.getGroupID()).get());
        }
        return groups;
    }

    //Offline Sync
    /**
     * Refresh the list with the items in the Mobile Service Sync Table
     */
    /*private List<ToDoItem> refreshItemsFromMobileServiceTableSyncTable() throws ExecutionException, InterruptedException {
        //sync the data
        sync().get();
        Query query = QueryOperations.field("complete").
                eq(val(false));
        return mToDoTable.read(query).get();
    }*/

    /**
     * Initialize local storage
     * @return
     * @throws MobileServiceLocalStoreException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    // TODO: This is crazy
    private AsyncTask<Void, Void, Void> initLocalStore() throws MobileServiceLocalStoreException, ExecutionException, InterruptedException {

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    MobileServiceSyncContext syncContext = mClient.getSyncContext();

                    if (syncContext.isInitialized())
                        return null;

                    SQLiteLocalStore localStore = new SQLiteLocalStore(mClient.getContext(), "OfflineStore", null, 1);

                    Map<String, ColumnDataType> tableDefinition = new HashMap<String, ColumnDataType>();
                    tableDefinition.put("id", ColumnDataType.String);
                    tableDefinition.put("PERSONID", ColumnDataType.String);
                    tableDefinition.put("FIRSTNAME", ColumnDataType.String);
                    tableDefinition.put("LASTNAME", ColumnDataType.String);
                    tableDefinition.put("EMAILADDRESS", ColumnDataType.String);
                    tableDefinition.put("CreatedDateTime", ColumnDataType.String);
                    tableDefinition.put("DeletedDateTime", ColumnDataType.String);


                    localStore.defineTable("Person", tableDefinition);

                    SimpleSyncHandler handler = new SimpleSyncHandler();

                    syncContext.initialize(localStore, handler).get();

                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        return runAsyncTask(task);
    }

    //Offline Sync
    /**
     * Sync the current context and the Mobile Service Sync Table
     * @return
     */
    /*
    private AsyncTask<Void, Void, Void> sync() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    MobileServiceSyncContext syncContext = mClient.getSyncContext();
                    syncContext.push().get();
                    mToDoTable.pull(null).get();
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };
        return runAsyncTask(task);
    }
    */

    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialogFromTask(final Exception exception, String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, "Error");
            }
        });
    }


    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    /**
     * Creates a dialog and shows it
     *
     * @param message
     *            The dialog message
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    /**
     * Run an ASync task on the corresponding executor
     * @param task
     * @return
     */
    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    private class ProgressFilter implements ServiceFilter {

        @Override
        public ListenableFuture<ServiceFilterResponse> handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilterCallback) {

            final SettableFuture<ServiceFilterResponse> resultFuture = SettableFuture.create();


            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.VISIBLE);
                }
            });

            ListenableFuture<ServiceFilterResponse> future = nextServiceFilterCallback.onNext(request);

            Futures.addCallback(future, new FutureCallback<ServiceFilterResponse>() {
                @Override
                public void onFailure(Throwable e) {
                    resultFuture.setException(e);
                }

                @Override
                public void onSuccess(ServiceFilterResponse response) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.GONE);
                        }
                    });

                    resultFuture.set(response);
                }
            });

            return resultFuture;
        }
    }

    public void changePage( String groupName ){
        String currentgroup = groupName;

        Intent intent = new Intent(BrandNewGroupActivity.this, GroupActivity.class);

        intent.putExtra("groupName", currentgroup);

        startActivity(intent);

    }
}
