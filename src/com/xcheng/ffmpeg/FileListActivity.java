package com.xcheng.ffmpeg;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore.Video.VideoColumns;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class FileListActivity extends Activity implements OnItemClickListener{

    private static final Uri VIDEO_URI = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    private static final Uri AUDIO_URI = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private static final Uri IMAGES_URI = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    private static final String[] PROJECTION = new String[]{
        BaseColumns._ID,
        MediaColumns.DISPLAY_NAME,
        MediaColumns.DATA,
    };
    public static final int INDEX_ID = 0;
    public static final int INDEX_DISPLAY_NAME = 1;
    public static final int INDEX_DATA = 2;

    private static final String IMAGES_ORDER_COLUMN =
        ImageColumns.DATE_TAKEN + " DESC, " +
        BaseColumns._ID + " DESC ";
    private static final String VIDEO_ORDER_COLUMN =
        VideoColumns.DATE_TAKEN + " DESC, " +
        BaseColumns._ID + " DESC ";
    private static final String AUDIO_ORDER_COLUMN = BaseColumns._ID + " DESC ";
    private String from;
    private ListView mListView;
    private FileListAdapter mAdapter;
    private Activity mActivity = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filelist);

        mActivity = this;
        Intent intent = getIntent();
        from = intent.getStringExtra("from_activity");
        mListView = (ListView) findViewById(android.R.id.list);
        mAdapter = new FileListAdapter(this, R.layout.filelist_item, null, new String[]{}, new int[]{});
        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(mAdapter);
        mListView.setOnItemClickListener(this);
        refreshFileList(from);
    }

    private void refreshFileList(String from) {
        if (from != null) {
            switch(from) {
                case "video":
                    mAdapter.getQueryHandler().removeCallbacks(null);
                    mAdapter.getQueryHandler().startQuery(0, null,
                        VIDEO_URI,
                        PROJECTION,
                        null,
                        null,
                        VIDEO_ORDER_COLUMN);
                    break;
                case "audio":
                    mAdapter.getQueryHandler().removeCallbacks(null);
                    mAdapter.getQueryHandler().startQuery(0, null,
                        AUDIO_URI,
                        PROJECTION,
                        null,
                        null,
                        AUDIO_ORDER_COLUMN);
                    break;
                case "images":
                    mAdapter.getQueryHandler().removeCallbacks(null);
                    mAdapter.getQueryHandler().startQuery(0, null,
                        IMAGES_URI,
                        PROJECTION,
                        null,
                        null,
                        IMAGES_ORDER_COLUMN);
                    break;
            }
        }
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        final Object obj = view.getTag();
        if (obj == null || !(obj instanceof ViewHolder)) {
            return;
        }
        ViewHolder holder = (ViewHolder) obj;
        String file_name = holder.mTitle;
        String path = holder.mData;
        Intent intent = new Intent();
        intent.putExtra("result_name", file_name);
        intent.putExtra("result_path", path);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshFileList(from);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public class FileListAdapter extends SimpleCursorAdapter implements OnScrollListener {
        private static final String TAG = "FileListAdapter";
        private final QueryHandler mQueryHandler;

        public FileListAdapter(final Context context, final int layout, final Cursor c,
                final String[] from, final int[] to) {
            super(context, layout, c, from, to);
            mQueryHandler = new QueryHandler(getContentResolver());
        }

        QueryHandler getQueryHandler() {
            return mQueryHandler;
        }

        @Override
        public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
            final View view = super.newView(context, cursor, parent);
            final ViewHolder holder = new ViewHolder();
            holder.mTitleView = (TextView) view.findViewById(R.id.item_title);
            view.setTag(holder);
            return view;
        }

        @Override
        public void bindView(final View view, final Context context, final Cursor cursor) {
            final ViewHolder holder = (ViewHolder) view.getTag();
            holder.mId = cursor.getLong(INDEX_ID);
            holder.mTitle = cursor.getString(INDEX_DISPLAY_NAME);
            holder.mData = cursor.getString(INDEX_DATA);
            holder.mTitleView.setText(holder.mTitle);
        }

        @Override
        public void changeCursor(final Cursor c) {
            super.changeCursor(c);
        }

        @Override
        protected void onContentChanged() {
            super.onContentChanged();
            mQueryHandler.onQueryComplete(0, null, getCursor());
        }

        class QueryHandler extends AsyncQueryHandler {

            QueryHandler(final ContentResolver cr) {
                super(cr);
            }

            @Override
            protected void onQueryComplete(final int token, final Object cookie,
                    final Cursor cursor) {

                if (cursor == null || cursor.getCount() == 0) {
                    if (cursor != null) {
                        changeCursor(cursor);
                    }
                } else {
                    mListView.setVisibility(View.VISIBLE);;
                    changeCursor(cursor);
                }
            }
        }

        @Override
        public void onScroll(final AbsListView view, final int firstVisibleItem,
                final int visibleItemCount, final int totalItemCount) {
        }

        private boolean mFling = false;
        @Override
        public void onScrollStateChanged(final AbsListView view, final int scrollState) {
            switch (scrollState) {
            case OnScrollListener.SCROLL_STATE_IDLE:
                mFling = false;
                notifyDataSetChanged();
                break;
            case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                mFling = false;
                break;
            case OnScrollListener.SCROLL_STATE_FLING:
                mFling = true;
                break;
            default:
                break;
            }
        }
    }

    public class ViewHolder {
        long mId;
        String mTitle;
        TextView mTitleView;
        String mData;
    }
}
