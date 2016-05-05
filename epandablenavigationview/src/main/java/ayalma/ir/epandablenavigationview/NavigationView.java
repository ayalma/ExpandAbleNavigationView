package ayalma.ir.epandablenavigationview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by alimohammadi on 5/3/16.
 *
 * @author alimohammadi.
 */
public class NavigationView extends RecyclerView {

    public NavigationView(Context context) {
        super(context);
        initNavigationView(null);
    }

    public NavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initNavigationView(attrs);
    }

    public NavigationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initNavigationView(attrs);
    }

    private void initNavigationView(AttributeSet attrs) {
        setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        TypedArray a = getContext().obtainStyledAttributes(attrs,R.styleable.NavigationView);
        setClipToPadding(false);

        for (int i = 0; i < a.getIndexCount(); i++)
        {
            int attr = a.getIndex(i);

            if (attr == R.styleable.NavigationView_headerLayout)
            {
                int header = a.getResourceId(attr,-1);
                if (header!=-1)
                {
                    setHeader(header);

                    //add this line for removing extra space from header top.
                    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        addItemDecoration(new SpaceItemDecoration((int) getResources().getDimension(R.dimen.status_barHeight)));
                }
            }

        }

        a.recycle();

    }

    public void setMenu(int resId) {

        Menu menu = new MenuBuilder(getContext());
        new MenuInflater(getContext()).inflate(resId, menu);

        if (getAdapter() == null)
            setAdapter(new Adapter());
        ((Adapter) getAdapter()).setItems(menu);
    }

    public void setHeader(int resId)
    {
       if (getAdapter() == null)
           setAdapter(new Adapter());

        ((Adapter) getAdapter()).setHeader(resId);

    }

    private static class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Menu items = null;
        private int headerView = 0;

        private SparseBooleanArray expanded = new SparseBooleanArray();

        boolean isExpanded(int group) {
            return expanded.get(group);
        }

        public SparseBooleanArray getExpanded() {
            return expanded;
        }

        public void setExpanded(SparseBooleanArray expanded) {
            this.expanded = expanded;
        }

        public void expand(int group) {
            if (isExpanded(group))
                return;

            int postion = 0;
            for (int i = 0; i < group; i++) {
                postion++;
                if (items.getItem(i).hasSubMenu() && isExpanded(i))
                    postion += getChildItemCount(i);
            }

            postion++;
            notifyItemRangeInserted(postion, getChildItemCount(group)); // notify recycler view for expanding
            expanded.put(group, true); // save expanding in sparce array
        }

        private int getChildItemCount(int group) {
            return items.getItem(group).getSubMenu().size();
        }

        public void collapse(int group) {
            if (!isExpanded(group)) // if is not expanded . so nothing to collapse.
                return;

            int postion = 0;
            for (int i = 0; i < group; i++) {
                postion++;
                if (items.getItem(i).hasSubMenu() && isExpanded(i))
                    postion += getChildItemCount(i);
            }

            postion++;

            notifyItemRangeRemoved(postion, getChildItemCount(group)); // notify recycler view for expanding
            expanded.put(group, false); // save expanding in sparce array
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;

            if (viewType == ViewType.Header.getValue()) {
                view = LayoutInflater.from(parent.getContext()).inflate(headerView, parent, false);
                return new HeaderViewHolder(view);
            } else if (viewType == ViewType.Group.getValue()) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_group_row, parent, false);
                return new GroupViewHolder(view);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_item_row, parent, false);
                return new ItemViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            if (headerView != 0 && position == 0)
                return;

            if (headerView != 0)
                position--;

            for (int group = 0; group <= items.size(); ) {
                if (items.getItem(group).hasSubMenu()) {
                    if (position > 0 && !isExpanded(group)) {
                        position--;
                        group++;
                    } else if (position > 0) {
                        position--;
                        if (position < getChildItemCount(group)) {
                            Log.d("TAG", "pos:" + position + "group:" + group);
                            onBindItemViewHolder((ItemViewHolder) holder, group, position);
                            return;
                        }
                        position -= getChildItemCount(group);
                        group++;
                    } else {
                        Log.d("TAG", "pos:" + position + "group:" + group);
                        onBindGruopViewHolder((GroupViewHolder) holder, group);
                        return;
                    }
                } else if (position > 0) {
                    group++;
                    position--;
                } else {
                    Log.d("TAG", "pos:" + position + "group:" + group);
                    onBindItemViewHolder((ItemViewHolder) holder, group, position);
                    return;
                }
            }

            throw new IndexOutOfBoundsException();
        }

        private void onBindGruopViewHolder(final GroupViewHolder holder, final int group) {
            holder.tv.setText(items.getItem(group).getTitle());
            if (items.getItem(group).getIcon() != null)
                holder.iv.setImageDrawable(items.getItem(group).getIcon());
            else holder.iv.setVisibility(GONE);

            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isExpanded(group)) collapse(group);
                    else expand(group);
                }
            });
        }

        private void onBindItemViewHolder(ItemViewHolder holder, int group, int child) {
            holder.tv.setText(getChildItem(group, child).getTitle());
            holder.iv.setImageDrawable(getChildItem(group, child).getIcon());
        }

        private MenuItem getChildItem(int group, int child) {
            if (items.getItem(group).hasSubMenu())
                return items.getItem(group).getSubMenu().getItem(child);
            else return items.getItem(group);
        }

        @Override
        public int getItemCount() {
            int size = 0;

            if (headerView !=0 )
                size++;

            for (int i = 0; i < items.size(); i++) {
                size++;
                if (items.getItem(i).hasSubMenu() && isExpanded(i))
                    size += getChildItemCount(i);
            }
            return size;
        }

        private MenuItem getItem(int postion) {

            return items.getItem(postion);
        }

        @Override
        public int getItemViewType(int position) {
            if (headerView != 0 && position == 0)
                return ViewType.Header.getValue();

            if (headerView != 0)
                position--;

            for (int group = 0; group <= items.size(); ) {
                if (items.getItem(group).hasSubMenu()) {
                    if (position > 0 && !isExpanded(group)) {
                        position--;
                        group++;
                    } else if (position > 0) {
                        position--;
                        if (position < getChildItemCount(group))
                            return ViewType.Item.getValue();
                        position -= getChildItemCount(group);
                        group++;
                    } else return ViewType.Group.getValue();
                } else if (position > 0) {
                    group++;
                    position--;
                } else return ViewType.Item.getValue();
            }

            throw new IndexOutOfBoundsException();
        }

        public Menu getItems() {
            return items;
        }

        public void setItems(Menu items) {
            this.items = items;
        }

        public void setHeader(int headerView) {
            this.headerView = headerView;
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class GroupViewHolder extends RecyclerView.ViewHolder {

        private TextView tv;
        private ImageView iv;
        private ImageView indicatorIv;

        private boolean expanded;

        public GroupViewHolder(View itemView) {
            super(itemView);

            tv = (TextView) itemView.findViewById(R.id.nav_group_itemText);
            iv = (ImageView) itemView.findViewById(R.id.nav_group_itemIcon);
            indicatorIv = (ImageView) itemView.findViewById(R.id.nav_groupExpandedIndicator);
        }

        public void expand() {
            expanded = true;
        }

        public void collapse() {
            expanded = false;
        }

        public boolean isExpanded() {
            return expanded;
        }

        public void setExpanded(boolean expanded) {
            this.expanded = expanded;
        }


    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tv;
        private ImageView iv;

        public ItemViewHolder(View itemView) {
            super(itemView);

            tv = (TextView) itemView.findViewById(R.id.nav_itemText);
            iv = (ImageView) itemView.findViewById(R.id.nav_itemIcon);
        }
    }


}
