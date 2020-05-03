package co.tinode.tindroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SendToActivity extends AppCompatActivity implements FindFragment.ReadContactsPermissionChecker {
    private static final String TAG = "SendToActivity";

    // Limit the number of times permissions are requested per session.
    private boolean mReadContactsPermissionsAlreadyRequested = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (!Intent.ACTION_SEND.equals(action) || type == null ||
                intent.getParcelableExtra(Intent.EXTRA_STREAM) == null) {
            Log.d(TAG, "Unable to share this type of content");
            finish();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SendToActivity.this, ChatsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            });
        }

        setContentView(R.layout.activity_send_to);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.contentFragment, new FindFragment(new ContactSelectedListener()), "contacts")
                .commitAllowingStateLoss();
    }

    public boolean isReadContactsPermissionRequested() {
        return mReadContactsPermissionsAlreadyRequested;
    }

    public void setReadContactsPermissionRequested() {
        mReadContactsPermissionsAlreadyRequested = true;
    }

    private class ContactSelectedListener implements FindAdapter.ClickListener {
        @Override
        public void onClick(String topicName) {
            Intent initial = getIntent();
            String type = initial.getType();

            Intent preview = new Intent(SendToActivity.this, MessageActivity.class);
            preview.setType(type);
            preview.putExtra(AttachmentHandler.ARG_SRC_URI, initial.getParcelableExtra(Intent.EXTRA_STREAM));
            // See discussion here: https://github.com/tinode/tindroid/issues/39
            preview.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            preview.putExtra("topic", topicName);
            startActivity(preview);
            finish();
        }
    }
}