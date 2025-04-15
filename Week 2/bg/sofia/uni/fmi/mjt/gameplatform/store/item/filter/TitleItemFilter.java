package bg.sofia.uni.fmi.mjt.gameplatform.store.item.filter;

import bg.sofia.uni.fmi.mjt.gameplatform.store.item.StoreItem;

public class TitleItemFilter implements  ItemFilter {
    private final String title;
    private final boolean caseSensitive;

    public TitleItemFilter(String title, boolean caseSensitive){
        this.title = title;
        this.caseSensitive = caseSensitive;
    }

    @Override
    public boolean matches(StoreItem item) {
        if(caseSensitive){
            return item.getTitle().contains(this.title);
        } else {
            return item.getTitle().toLowerCase().contains(this.title.toLowerCase());
        }
    }
}
