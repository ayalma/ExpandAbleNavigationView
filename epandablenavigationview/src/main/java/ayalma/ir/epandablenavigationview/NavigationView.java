package ayalma.ir.epandablenavigationview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.DefaultItemAnimator;
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
        setClipToPadding(false);
        setItemAnimator(new DefaultItemAnimator());
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.NavigationView);

            for (int i = 0; i < a.getIndexCount(); i++) {
                int attr = a.getIndex(i);

                if (attr == R.styleable.NavigationView_headerLayout) {
                    int header = a.getResourceId(attr, -1);
                    if (header != -1) {
                        setHeader(header);

                        //add this line for removing extra space from header top.
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            addItemDecoration(new SpaceItemDecoration((int) getResources().getDimension(R.dimen.status_barHeight)));
                    }
                }
                if (attr == R.styleable.NavigationView_itemTextColor)
                {
                    setItemTextColor(a.getColor(attr,0));
                }
                if (attr == R.styleable.NavigationView_itemIconTint)
                {
                    setItemIconTint(a.getColor(attr,0));
                }
                if (attr == R.styleable.NavigationView_itemRippleColor)
                {
                    setItemRippleColor(a.getColor(attr,0));
                }
                if (attr == R.styleable.NavigationView_itemBackgroundColor)
                {
                    setItemBackgroundColor(a.getColor(attr,0));
                }
                //if (attr == R.styleable.NavigationView_itemTextAppearance);

            }

            a.recycle();

        }


    }

    public void setMenu(int resId) {

        Menu menu = new MenuBuilder(getContext());
        new MenuInflater(getContext()).inflate(resId, menu);

        if (getAdapter() == null)
            setAdapter(new Adapter());
        ((Adapter) getAdapter()).setItems(menu);
    }

    public void setHeader(int resId) {
        if (getAdapter() == null)
            setAdapter(new Adapter());

        ((Adapter) getAdapter()).setHeader(resId);

    }

    public void setItemTextColor(int itemTextColor) {
        if (getAdapter() == null)
            setAdapter(new Adapter());

        ((Adapter) getAdapter()).setItemTextColor(itemTextColor);
    }

    public void setItemIconTint(int itemIconTint)
    {
        if (getAdapter() == null)
            setAdapter(new Adapter());

        ((Adapter) getAdapter()).setItemIconTint(itemIconTint);
    }

    public void setItemRippleColor(int itemRippleColor)
    {
        if (getAdapter() == null)
            setAdapter(new Adapter());

        ((Adapter) getAdapter()).setItemRippleColor(itemRippleColor);
    }

    public void setItemBackgroundColor(int itemBackgroundColor) {
        if (getAdapter() == null)
            setAdapter(new Adapter());

        ((Adapter) getAdapter()).setItemBackgroundColor(itemBackgroundColor);
    }

    private static class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Menu items = null;
        private int headerView = 0;

        private SparseBooleanArray expanded = new SparseBooleanArray();
        private int itemTextColor;
        private int itemIconTint;
        private int itemRippleColor;
        private int itemBackgroundColor;

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

            postion += 2;
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

            postion += 2;

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
            holder.setExpanded(isExpanded(group));

            if (itemBackgroundColor !=0 && itemRippleColor!=0)
                Util.setRippleColor(itemRippleColor, itemBackgroundColor,holder.itemView);

            if (itemIconTint !=0)
                holder.indicatorIv.setColorFilter(itemIconTint);

            if (itemTextColor!=0)
                holder.tv.setTextColor(itemTextColor);

            if (itemIconTint!=0)
                holder.iv.setColorFilter(itemIconTint);

            holder.tv.setText(items.getItem(group).getTitle());
            if (items.getItem(group).getIcon() != null)
                holder.iv.setImageDrawable(items.getItem(group).getIcon());
            else holder.iv.setVisibility(GONE);

            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isExpanded(group)) {
                        collapse(group);
                        holder.collapse();
                    } else {
                        expand(group);
                        holder.expand();
                    }
                }
            });
        }

        private void onBindItemViewHolder(ItemViewHolder holder, int group, int child) {

            if (itemBackgroundColor !=0 && itemRippleColor!=0)
                Util.setRippleColor(itemRippleColor, itemBackgroundColor,holder.itemView);

            if (itemTextColor!=0)
                holder.tv.setTextColor(itemTextColor);

            if (itemIconTint!=0)
                holder.iv.setColorFilter(itemIconTint);

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

            if (headerView != 0)
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

        public void setItemTextColor(int itemTextColor) {
            this.itemTextColor = itemTextColor;
        }

        public int getItemIconTint() {
            return itemIconTint;
        }

        public void setItemIconTint(int itemIconTint) {
            this.itemIconTint = itemIconTint;
        }

        public int getItemRippleColor() {
            return itemRippleColor;
        }

        public void setItemRippleColor(int itemRippleColor) {
            this.itemRippleColor = itemRippleColor;
        }

        public int getItemBackgroundColor() {
            return itemBackgroundColor;
        }

        public void setItemBackgroundColor(int itemBackgroundColor) {
            this.itemBackgroundColor = itemBackgroundColor;
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

            indicatorIv.startAnimation(Util.getRotateAnim(0f, 180f, 200));
            expanded = true;
        }

        public void collapse() {
            indicatorIv.startAnimation(Util.getRotateAnim(180f, 0f, 200));
            expanded = false;
        }

        public boolean isExpanded() {
            return expanded;
        }

        public void setExpanded(boolean expanded)
        {
            if (expanded)
                indicatorIv.startAnimation(Util.getRotateAnim(0f, 180f, 200));
            else
                indicatorIv.startAnimation(Util.getRotateAnim(180f, 0f, 200));

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
