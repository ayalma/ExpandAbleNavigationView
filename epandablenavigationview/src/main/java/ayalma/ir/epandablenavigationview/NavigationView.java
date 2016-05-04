package ayalma.ir.epandablenavigationview;

import android.content.Context;
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

    }

    public void setMenu(int resId) {

        Menu menu = new MenuBuilder(getContext());
        new MenuInflater(getContext()).inflate(resId, menu);

        if (getAdapter() == null)
            setAdapter(new Adapter());
        ((Adapter) getAdapter()).setItems(menu);
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

            if (viewType == ViewHolderType.Header.getValue()) {
                view = LayoutInflater.from(parent.getContext()).inflate(headerView, parent, false);
                return new HeaderViewHolder(view);
            } else if (viewType == ViewHolderType.Group.getValue()) {
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

            for (int group = 0; group <= items.size(); ) {
                if (items.getItem(group).hasSubMenu()) {
                    if (position > 0 && !isExpanded(group)) {
                        position--;
                        group++;
                        continue;
                    } else if (position > 0) {
                        position--;
                        if (position < getChildItemCount(group)) {
                            Log.d("TAG", "pos:" + position + "group:" + group);
                            onBindItemViewHolder((ItemViewHolder) holder, group, position);
                            return;
                        }
                        position -= getChildItemCount(group);
                        group++;
                        continue;
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
            holder.tv.setText("group:" + group);

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
                return ViewHolderType.Header.getValue();

            for (int group = 0; group <= items.size(); ) {
                if (items.getItem(group).hasSubMenu()) {
                    if (position > 0 && !isExpanded(group)) {
                        position--;
                        group++;
                        continue;
                    } else if (position > 0) {
                        position--;
                        if (position < getChildItemCount(group))
                            return ViewHolderType.Item.getValue();
                        position -= getChildItemCount(group);
                        group++;
                        continue;
                    } else return ViewHolderType.Group.getValue();
                } else if (position > 0) {
                    group++;
                    position--;
                } else return ViewHolderType.Item.getValue();
            }
           /* int group = 0;
            while (group < items.size())
            {
                if (items.getItem(group).hasSubMenu())
                {
                    if (!isExpanded(group)) {
                        Log.d("TAG","pos:"+ position +"group:" +group);
                        position--;
                        group++;
                        continue;
                    } else  {
                        Log.d("TAG","pos:"+ position +"group:" +group);
                        position--;
                        if (position < getChildItemCount(group))
                            return ViewHolderType.Item.getValue();
                        position -= getChildItemCount(group);
                        group++;
                        continue;
                    }
                }
                else
                {
                    if (position<items.size()-1)
                        return  ViewHolderType.Item.getValue();
                    else {
                        position--;
                        group++;
                    }

                }
            }*/
            throw new IndexOutOfBoundsException();
        }

        public Menu getItems() {
            return items;
        }

        public void setItems(Menu items) {
            this.items = items;
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
