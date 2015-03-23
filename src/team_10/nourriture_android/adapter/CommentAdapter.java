package team_10.nourriture_android.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import team_10.nourriture_android.R;
import team_10.nourriture_android.activity.DishDetailActivity;
import team_10.nourriture_android.activity.NourritureRestClient;
import team_10.nourriture_android.bean.CommentBean;
import team_10.nourriture_android.bean.DishBean;
import team_10.nourriture_android.jsonTobean.JsonTobean;

/**
 * Created by ping on 2014/12/27.
 */
public class CommentAdapter extends BaseAdapter {

    public List<CommentBean> mCommentList = new ArrayList<CommentBean>();
    private LayoutInflater mInflater;
    private Context mContext;
    private boolean isUpdate = false;
    private boolean isUserComment = false;
    private DishBean dishBean;

    public CommentAdapter(Context context, List<CommentBean> commentList) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mCommentList = commentList;
    }

    public CommentAdapter(Context context, boolean update) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        isUpdate = update;
    }

    public CommentAdapter(Context context, boolean update, boolean userComment) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        isUpdate = update;
        isUserComment = userComment;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        CommentViewHolder cvh = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_comment, null);
            cvh = new CommentViewHolder();
            cvh.comment_ll = (LinearLayout) convertView.findViewById(R.id.comment_ll);
            cvh.user_name = (TextView) convertView.findViewById(R.id.tv_user_name);
            cvh.user_photo = (ImageView) convertView.findViewById(R.id.img_user_photo);
            cvh.comment_content = (TextView) convertView.findViewById(R.id.tv_comment_content);
            cvh.comment_time = (TextView) convertView.findViewById(R.id.tv_comment_time);
            convertView.setTag(cvh);
        } else {
            cvh = (CommentViewHolder) convertView.getTag();
        }

        final CommentBean commentBean = (CommentBean) mCommentList.get(position);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = df.format(commentBean.getDate());
        cvh.comment_time.setText(strDate);
        cvh.comment_content.setText(commentBean.getContent());

        if (isUserComment) {
            cvh.comment_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDishFromComment(commentBean);
                }
            });

            cvh.comment_ll.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final CommentBean commentBean = (CommentBean) mCommentList.get(position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Confirm delete");
                    builder.setIcon(android.R.drawable.ic_dialog_info);
                    builder.setMessage("Do you really want to delete the comment ?");
                    builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteUserComment(commentBean);
                        }
                    });
                    builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.create().show();
                    return true;
                }
            });
        } else {
            cvh.comment_ll.setEnabled(false);
        }

        return convertView;
    }

    public void deleteUserComment(final CommentBean commentBean) {
        String url = "comments/" + commentBean.get_id();
        NourritureRestClient.delete(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("delete user comment", response.toString());
                if (statusCode == 204) {
                    mCommentList.remove(commentBean);
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(mContext, "Deleting comment is wrong. Please try it again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(mContext, "Deleting comment is wrong. Please try it again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getDishFromComment(CommentBean commentBean) {
        String url = "dishes/" + commentBean.getDish();
        NourritureRestClient.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("get dish by comment", response.toString());
                if (statusCode == 200) {
                    try {
                        dishBean = (DishBean) JsonTobean.getList(DishBean[].class, response.toString()).get(0);
                        Intent intent = new Intent(mContext, DishDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("dishBean", dishBean);
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(mContext, "Network connection is wrong.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(mContext, "Network connection is wrong.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public Object getItem(int position) {
        return mCommentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mCommentList.size();
    }

    static class CommentViewHolder {
        TextView user_name;
        TextView comment_content;
        TextView comment_time;
        ImageView user_photo;
        LinearLayout comment_ll;
    }
}
