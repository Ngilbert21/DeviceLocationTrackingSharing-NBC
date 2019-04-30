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

public class BrandNewGroupActivity extends AppCompatActivity {

    /**
     * Client reference
     */
    private MobileServiceClient mClient;

    /**
     * Table used to access data from the mobile app backend.
     */
    private MobileServiceTable<Group> mGroupTable;

    /**
     * Adapter to sync the items list with the view
     */
    private GroupAdapter mAdapter;

    /**
     * EditText containing the "New To Do" text
     */
    private EditText mGroupEntry;

    /**
     * Progress spinner to use for table operations
     */
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_new_group);

        mProgressBar = findViewById(R.id.loadingProgressBar);

        // Initialize the progress bar
        mProgressBar.setVisibility(ProgressBar.GONE);

        try {
            // Create the client instance, using the provided mobile app URL.
            mClient = AzureServiceAdapter.getInstance().getClient().withFilter(new BrandNewGroupActivity.ProgressFilter());

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

            //Init local storage
            initLocalStore().get();

            mGroupEntry = findViewById(R.id.groupEntry);

            // Create an adapter to bind the items with the view
            mAdapter = new GroupAdapter(this, R.layout.row_list_users);

            ListView groupList = findViewById(R.id.groupList);
            groupList.setAdapter(mAdapter);

            // Load the items from the mobile app backend.
            refreshItemsFromTable();

        } catch (Exception e){
            createAndShowDialog(e);
        }
    }


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
        item.setName(mGroupEntry.getText().toString());

        // Insert the new item
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
                    createAndShowDialogFromTask(e);
                }
                return null;
            }
        };

        runAsyncTask(task);



        mGroupEntry.setText("");
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
                    createAndShowDialogFromTask(e);
                }

                return null;
            }
        };

        runAsyncTask(task);
    }

    /**
     * Refresh the list with the items in the Mobile Service Table
     */

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

    /**
     * Initialize local storage
     * @return
     * @throws MobileServiceLocalStoreException
     * @throws ExecutionException
     * @throws InterruptedException
     */
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
                    createAndShowDialogFromTask(e);
                }

                return null;
            }
        };

        return runAsyncTask(task);
    }

    /**
     * Creates a dialog and shows it
     *  @param exception
     *            The exception to show in the dialog
     *
     */
    private void createAndShowDialogFromTask(final Exception exception) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception);
            }
        });
    }


    /**
     * Creates a dialog and shows it
     *  @param exception
     *            The exception to show in the dialog
     *
     */
    private void createAndShowDialog(Exception exception) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage());
    }

    /**
     * Creates a dialog and shows it
     *  @param message
     *            The dialog message
     *
     */
    private void createAndShowDialog(final String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle("Error");
        builder.create().show();
    }

    /**
     * Run an ASync task on the corresponding executor
     * @param task
     * @return
     */
    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

        Intent intent = new Intent(BrandNewGroupActivity.this, GroupActivity.class);

        intent.putExtra("groupName", groupName);

        startActivity(intent);

    }
}
