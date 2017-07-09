package tk.kejie.dcrawler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collection;

public class BlogItemViewAdapter extends BaseRecyclerViewAdapter<BlogModel, BlogItemViewAdapter.ViewHolder> {
    private static final String TAG = "BlogItemViewAdapter";

    private Context context;

    public BlogItemViewAdapter(Context context) {
        this.context = context;
    }

    public BlogItemViewAdapter(int capacity, Context context) {
        super(capacity);
        this.context = context;
    }

    public BlogItemViewAdapter(Collection<? extends BlogModel> collection, Context context) {
        super(collection);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.blog_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final BlogModel item = get(position);
        final TextView title = holder.title;
        final TextView describe = holder.describe;

        title.setText(item.getTitle().trim().replace(" ", "").replace("\\n", ""));
        describe.setText(item.getDescribe());

        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(BlogItemViewAdapter.this.get(position));
                }
            });
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView describe;
        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            describe = (TextView) itemView.findViewById(R.id.tv_describe);
        }
    }


    /**
     * item 点击事件
     */
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(BlogModel blog);
    }
}