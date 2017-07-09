package tk.kejie.dcrawler;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public abstract class BaseRecyclerViewAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> implements List<T> {
    private final Object lock = new Object();
    private final List<T> list;

    public BaseRecyclerViewAdapter() {
        list = new ArrayList<T>();
    }

    public BaseRecyclerViewAdapter(int capacity){
        list = new ArrayList<T>(capacity);
    }

    public BaseRecyclerViewAdapter(Collection<? extends T> collection) {
        this.list = new ArrayList<T>(collection);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void add(int location, T object) {
        synchronized (lock) {
            list.add(location, object);
            notifyItemInserted(location);
        }
    }

    @Override
    public boolean add(T object) {
        synchronized (lock) {
            if (list.add(object)) {
                int position = list.indexOf(object);
                notifyItemInserted(position);
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        synchronized (lock) {
            int lastIndex = list.size();
            if (list.addAll(collection)) {
                notifyItemRangeInserted(lastIndex, collection.size());
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean addAll(int location, Collection<? extends T> collection) {
        synchronized (lock) {
            if (list.addAll(location, collection)) {
                notifyItemRangeInserted(location, collection.size());
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public void clear() {
        synchronized (lock) {
            int size = list.size();
            list.clear();
            notifyItemRangeRemoved(0, size);
        }
    }

    @Override
    public boolean contains(Object object) {
        return list.contains(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return list.contains(collection);
    }

    @Override
    public T get(int location) {
        return list.get(location);
    }

    @Override
    public int indexOf(Object object) {
        return list.indexOf(object);
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public int lastIndexOf(Object object) {
        return list.lastIndexOf(object);
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator() {
        return list.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator(int location) {
        return list.listIterator(location);
    }

    @Override
    public T remove(int location) {
        synchronized (lock) {
            T item = list.remove(location);
            notifyItemRemoved(list.indexOf(item));
            return item;
        }
    }

    @Override
    public boolean remove(Object object) {
        boolean modified = false;
        synchronized (lock) {
            if (list.contains(object)) {
                int position = list.indexOf(object);
                list.remove(position);
                notifyItemRemoved(position);
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean modified = false;
        synchronized (lock) {
            Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
                Object object = iterator.next();
                if (list.contains(object)){
                    int position = list.indexOf(object);
                    list.remove(position);
                    notifyItemRemoved(position);
                    modified = true;
                }
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean modified = false;
        synchronized (lock) {
            Iterator<T> iterator = list.iterator();
            while (iterator.hasNext()) {
                Object object = iterator.next();
                if (!collection.contains(object)) {
                    int position = list.indexOf(object);
                    list.remove(position);
                    notifyItemRemoved(position);
                    modified = true;
                }
            }
        }
        return modified;
    }

    @Override
    public T set(int location, T object) {
        synchronized (lock) {
            T item = list.set(location, object);
            notifyItemInserted(location);
            return item;
        }
    }

    @Override
    public int size() {
        return list.size();
    }

    @NonNull
    @Override
    public List<T> subList(int start, int end) {
        return list.subList(start, end);
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @NonNull
    @Override
    public <T1> T1[] toArray(T1[] array) {
        return list.toArray(array);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof List && list.equals(o);
    }
}
