/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.crafter.engine.util.breadcrumb;

import org.apache.commons.lang.StringUtils;
import org.craftercms.crafter.core.service.CachingOptions;
import org.craftercms.crafter.core.service.ContentStoreService;
import org.craftercms.crafter.core.service.Context;
import org.craftercms.crafter.core.service.Item;
import org.craftercms.crafter.core.util.cache.CacheCallback;
import org.craftercms.crafter.core.util.cache.CacheTemplate;
import org.craftercms.crafter.core.util.cache.impl.CachingAwareList;
import org.craftercms.crafter.engine.servlet.filter.AbstractSiteContextResolvingFilter;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

/**
 * Helper class to create navigation breadcrumbs.
 *
 * @author Alfonso Vásquez
 */
public class BreadcrumbBuilder {

    public static final String BREADCRUMB_CONST_KEY_ELEM = "breadcrumb";
    public static final String HOME_BREADCRUMB_NAME = "Home";

    protected CacheTemplate cacheTemplate;
    protected CachingOptions cachingOptions;
    protected ContentStoreService storeService;
    protected String homePath;
    protected String breadcrumbNameXPathQuery;

    public BreadcrumbBuilder() {
        cachingOptions = CachingOptions.DEFAULT_CACHING_OPTIONS;
    }

    @Required
    public void setCacheTemplate(CacheTemplate cacheTemplate) {
        this.cacheTemplate = cacheTemplate;
    }

    public void setCachingOptions(CachingOptions cachingOptions) {
        this.cachingOptions = cachingOptions;
    }

    @Required
    public void setStoreService(ContentStoreService storeService) {
        this.storeService = storeService;
    }

    @Required
    public void setHomePath(String homePath) {
        this.homePath = homePath;
    }

    @Required
    public void setBreadcrumbNameXPathQuery(String breadcrumbNameXPathQuery) {
        this.breadcrumbNameXPathQuery = breadcrumbNameXPathQuery;
    }

    public List<BreadcrumbItem> buildBreadcrumb(final String url) {
        final Context context = AbstractSiteContextResolvingFilter.getCurrentContext().getContext();

        return cacheTemplate.execute(context, cachingOptions, new CacheCallback<List<BreadcrumbItem>>() {

            @Override
            public List<BreadcrumbItem> doCacheable() {
                CachingAwareList<BreadcrumbItem> breadcrumb = new CachingAwareList<BreadcrumbItem>();
                String breadcrumbUrl = StringUtils.substringBeforeLast(StringUtils.substringAfter(url, homePath), "/index.xml");
                String[] breadcrumbUrlComponents = breadcrumbUrl.split("/");
                String currentUrl = homePath;

                for (String breadcrumbUrlComponent : breadcrumbUrlComponents) {
                    if (StringUtils.isNotEmpty(breadcrumbUrlComponent)) {
                        currentUrl += "/" + breadcrumbUrlComponent;
                    }

                    Item item = storeService.getItem(context, currentUrl + "/index.xml");
                    String breadcrumbName = item.queryDescriptorValue(breadcrumbNameXPathQuery);
                    if (StringUtils.isEmpty(breadcrumbName)) {
                        if (StringUtils.isNotEmpty(breadcrumbUrlComponent)) {
                            breadcrumbName = StringUtils.capitalize(breadcrumbUrlComponent.replace("-", " ").replace(".xml", ""));
                        } else {
                            breadcrumbName = HOME_BREADCRUMB_NAME;
                        }
                    }

                    breadcrumb.add(new BreadcrumbItem(currentUrl, breadcrumbName));
                    breadcrumb.addDependencyKey(item.getKey());
                }

                return breadcrumb;
            }

        }, url, BREADCRUMB_CONST_KEY_ELEM);
    }

}