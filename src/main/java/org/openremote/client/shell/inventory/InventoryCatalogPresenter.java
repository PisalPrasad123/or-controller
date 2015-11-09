package org.openremote.client.shell.inventory;

import com.google.gwt.core.client.GWT;
import jsinterop.annotations.JsType;
import org.openremote.client.event.RequestFailure;
import org.openremote.client.shared.JsUtil;
import org.openremote.client.shared.RequestPresenter;
import org.openremote.client.shared.View;
import org.openremote.shared.catalog.CatalogCategory;
import org.openremote.shared.catalog.CatalogItem;
import org.openremote.shared.event.client.ShowInfoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsType
public class InventoryCatalogPresenter extends RequestPresenter<View> {

    private static final Logger LOG = LoggerFactory.getLogger(InventoryCatalogPresenter.class);

    private static final CatalogItemCodec CATALOG_ITEM_CODEC= GWT.create(CatalogItemCodec.class);

    public CategorizedCatalogItem[] categorizedItems;

    public InventoryCatalogPresenter(View view) {
        super(view);
    }

    @Override
    public void attached() {
        super.attached();
        loadCatalog();
    }

    public void loadCatalog() {

        sendRequest(
            resource("catalog").get(),
            new ListResponseCallback<CatalogItem>("Load catalog", CATALOG_ITEM_CODEC) {
                @Override
                protected void onResponse(List<CatalogItem> data) {
                    categorizedItems = createCategorizedItems(data);
                    notifyPath("categorizedItems", categorizedItems);
                }

                @Override
                public void onFailure(RequestFailure requestFailure) {
                    super.onFailure(requestFailure);
                    categorizedItems = null;
                    notifyPathNull("categorizedItems");
                }
            }
        );
    }

    public void itemSelected(CatalogItem item) {
        dispatch(new ShowInfoEvent("TODO: Implement help display for selected: " + item));
    }

    protected CategorizedCatalogItem[] createCategorizedItems(List<CatalogItem> items) {
        List<CategorizedCatalogItem> list = new ArrayList<>();
        for (CatalogItem item : items) {
            CatalogCategory category = item.getCategory();

            CategorizedCatalogItem categorizedCatalogItem;
            int index = -1;

            for (int i = 0; i < list.size(); i++) {
                CategorizedCatalogItem ci = list.get(i);
                if (ci.category == category) {
                    index = i;
                    break;
                }
            }

            if (index >= 0) {
                categorizedCatalogItem = list.get(index);
            } else {
                categorizedCatalogItem = new CategorizedCatalogItem(category);
                list.add(categorizedCatalogItem);
            }
            categorizedCatalogItem.addCatalogItem(item);
        }

        Collections.sort(list, (a, b) -> JsUtil.compare(a.category.getLabel(), b.category.getLabel()));
        return list.toArray(new CategorizedCatalogItem[list.size()]);
    }
}
